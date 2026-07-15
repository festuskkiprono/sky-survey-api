package com.skysurvey.sky_survey_api.question.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

public class OptionItemDto {
    @JacksonXmlProperty(isAttribute = true)
    private String value;

    @JacksonXmlText
    private String label;

    public OptionItemDto(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String getValue() { return value; }
    public String getLabel() { return label; }
}
