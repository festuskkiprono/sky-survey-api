package com.skysurvey.sky_survey_api.question.entity;

import com.skysurvey.sky_survey_api.survey.entity.SurveyEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions",uniqueConstraints = {@UniqueConstraint(name = "unique_question_name_per_survey",
                        columnNames = {"survey_id", "name"}
                )}
)
public class QuestionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "survey_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_question_survey")
    )
    private  SurveyEntity survey;

    @Column(nullable = false)
    private String name;
    @Column(name = "question_text", columnDefinition = "TEXT",nullable = false)
    private  String questionText;

    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(nullable = false, length = 20)
    private String type;
    @Column(nullable = false, length = 20)
    private String status;

    @Column(nullable = false)
    private boolean required;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;
    @Column(name="created_at",nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "max_length")
    private Integer maxLength;

    @Column(name = "allow_multiple")
    private Boolean  allowMultiple;

    @Column(name = "min_selection")
    private Integer minSelection;

    @Column(name = "max_selection")
    private Integer maxSelection;

    @Column(name = "file_format", length = 50)
    private String fileFormat;

    @Column(name = "max_file_size")
    private Integer maxFileSize;

    @Column(name = "max_file_size_unit", length = 10)
    private String maxFileSizeUnit;
    @Column(name = "allow_multiple_files")
    private Boolean  allowMultipleFiles;


    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder")
    private List<OptionEntity> options = new ArrayList<>();

    public List<OptionEntity> getOptions() { return options; }
    public void addOption(OptionEntity option) {
        option.setQuestion(this);
        this.options.add(option);
    }
    @PrePersist
    void onCreate() {
        if (this.status == null) this.status = "ACTIVE";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    protected QuestionEntity() { }

    public Integer getId() {
        return id;
    }


    public SurveyEntity getSurvey() {
        return survey;
    }

    public void setSurvey(SurveyEntity survey) {
        this.survey = survey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }


    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    public Boolean getAllowMultiple() {
        return allowMultiple;
    }

    public void setAllowMultiple(Boolean allowMultiple) {
        this.allowMultiple = allowMultiple;
    }

    public Integer getMinSelection() {
        return minSelection;
    }

    public void setMinSelection(Integer minSelection) {
        this.minSelection = minSelection;
    }

    public Integer getMaxSelection() {
        return maxSelection;
    }

    public void setMaxSelection(Integer maxSelection) {
        this.maxSelection = maxSelection;
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    public Integer getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(Integer maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public String getMaxFileSizeUnit() {
        return maxFileSizeUnit;
    }

    public void setMaxFileSizeUnit(String maxFileSizeUnit) {
        this.maxFileSizeUnit = maxFileSizeUnit;
    }

    public Boolean getAllowMultipleFiles() {
        return allowMultipleFiles;
    }

    public void setAllowMultipleFiles(Boolean allowMultipleFiles) {
        this.allowMultipleFiles = allowMultipleFiles;
    }
}
