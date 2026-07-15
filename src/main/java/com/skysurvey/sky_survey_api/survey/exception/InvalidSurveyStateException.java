package com.skysurvey.sky_survey_api.survey.exception;

public class InvalidSurveyStateException extends RuntimeException {
    public InvalidSurveyStateException(String message) {
        super(message);
    }
}
