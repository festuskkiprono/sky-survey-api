package com.skysurvey.sky_survey_api.response;
import   com.skysurvey.sky_survey_api.response.FileStorage;

import com.skysurvey.sky_survey_api.question.OptionEntity;
import com.skysurvey.sky_survey_api.question.QuestionEntity;
import com.skysurvey.sky_survey_api.question.QuestionEntityRepository;
import com.skysurvey.sky_survey_api.survey.SurveyEntity;
import com.skysurvey.sky_survey_api.survey.SurveyNotFoundException;
import com.skysurvey.sky_survey_api.survey.SurveyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The summit. One method, five phases, strictly ordered:
 *
 *   1. GATE      -- survey must be visible to respondents (ACTIVE, not deleted)
 *   2. VALIDATE  -- every answer against every stored rule; collect ALL errors
 *   3. PERSIST   -- response -> answers -> answer_options (pure DB writes)
 *   4. FILES     -- bytes to disk + metadata rows (the non-transactional resource, LAST)
 *   5. RECEIPT   -- return the saved response for the 201 body
 *
 * The ordering IS the atomicity strategy. @Transactional makes phases 3-4's DB
 * writes all-or-nothing automatically. Disk writes can't join a DB transaction,
 * so they go LAST -- by the time the first byte is written, everything that can
 * be validated has passed. If a write still fails mid-way, the RuntimeException
 * rolls back every DB row, and the catch block below compensates for the disk:
 * already-written files are deleted. Net effect: a failed submission leaves NO
 * trace, in either the database or the filesystem.
 */
@Service
public class SubmissionService {

    private static final Set<String> TEXTUAL_TYPES = Set.of("short_text", "long_text", "email");

    private final SurveyRepository surveyRepository;
    private final QuestionEntityRepository questionRepository;
    private final SurveyResponseRepository responseRepository;
    private final AnswerRepository answerRepository;
    private final AnswerOptionRepository answerOptionRepository;
    private final UploadedFileRepository uploadedFileRepository;
    private final SubmissionValidator validator;
    private final FileStorage fileStorage;

    public SubmissionService(SurveyRepository surveyRepository,
                             QuestionEntityRepository questionRepository,
                             SurveyResponseRepository responseRepository,
                             AnswerRepository answerRepository,
                             AnswerOptionRepository answerOptionRepository,
                             UploadedFileRepository uploadedFileRepository,
                             SubmissionValidator validator,
                             FileStorage fileStorage) {
        this.surveyRepository = surveyRepository;
        this.questionRepository = questionRepository;
        this.responseRepository = responseRepository;
        this.answerRepository = answerRepository;
        this.answerOptionRepository = answerOptionRepository;
        this.uploadedFileRepository = uploadedFileRepository;
        this.validator = validator;
        this.fileStorage = fileStorage;
    }

    @Transactional
    public SurveyResponseEntity submit(Integer surveyId,
                                       Map<String, String> formFields,
                                       Map<String, List<MultipartFile>> fileParts) {

        // ---- Phase 1: the gate -- same visibility rule as Slice 4, reused verbatim ----
        SurveyEntity survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new SurveyNotFoundException(surveyId));
        if (!"ACTIVE".equals(survey.getStatus()) || survey.getDeletedAt() != null) {
            // The 2:00-opened / 2:10-deactivated / 2:15-submitted case, decided long ago:
            // in-flight submissions to a pulled survey are REJECTED, not silently absorbed.
            throw new SubmissionNotAcceptedException(
                    "This survey is no longer accepting responses");
        }

        // The validation universe: only questions a respondent can SEE.
        // Answers to inactive/deleted questions are ignored below (never persisted),
        // and their required flags are NOT enforced -- a hidden question can't be mandatory.
        List<QuestionEntity> questions = questionRepository
                .findBySurveyIdAndStatusAndDeletedAtIsNullOrderByDisplayOrder(surveyId, "ACTIVE");

        // ---- Phase 2: validate EVERYTHING before persisting ANYTHING ----
        List<String> errors = new ArrayList<>();
        for (QuestionEntity q : questions) {
            validator.validate(q, formFields.get(q.getName()), fileParts.get(q.getName()), errors);
        }
        if (!errors.isEmpty()) {
            throw new SubmissionValidationException(errors);
        }

        // ---- Email promotion: the email-typed answer becomes responses.email_address ----
        String email = questions.stream()
                .filter(q -> "email".equals(q.getType()))
                .map(q -> formFields.get(q.getName()))
                .filter(v -> v != null && !v.isBlank())
                .findFirst()
                .orElse(null);
        if (email == null) {
            // email_address is NOT NULL in the schema; a survey design without a
            // required email question would fail at INSERT. Fail politely instead.
            throw new SubmissionValidationException(
                    List.of("A valid email address answer is required to submit this survey"));
        }

        // ---- Phase 3: DB persist -- response, then answers, then answer_options ----
        SurveyResponseEntity response =
                responseRepository.save(new SurveyResponseEntity(survey, email.trim()));

        List<PendingFile> pendingFiles = new ArrayList<>();

        for (QuestionEntity q : questions) {
            String raw = formFields.get(q.getName());
            List<MultipartFile> parts = fileParts.get(q.getName());
            boolean hasText = raw != null && !raw.isBlank();
            boolean hasFiles = parts != null && parts.stream().anyMatch(p -> !p.isEmpty());
            if (!hasText && !hasFiles) {
                continue; // optional question left unanswered -> no answer row at all
            }

            // value_text only for the textual family; choice/file answers carry
            // their payload in their own child tables.
            String valueText = TEXTUAL_TYPES.contains(q.getType()) ? raw.trim() : null;
            AnswerEntity answer = answerRepository.save(new AnswerEntity(response, q, valueText));

            if ("choice".equals(q.getType())) {
                for (String selectedValue : raw.split(",")) {
                    OptionEntity option = resolveOption(q, selectedValue.trim());
                    answerOptionRepository.save(
                            new AnswerOptionEntity(answer.getId(), option.getId()));
                }
            }

            if ("file".equals(q.getType())) {
                // Don't write bytes yet -- queue them so ALL disk I/O happens last.
                parts.stream().filter(p -> !p.isEmpty())
                        .forEach(p -> pendingFiles.add(new PendingFile(answer, p)));
            }
        }

        // ---- Phase 4: disk writes, LAST, with compensation on failure ----
        List<String> writtenPaths = new ArrayList<>();
        try {
            for (PendingFile pending : pendingFiles) {
                String storagePath = fileStorage.store(pending.part());
                writtenPaths.add(storagePath);
                uploadedFileRepository.save(new UploadedFileEntity(
                        pending.answer(),
                        pending.part().getOriginalFilename(),
                        storagePath,
                        (int) pending.part().getSize(),
                        pending.part().getContentType()));
            }
        } catch (RuntimeException e) {
            // DB rows roll back automatically (we're inside @Transactional and
            // rethrowing). Disk has no rollback -- compensate by hand:
            writtenPaths.forEach(fileStorage::deleteQuietly);
            throw e;
        }

        // ---- Phase 5: receipt ----
        return response;
    }

    /**
     * Re-resolve a validated choice value to its OptionEntity. Validation already
     * guaranteed a live match exists, so the orElseThrow is a can't-happen guard --
     * but writing it keeps the method honest if the two code paths ever drift.
     */
    private OptionEntity resolveOption(QuestionEntity q, String value) {
        return q.getOptions().stream()
                .filter(opt -> opt.getDeletedAt() == null)
                .filter(opt -> opt.getOptionValue().equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Validated option vanished: " + value + " on " + q.getName()));
    }

    /** Tiny carrier pairing a saved answer with the multipart part awaiting storage. */
    private record PendingFile(AnswerEntity answer, MultipartFile part) {
    }
}
