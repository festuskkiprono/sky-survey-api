package com.skysurvey.sky_survey_api.question.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "question")
public class QuestionCreatedDto {
    @JacksonXmlProperty(isAttribute = true)
    private Integer id;

    private String name;

    public QuestionCreatedDto(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() { return id; }
    public String getName() { return name; }
}
