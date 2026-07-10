package com.skysurvey.sky_survey_api.response;

import com.skysurvey.sky_survey_api.question.OptionEntity;
import com.skysurvey.sky_survey_api.question.QuestionEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * The dynamic, data-driven validation the whole design has been building toward.
 *
 * Every rule enforced here comes from the STORED question configuration --
 * max_length, min/max_selection, file_format, max_file_size -- not from code.
 * A brand-new survey with brand-new questions is validated correctly the moment
 * its rows exist. This is the Strategy pattern in spirit: one validate method
 * per question family, dispatched by the type discriminator.
 *
 * Contract: validators APPEND to the errors list and never throw. The service
 * collects ALL problems across ALL questions and rejects once, with everything --
 * a respondent fixing a long form deserves the full list, not one error per attempt.
 */
@Component
public class SubmissionValidator {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    /** The schema's own hard ceiling: the 50 MB CHECK on uploaded_files.file_size. */
    private static final long GLOBAL_MAX_FILE_BYTES = 52_428_800L;

    public void validate(QuestionEntity q, String rawValue, List<MultipartFile> fileParts,
                         List<String> errors) {
        switch (q.getType()) {
            case "short_text", "long_text" -> validateText(q, rawValue, errors);
            case "email"                   -> validateEmail(q, rawValue, errors);
            case "choice"                  -> validateChoice(q, rawValue, errors);
            case "file"                    -> validateFile(q, fileParts, errors);
            default -> errors.add("Question '" + q.getName()
                    + "' has unrecognized type '" + q.getType() + "'");
        }
    }

    // ---------- textual family ----------

    private void validateText(QuestionEntity q, String raw, List<String> errors) {
        if (isBlank(raw)) {
            if (q.isRequired()) errors.add("'" + q.getName() + "' is required");
            return; // optional + absent = fine; nothing more to check
        }
        Integer maxLength = q.getMaxLength();
        if (maxLength != null && raw.length() > maxLength) {
            errors.add("'" + q.getName() + "' exceeds the maximum length of "
                    + maxLength + " characters");
        }
    }

    private void validateEmail(QuestionEntity q, String raw, List<String> errors) {
        if (isBlank(raw)) {
            if (q.isRequired()) errors.add("'" + q.getName() + "' is required");
            return;
        }
        if (!EMAIL_PATTERN.matcher(raw.trim()).matches()) {
            errors.add("'" + q.getName() + "' must be a valid email address");
        }
    }

    // ---------- choice family ----------

    private void validateChoice(QuestionEntity q, String raw, List<String> errors) {
        if (isBlank(raw)) {
            if (q.isRequired()) errors.add("'" + q.getName() + "' is required");
            return;
        }

        // The contract's multi-select wire format: comma-joined values ("REACT,VUE").
        // LinkedHashSet: dedupes (submitting REACT,REACT counts once) while keeping order.
        Set<String> submitted = Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toCollection(LinkedHashSet::new));

        // Valid values = this question's LIVE options. Deleted options are excluded:
        // the frozen-value rule keeps them in the DB for old answers' sake, but new
        // submissions may not select them.
        Set<String> allowedValues = q.getOptions().stream()
                .filter(opt -> opt.getDeletedAt() == null)
                .map(OptionEntity::getOptionValue)
                .collect(Collectors.toSet());

        for (String value : submitted) {
            if (!allowedValues.contains(value)) {
                errors.add("'" + value + "' is not a valid option for '" + q.getName() + "'");
            }
        }

        // Cardinality rules -- all from stored config, null-safe.
        boolean allowMultiple = Boolean.TRUE.equals(q.getAllowMultiple());

        if (!allowMultiple && submitted.size() > 1) {
            errors.add("'" + q.getName() + "' accepts only one selection");
        }
        if (allowMultiple) {
            Integer min = q.getMinSelection();
            Integer max = q.getMaxSelection();
            if (min != null && submitted.size() < min) {
                errors.add("'" + q.getName() + "' requires at least " + min + " selections");
            }
            if (max != null && submitted.size() > max) {
                errors.add("'" + q.getName() + "' allows at most " + max + " selections");
            }
        }
    }

    // ---------- file family ----------

    private void validateFile(QuestionEntity q, List<MultipartFile> parts, List<String> errors) {
        List<MultipartFile> realParts = (parts == null) ? List.of()
                : parts.stream().filter(p -> !p.isEmpty()).toList();

        if (realParts.isEmpty()) {
            if (q.isRequired()) errors.add("'" + q.getName() + "' requires a file upload");
            return;
        }

        if (!Boolean.TRUE.equals(q.getAllowMultipleFiles()) && realParts.size() > 1) {
            errors.add("'" + q.getName() + "' accepts only one file");
        }

        // file_format may be a single extension ("pdf") or a comma list ("pdf,docx")
        Set<String> allowedFormats = isBlank(q.getFileFormat()) ? Set.of()
                : Arrays.stream(q.getFileFormat().split(","))
                .map(s -> s.trim().toLowerCase(Locale.ROOT).replace(".", ""))
                .collect(Collectors.toSet());

        long configuredMaxBytes = configuredMaxBytes(q);

        for (MultipartFile part : realParts) {
            String original = part.getOriginalFilename() == null ? "" : part.getOriginalFilename();

            if (!allowedFormats.isEmpty()) {
                String ext = original.lastIndexOf('.') > -1
                        ? original.substring(original.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT)
                        : "";
                if (!allowedFormats.contains(ext)) {
                    errors.add("'" + original + "' has an invalid format for '" + q.getName()
                            + "' (allowed: " + q.getFileFormat() + ")");
                }
            }

            if (configuredMaxBytes > 0 && part.getSize() > configuredMaxBytes) {
                errors.add("'" + original + "' exceeds the size limit of "
                        + q.getMaxFileSize() + " " + q.getMaxFileSizeUnit()
                        + " for '" + q.getName() + "'");
            }

            // The schema's CHECK constraint, enforced HERE so it surfaces as a polite
            // 400 instead of a raw SQL constraint violation at persist time.
            if (part.getSize() > GLOBAL_MAX_FILE_BYTES) {
                errors.add("'" + original + "' exceeds the global 50 MB upload ceiling");
            }
        }
    }

    /** Convert the (max_file_size, max_file_size_unit) pair to bytes. 0 = no per-question limit. */
    private long configuredMaxBytes(QuestionEntity q) {
        if (q.getMaxFileSize() == null) return 0;
        String unit = q.getMaxFileSizeUnit() == null ? "mb" : q.getMaxFileSizeUnit().toLowerCase(Locale.ROOT);
        long multiplier = "kb".equals(unit) ? 1024L : 1024L * 1024L;
        return q.getMaxFileSize() * multiplier;
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
