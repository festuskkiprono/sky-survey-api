package com.skysurvey.sky_survey_api.survey;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "survey")
public class SurveyResponseDto {
    @JacksonXmlProperty(isAttribute = true)
    private Integer id;

    private String name;

    private String description;

    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }

    public static SurveyResponseDto from(SurveyEntity entity) {
        SurveyResponseDto dto = new SurveyResponseDto();
        dto.id = entity.getId();
        dto.name = entity.getName();
        dto.description = entity.getDescription();
        return dto;
    }
}
