package com.skysurvey.sky_survey_api.survey.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SurveyNotFoundException extends RuntimeException {
    public SurveyNotFoundException(Integer id) {
        super("Survey not found: " + id);
    }
}
