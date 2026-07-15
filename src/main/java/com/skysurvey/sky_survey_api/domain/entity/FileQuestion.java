package com.skysurvey.sky_survey_api.domain.entity;

import com.skysurvey.sky_survey_api.domain.repository.Question;

public class FileQuestion implements Question {
    private final Integer id;
    private final String name;
    private final String prompt;
    private final String description;
    private final boolean required;

    private final  Integer maxFileSize;
    private final  String maxFileSizeUnit;
    private final  boolean allowMultipleFiles;
    private final  String fileFormat;


    private FileQuestion(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.prompt = builder.prompt;
        this.description = builder.description;
        this.required = builder.required;
        this.fileFormat = builder.fileFormat;
        this.maxFileSize = builder.maxFileSize;
        this.maxFileSizeUnit = builder.maxFileSizeUnit;
        this.allowMultipleFiles = builder.allowMultipleFiles;



    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPrompt() {
        return prompt;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getType() {
        return "file";
    }
    @Override
    public boolean isRequired() {
        return required;
    }
    public final String getFileFormat() { return fileFormat; }
    public final Integer getMaxFileSize() { return maxFileSize; }
    public final String getMaxFileSizeUnit() { return maxFileSizeUnit; }
    public final boolean isAllowMultipleFiles() { return allowMultipleFiles; }

    public static class Builder {
        private Integer id;
        private String name;
        private String prompt;
        private String description;

        private boolean required;

        private Integer maxFileSize;
        private String maxFileSizeUnit;
        private boolean allowMultipleFiles;
        private String fileFormat;

        public Builder fileFormat(String fileFormat) {
            this.fileFormat = fileFormat;
            return this;
        }

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        public Builder prompt(String prompt) {
            this.prompt = prompt;
            return this;
        }
        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder required(boolean required) {
            this.required = required;
            return this;
        }
        public Builder maxFileSize(Integer maxFileSize) {
            this.maxFileSize = maxFileSize;
            return this;
        }
        public Builder maxFileSizeUnit(String maxFileSizeUnit) {
            this.maxFileSizeUnit = maxFileSizeUnit;
            return this;
        }
        public Builder allowMultipleFiles(boolean allowMultipleFiles) {
            this.allowMultipleFiles = allowMultipleFiles;
            return this;
        }

        public FileQuestion build() {
            if (name == null || name.isBlank())
                throw new IllegalStateException("Question must have a name");
            if (prompt == null || prompt.isBlank())
                throw new IllegalStateException("Question must have a prompt");


            if (maxFileSize != null && maxFileSizeUnit == null)
                throw new IllegalStateException("maxFileSizeUnit required when maxFileSize is set");
            if (maxFileSize == null && maxFileSizeUnit != null)
                throw new IllegalStateException("maxFileSize required when maxFileSizeUnit is set");

            if (maxFileSize != null) {
                if (maxFileSize <= 0)
                    throw new IllegalStateException("maxFileSize must be positive");
                if (!maxFileSizeUnit.equals("kb") && !maxFileSizeUnit.equals("mb"))
                    throw new IllegalStateException("maxFileSizeUnit must be 'kb' or 'mb'");

                long bytes = maxFileSize * (maxFileSizeUnit.equals("kb") ? 1_024L : 1_048_576L);
                if (bytes > 52_428_800L)
                    throw new IllegalStateException("File size limit cannot exceed 50 MB");
            }
            return new FileQuestion(this);



        }
    }

}
