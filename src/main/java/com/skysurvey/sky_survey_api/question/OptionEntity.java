package com.skysurvey.sky_survey_api.question;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "options",
        uniqueConstraints = @UniqueConstraint(
                name = "unique_option_value_per_question",
                columnNames = {"question_id", "option_value"}))
public class OptionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_option_question"))
    private QuestionEntity question;

    @Column(name="option_value",nullable = false,length = 100)
    private String optionValue;

    @Column(name="label",nullable = false,length = 150)
    private String label;

    @Column(name = "display_order",nullable = false)
    private Integer displayOrder;

    @Column(name="deleted_at")
    private LocalDateTime deletedAt;

    public OptionEntity() {

    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Integer getId() {
        return id;
    }


    public QuestionEntity getQuestion() {
        return question;
    }

    public void setQuestion(QuestionEntity question) {
        this.question = question;
    }

    public String getOptionValue() {
        return optionValue;
    }

    public void setOptionValue(String optionValue) {
        this.optionValue = optionValue;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
