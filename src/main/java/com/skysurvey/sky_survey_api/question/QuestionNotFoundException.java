package com.skysurvey.sky_survey_api.question;

public class QuestionNotFoundException extends RuntimeException {
    public QuestionNotFoundException(Integer id) {
        super("Question not found: " + id);
    }
}
