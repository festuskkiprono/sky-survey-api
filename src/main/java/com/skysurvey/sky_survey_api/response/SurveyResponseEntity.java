package com.skysurvey.sky_survey_api.response;

import com.skysurvey.sky_survey_api.survey.SurveyEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
@Entity
@Table(name = "responses")
public class SurveyResponseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "survey_id", nullable = false)
    private SurveyEntity survey;

    @Column(name = "email_address", nullable = false, length = 100)
    private String emailAddress;

    @Column(name = "date_responded", nullable = false)
    private LocalDateTime dateResponded;

    protected SurveyResponseEntity() {
        // JPA requires a no-arg constructor
    }

    public SurveyResponseEntity(SurveyEntity survey, String emailAddress) {
        this.survey = survey;
        this.emailAddress = emailAddress;
    }

    @PrePersist
    void onCreate() {
        this.dateResponded = LocalDateTime.now();
    }

    public Integer getId() { return id; }
    public SurveyEntity getSurvey() { return survey; }
    public String getEmailAddress() { return emailAddress; }
    public LocalDateTime getDateResponded() { return dateResponded; }
}
