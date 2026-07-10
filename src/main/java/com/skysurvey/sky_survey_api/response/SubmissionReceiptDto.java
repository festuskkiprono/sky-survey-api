package com.skysurvey.sky_survey_api.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.time.LocalDateTime;

@JacksonXmlRootElement(localName = "response")
public class SubmissionReceiptDto {
    @JacksonXmlProperty(isAttribute = true)
    private final Integer id;

    @JacksonXmlProperty(isAttribute = true, localName = "survey_id")
    private final Integer surveyId;

    @JacksonXmlProperty(localName = "email_address")
    private final String emailAddress;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JacksonXmlProperty(localName = "date_responded")
    private final LocalDateTime dateResponded;

    private SubmissionReceiptDto(Integer id, Integer surveyId,
                                 String emailAddress, LocalDateTime dateResponded) {
        this.id = id;
        this.surveyId = surveyId;
        this.emailAddress = emailAddress;
        this.dateResponded = dateResponded;
    }

    public static SubmissionReceiptDto from(SurveyResponseEntity e) {
        return new SubmissionReceiptDto(
                e.getId(),
                e.getSurvey().getId(),
                e.getEmailAddress(),
                e.getDateResponded());
    }

    public Integer getId() { return id; }
    public Integer getSurveyId() { return surveyId; }
    public String getEmailAddress() { return emailAddress; }
    public LocalDateTime getDateResponded() { return dateResponded; }
}
