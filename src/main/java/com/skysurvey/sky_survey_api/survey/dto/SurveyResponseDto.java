package com.skysurvey.sky_survey_api.survey.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.skysurvey.sky_survey_api.survey.entity.SurveyEntity;

@JacksonXmlRootElement(localName = "survey")
public class SurveyResponseDto {
    @JacksonXmlProperty(isAttribute = true)
    private Integer id;

    private String name;

    private String description;
    @JacksonXmlProperty(isAttribute = true)
    private String status;

    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    @JacksonXmlProperty(isAttribute = true)
    private String deleted;

    public static SurveyResponseDto from(SurveyEntity entity) {
        SurveyResponseDto dto = new SurveyResponseDto();
        dto.id = entity.getId();
        dto.name = entity.getName();
        dto.description = entity.getDescription();
        dto.status = entity.getStatus();
        dto.deleted = entity.getDeletedAt() != null ? "yes" : "no";
        return dto;
    }
}
