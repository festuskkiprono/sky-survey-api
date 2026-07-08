package com.skysurvey.sky_survey_api.survey;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JacksonXmlRootElement(localName = "surveys")
public class SurveysListDto {

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "survey")
    private List<SurveyResponseDto> surveys;

    public SurveysListDto(List<SurveyResponseDto> surveys) {
        this.surveys = surveys;
    }

    public List<SurveyResponseDto> getSurveys() { return surveys; }
}
