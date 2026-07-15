package com.skysurvey.sky_survey_api.response.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "answer_options")
public class AnswerOptionEntity {
    @EmbeddedId
    private AnswerOptionId id;

    protected AnswerOptionEntity() {
    }

    public AnswerOptionEntity(Integer answerId, Integer optionId) {
        this.id = new AnswerOptionId(answerId, optionId);
    }

    public AnswerOptionId getId() { return id; }
}
