package com.skysurvey.sky_survey_api.domain.entity;

import com.skysurvey.sky_survey_api.domain.repository.Question;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChoiceQuestion implements Question {
    private final Integer id;
    private final String name;
    private final String prompt;
    private final String description;
    private final boolean required;

    private final boolean allowMultiple;
    private final Integer minSelection;
    private final Integer maxSelection;
    private final List<Option> options;

    private ChoiceQuestion(Builder b) {
        this.id = b.id;
        this.name = b.name;
        this.prompt = b.prompt;
        this.description = b.description;
        this.required = b.required;
        this.allowMultiple = b.allowMultiple;
        this.minSelection = b.minSelection;
        this.maxSelection = b.maxSelection;
        this.options = List.copyOf(b.options);
    }

    @Override public Integer getId() { return id; }
    @Override public String getName() { return name; }
    @Override public String getPrompt() { return prompt; }
    @Override public String getDescription() { return description; }
    @Override public String getType() { return "choice"; }
    @Override public boolean isRequired() { return required; }

    public boolean isAllowMultiple() { return allowMultiple; }
    public Integer getMinSelection() { return minSelection; }
    public Integer getMaxSelection() { return maxSelection; }
    public List<Option> getOptions() { return options; }


    public static class Builder {
        private Integer id;
        private String name;
        private String prompt;
        private String description;
        private boolean required = false;
        private boolean allowMultiple = false;
        private Integer minSelection;
        private Integer maxSelection;
        private final List<Option> options = new ArrayList<>();

        public Builder id(Integer id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder prompt(String prompt) { this.prompt = prompt; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder required(boolean required) { this.required = required; return this; }

        public Builder addOption(String value, String label) {
            options.add(new Option(value, label));
            return this;
        }

        public Builder allowMultiple(Integer min, Integer max) {
            this.allowMultiple = true;
            this.minSelection = min;
            this.maxSelection = max;
            return this;
        }

        public ChoiceQuestion build() {
            if (name == null || name.isBlank())
                throw new IllegalStateException("Question must have a name");
            if (prompt == null || prompt.isBlank())
                throw new IllegalStateException("Question must have a prompt");

            if (options.size() < 2)
                throw new IllegalStateException("Choice question needs at least 2 options");

            Set<String> values = new HashSet<>();
            for (Option o : options) {
                if (!values.add(o.getValue()))
                    throw new IllegalStateException("Duplicate option value: " + o.getValue());
            }

            if (allowMultiple) {
                if (minSelection != null && minSelection < 1)
                    throw new IllegalStateException("minSelection must be at least 1");
                if (minSelection != null && maxSelection != null && minSelection > maxSelection)
                    throw new IllegalStateException("minSelection cannot exceed maxSelection");
                if (maxSelection != null && maxSelection > options.size())
                    throw new IllegalStateException("maxSelection cannot exceed number of options");
            } else {
                if (minSelection != null || maxSelection != null)
                    throw new IllegalStateException("Selection bounds only apply to multi-select questions");
            }

            return new ChoiceQuestion(this);
        }
    }
}
