package com.skysurvey.sky_survey_api.survey.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

@JacksonXmlRootElement(localName = "message")
public class SurveyActivatedDeactivatedDto {
    @JacksonXmlText
    private final String text;

    public SurveyActivatedDeactivatedDto(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
