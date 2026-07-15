package com.skysurvey.sky_survey_api.question.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

public class OptionsDto {
    @JacksonXmlProperty(isAttribute = true)
    private String multiple;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "option")
    private List<OptionItemDto> options;

    public OptionsDto(String multiple, List<OptionItemDto> options) {
        this.multiple = multiple;
        this.options = options;
    }

    public String getMultiple() { return multiple; }
    public List<OptionItemDto> getOptions() { return options; }
}
