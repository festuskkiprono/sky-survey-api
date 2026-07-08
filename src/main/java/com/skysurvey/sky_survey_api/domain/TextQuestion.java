package com.skysurvey.sky_survey_api.domain;

import java.util.Set;

public class TextQuestion implements Question{
    private static final Set<String> TEXT_TYPES = Set.of("short_text", "long_text", "email");

    private final Integer id;
    private final String name;
    private final String prompt;
    private final String description;
    private final String type;
    private final boolean required;
    private final Integer maxLength;

    private TextQuestion(Builder b) {
        this.id = b.id;
        this.name = b.name;
        this.prompt = b.prompt;
        this.description = b.description;
        this.type = b.type;
        this.required = b.required;
        this.maxLength = b.maxLength;
    }

    @Override public Integer getId() { return id; }
    @Override public String getName() { return name; }
    @Override public String getPrompt() { return prompt; }
    @Override public String getDescription() { return description; }
    @Override public String getType() { return type; }
    @Override public boolean isRequired() { return required; }
    public Integer getMaxLength() { return maxLength; }

    public static class Builder {
        private Integer id;
        private String name;
        private String prompt;
        private String description;
        private String type;
        private boolean required = false;
        private Integer maxLength;

        public Builder id(Integer id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder prompt(String prompt) { this.prompt = prompt; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder type(String type) { this.type = type; return this; }
        public Builder required(boolean required) { this.required = required; return this; }
        public Builder maxLength(Integer maxLength) { this.maxLength = maxLength; return this; }

        public TextQuestion build() {
            if (name == null || name.isBlank())
                throw new IllegalStateException("Question must have a name");
            if (prompt == null || prompt.isBlank())
                throw new IllegalStateException("Question must have a prompt");
            if (type == null || !TEXT_TYPES.contains(type))
                throw new IllegalStateException("Invalid text question type: " + type);
            if (maxLength != null && maxLength <= 0)
                throw new IllegalStateException("maxLength must be positive");
            return new TextQuestion(this);
        }

    }
}
