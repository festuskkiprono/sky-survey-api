package com.skysurvey.sky_survey_api.response;

import com.skysurvey.sky_survey_api.question.OptionEntity;
import com.skysurvey.sky_survey_api.question.QuestionEntity;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Turns ONE stored response back into the contract's <question_response> shape --
 * the read-time reversal of everything Slice 5 wrote.
 *
 * Per answer, by family (the same type dispatch as the validator, mirrored):
 *   text/email -> element named after the question, content = value_text
 *   choice     -> element named after the question, content = comma-joined
 *                 option VALUES ("REACT,VUE"), rebuilt from answer_options rows
 *   file       -> element named after the question containing <certificate id>
 *                 children, rebuilt from uploaded_files rows
 *
 * Two design rules cash in here:
 *   - FROZEN VALUES: option values are resolved from the question's option list
 *     INCLUDING soft-deleted options -- an old answer that selected a since-
 *     deleted option still renders its value. This is exactly why options are
 *     soft-deleted, witnessed at last.
 *   - EMAIL PROMOTION never mattered here (the email answer renders like any
 *     text answer); it exists for the ?email= FILTER, one layer up.
 *
 * MUST run inside an open transaction (answers -> question is LAZY);
 * ResponseQueryService provides it via @Transactional(readOnly = true).
 */
@Component
public class ResponseAssembler {

    /** The contract's date shape: space-separated, not ISO's 'T'. */
    private static final DateTimeFormatter CONTRACT_DATE =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final AnswerRepository answerRepository;
    private final AnswerOptionRepository answerOptionRepository;
    private final UploadedFileRepository uploadedFileRepository;

    public ResponseAssembler(AnswerRepository answerRepository,
                             AnswerOptionRepository answerOptionRepository,
                             UploadedFileRepository uploadedFileRepository) {
        this.answerRepository = answerRepository;
        this.answerOptionRepository = answerOptionRepository;
        this.uploadedFileRepository = uploadedFileRepository;
    }

    public QuestionResponseXmlDto assemble(SurveyResponseEntity response) {
        // LinkedHashMap = element order is insertion order.
        LinkedHashMap<String, Object> elements = new LinkedHashMap<>();
        elements.put("response_id", response.getId());

        List<AnswerEntity> answers = answerRepository.findByResponseId(response.getId())
                .stream()
                .sorted(Comparator.comparing(a -> a.getQuestion().getDisplayOrder()))
                .toList();

        for (AnswerEntity answer : answers) {
            QuestionEntity q = answer.getQuestion();
            switch (q.getType()) {
                case "short_text", "long_text", "email" ->
                        elements.put(q.getName(), answer.getValueText());

                case "choice" -> elements.put(q.getName(), joinSelectedValues(answer, q));

                case "file" -> {
                    List<CertificateDto> certs = uploadedFileRepository
                            .findByAnswerId(answer.getId()).stream()
                            .map(f -> new CertificateDto(f.getId(), f.getOriginalFilename()))
                            .toList();
                    elements.put(q.getName(), new CertificatesDto(certs));
                }

                default -> { /* unknown legacy type: omit rather than corrupt the document */ }
            }
        }

        elements.put("date_responded", response.getDateResponded().format(CONTRACT_DATE));
        return new QuestionResponseXmlDto(elements);
    }

    /**
     * answer_options rows -> "REACT,VUE". Iterating the QUESTION's option list
     * (display order) and filtering by the selected ids gives a deterministic,
     * admin-defined ordering -- and, deliberately, does NOT filter deleted_at:
     * the frozen-value rule means historical answers keep rendering.
     */
    private String joinSelectedValues(AnswerEntity answer, QuestionEntity q) {
        Set<Integer> selectedOptionIds = answerOptionRepository
                .findByIdAnswerId(answer.getId()).stream()
                .map(ao -> ao.getId().getOptionId())
                .collect(Collectors.toSet());

        return q.getOptions().stream()
                .filter(opt -> selectedOptionIds.contains(opt.getId()))
                .map(OptionEntity::getOptionValue)
                .collect(Collectors.joining(","));
    }
}
