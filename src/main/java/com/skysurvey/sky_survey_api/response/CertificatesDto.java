package com.skysurvey.sky_survey_api.response;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

public class CertificatesDto {
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "certificate")
    private final List<CertificateDto> certificates;

    public CertificatesDto(List<CertificateDto> certificates) {
        this.certificates = certificates;
    }

    public List<CertificateDto> getCertificates() { return certificates; }
}
