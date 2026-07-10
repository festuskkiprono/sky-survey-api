package com.skysurvey.sky_survey_api.response;

public class SubmissionNotAcceptedException extends RuntimeException {
    public SubmissionNotAcceptedException(String message) {
        super(message);
    }

}
