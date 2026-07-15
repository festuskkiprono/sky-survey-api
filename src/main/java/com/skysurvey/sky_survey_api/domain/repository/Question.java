package com.skysurvey.sky_survey_api.domain.repository;

public interface Question {
    Integer getId();
    String getName();
    String getPrompt();
    String getDescription();
    String getType();
    boolean isRequired();
}
