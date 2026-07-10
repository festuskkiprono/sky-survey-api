package com.skysurvey.sky_survey_api.response;

public class CertificateNotFoundException extends RuntimeException {
    public CertificateNotFoundException(Integer id) {
        super("Certificate with id " + id + " not found");
    }
}
