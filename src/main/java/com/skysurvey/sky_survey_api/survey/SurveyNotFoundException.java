package com.skysurvey.sky_survey_api.survey;

public class SurveyNotFoundException extends RuntimeException {
    public SurveyNotFoundException(Integer id) {
        super("Survey not found: " + id);
    }
}
