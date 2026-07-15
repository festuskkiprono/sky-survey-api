package com.skysurvey.sky_survey_api.question.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.skysurvey.sky_survey_api.question.entity.OptionEntity;
import com.skysurvey.sky_survey_api.question.entity.QuestionEntity;

import java.util.ArrayList;
import java.util.List;

@JacksonXmlRootElement(localName = "question")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuestionResponseDto {
    @JacksonXmlProperty(isAttribute = true)
    private Integer id;

    @JacksonXmlProperty(isAttribute = true)
    private String name;

    @JacksonXmlProperty(isAttribute = true)
    private String type;

    @JacksonXmlProperty(isAttribute = true)
    private String required;

    @JacksonXmlProperty(localName = "text")
    private String questionText;

    private String description;

    private OptionsDto options;

    @JacksonXmlProperty(localName = "file_properties")
    private FilePropertiesDto fileProperties;

    @JacksonXmlProperty(isAttribute = true)
    private String status;

    @JacksonXmlProperty(isAttribute = true)
    private String deleted;

    public static QuestionResponseDto from(QuestionEntity e) {
        QuestionResponseDto dto = new QuestionResponseDto();
        dto.id = e.getId();
        dto.name = e.getName();
        dto.type = e.getType();
        dto.required = e.isRequired() ? "yes" : "no";
        dto.questionText = e.getQuestionText();
        dto.description = e.getDescription();
        dto.status  = e.getStatus();
        dto.deleted = e.getDeletedAt() != null ? "yes" : "no";

        if ("choice".equals(e.getType())) {
            List<OptionItemDto> items = new ArrayList<>();
            for (OptionEntity o : e.getOptions()) {
                if (o.getDeletedAt() == null) {
                    items.add(new OptionItemDto(o.getOptionValue(), o.getLabel()));
                }
            }
            String multiple = Boolean.TRUE.equals(e.getAllowMultiple()) ? "yes" : "no";
            dto.options = new OptionsDto(multiple, items);
        }

        if ("file".equals(e.getType())) {
            dto.fileProperties = new FilePropertiesDto(
                    e.getFileFormat(),
                    e.getMaxFileSize(),
                    e.getMaxFileSizeUnit(),
                    Boolean.TRUE.equals(e.getAllowMultipleFiles()) ? "yes" : "no");
        }
        return dto;
    }

    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getRequired() { return required; }
    public String getQuestionText() { return questionText; }
    public String getDescription() { return description; }
    public OptionsDto getOptions() { return options; }
    public FilePropertiesDto getFileProperties() { return fileProperties; }
    public String getStatus()  { return status; }
    public String getDeleted() { return deleted; }
}
