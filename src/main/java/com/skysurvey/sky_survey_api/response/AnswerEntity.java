package com.skysurvey.sky_survey_api.response;

import com.skysurvey.sky_survey_api.question.QuestionEntity;
import jakarta.persistence.*;


@Entity
@Table(name = "answers")
public class AnswerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "response_id", nullable = false)
    private SurveyResponseEntity response;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private QuestionEntity question;

    @Column(name = "value_text")
    private String valueText;

    protected AnswerEntity() {
    }

    public AnswerEntity(SurveyResponseEntity response, QuestionEntity question, String valueText) {
        this.response = response;
        this.question = question;
        this.valueText = valueText;
    }

    public Integer getId() { return id; }
    public SurveyResponseEntity getResponse() { return response; }
    public QuestionEntity getQuestion() { return question; }
    public String getValueText() { return valueText; }
}
