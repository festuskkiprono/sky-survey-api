package com.skysurvey.sky_survey_api.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class QuestionRequest {

    @Setter(AccessLevel.NONE)
    private Integer id;
    private String name;
    private String prompt;
    private String description;
    private String type;
    private boolean required;

    //For text questions
    private Integer maxLength;

    //For choice questions
    private boolean allowMultiple;
    private Integer minSelection;
    private Integer maxSelection;
    private List<Option> options;

    //For file questions
    private Integer maxFileSize;
    private String maxFileSizeUnit;
    private boolean allowMultipleFiles;
    private String fileFormat;



}
