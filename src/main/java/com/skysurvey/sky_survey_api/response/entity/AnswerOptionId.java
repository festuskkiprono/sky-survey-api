package com.skysurvey.sky_survey_api.response.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AnswerOptionId implements Serializable {
    @Column(name = "answer_id")
    private Integer answerId;

    @Column(name = "option_id")
    private Integer optionId;

    protected AnswerOptionId() {
    }

    public AnswerOptionId(Integer answerId, Integer optionId) {
        this.answerId = answerId;
        this.optionId = optionId;
    }

    public Integer getAnswerId() { return answerId; }
    public Integer getOptionId() { return optionId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AnswerOptionId that)) return false;
        return Objects.equals(answerId, that.answerId) && Objects.equals(optionId, that.optionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(answerId, optionId);
    }
}
