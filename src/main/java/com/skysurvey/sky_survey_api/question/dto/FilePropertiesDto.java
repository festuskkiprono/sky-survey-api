package com.skysurvey.sky_survey_api.question.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class FilePropertiesDto {
    @JacksonXmlProperty(isAttribute = true)
    private String format;

    @JacksonXmlProperty(isAttribute = true, localName = "max_file_size")
    private Integer maxFileSize;

    @JacksonXmlProperty(isAttribute = true, localName = "max_file_size_unit")
    private String maxFileSizeUnit;

    @JacksonXmlProperty(isAttribute = true)
    private String multiple;

    public FilePropertiesDto(String format, Integer maxFileSize,
                             String maxFileSizeUnit, String multiple) {
        this.format = format;
        this.maxFileSize = maxFileSize;
        this.maxFileSizeUnit = maxFileSizeUnit;
        this.multiple = multiple;
    }

    public String getFormat() { return format; }
    public Integer getMaxFileSize() { return maxFileSize; }
    public String getMaxFileSizeUnit() { return maxFileSizeUnit; }
    public String getMultiple() { return multiple; }
}
