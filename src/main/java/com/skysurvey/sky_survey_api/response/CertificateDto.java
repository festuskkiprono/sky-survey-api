package com.skysurvey.sky_survey_api.response;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

public class CertificateDto {
    @JacksonXmlProperty(isAttribute = true)
    private final Integer id;

    @JacksonXmlText
    private final String name;

    public CertificateDto(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() { return id; }
    public String getName() { return name; }
}
