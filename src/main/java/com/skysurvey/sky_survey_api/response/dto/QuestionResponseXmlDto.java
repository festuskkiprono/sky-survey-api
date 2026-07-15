package com.skysurvey.sky_survey_api.response.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.LinkedHashMap;
import java.util.Map;

@JacksonXmlRootElement(localName = "question_response")
public class QuestionResponseXmlDto {
    private final Map<String, Object> elements;

    public QuestionResponseXmlDto(LinkedHashMap<String, Object> elements) {
        this.elements = elements;
    }

    @JsonAnyGetter
    public Map<String, Object> getElements() {
        return elements;
    }
}
