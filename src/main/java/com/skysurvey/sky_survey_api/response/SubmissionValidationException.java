package com.skysurvey.sky_survey_api.response;

import java.util.List;

public class SubmissionValidationException  extends RuntimeException{
    private final List<String> errors;

    public SubmissionValidationException(List<String> errors) {
        super("Submission failed validation: " + String.join("; ", errors));
        this.errors = List.copyOf(errors);
    }

    public List<String> getErrors() {
        return errors;
    }
}
