package com.skysurvey.sky_survey_api.domain;
import com.skysurvey.sky_survey_api.question.QuestionEntity;
import com.skysurvey.sky_survey_api.question.OptionEntity;
public class QuestionFactory {

    public static Question createQuestion(QuestionRequest questionRequest) {

        switch(questionRequest.getType().toLowerCase()) {
            case "short_text":
            case "email":
            case "long_text":
                return new TextQuestion.Builder()
                        .id(questionRequest.getId())
                        .name(questionRequest.getName())
                        .prompt(questionRequest.getPrompt())
                        .description(questionRequest.getDescription())
                        .type(questionRequest.getType())
                        .required(questionRequest.isRequired())
                        .maxLength(questionRequest.getMaxLength())
                        .build();
            case "choice":
                ChoiceQuestion.Builder choiceQuestionBuilder = new ChoiceQuestion.Builder()
                        .id(questionRequest.getId())
                        .name(questionRequest.getName())
                        .prompt(questionRequest.getPrompt())
                        .description(questionRequest.getDescription())
                        .required(questionRequest.isRequired());

                        if(questionRequest.isAllowMultiple()){
                            choiceQuestionBuilder.allowMultiple(questionRequest.getMinSelection(),
                                    questionRequest.getMaxSelection());
                        }

                if (questionRequest.getOptions() != null) {
                    for (Option option : questionRequest.getOptions()) {
                        choiceQuestionBuilder.addOption(option.getValue(), option.getLabel());
                    }
                }

                        return choiceQuestionBuilder.build();
            case "file":
                return new FileQuestion.Builder()
                        .id(questionRequest.getId())
                        .name(questionRequest.getName())
                        .prompt(questionRequest.getPrompt())
                        .description(questionRequest.getDescription())
                        .required(questionRequest.isRequired())
                        .fileFormat(questionRequest.getFileFormat())
                        .maxFileSize(questionRequest.getMaxFileSize())
                        .maxFileSizeUnit(questionRequest.getMaxFileSizeUnit())
                        .allowMultipleFiles(questionRequest.isAllowMultipleFiles())
                        .build();

            default:
                throw new IllegalArgumentException(
                        "Unsupported question type: " + questionRequest.getType()
                );

        }


    }
    public static Question fromQuestionEntity(QuestionEntity e) {
        return switch (e.getType()) {
            case "short_text", "long_text", "email" -> new TextQuestion.Builder()
                    .id(e.getId())
                    .name(e.getName())
                    .prompt(e.getQuestionText())
                    .description(e.getDescription())
                    .type(e.getType())
                    .required(e.isRequired())
                    .maxLength(e.getMaxLength())
                    .build();

            case "choice" -> {
                ChoiceQuestion.Builder b = new ChoiceQuestion.Builder()
                        .id(e.getId())
                        .name(e.getName())
                        .prompt(e.getQuestionText())
                        .description(e.getDescription())
                        .required(e.isRequired());

                if (Boolean.TRUE.equals(e.getAllowMultiple())) {
                    b.allowMultiple(e.getMinSelection(), e.getMaxSelection());
                }
                for (OptionEntity o : e.getOptions()) {
                    if (o.getDeletedAt() == null) {
                        b.addOption(o.getOptionValue(), o.getLabel());
                    }
                }
                yield b.build();
            }

            case "file" -> new FileQuestion.Builder()
                    .id(e.getId())
                    .name(e.getName())
                    .prompt(e.getQuestionText())
                    .description(e.getDescription())
                    .required(e.isRequired())
                    .fileFormat(e.getFileFormat())
                    .maxFileSize(e.getMaxFileSize())
                    .maxFileSizeUnit(e.getMaxFileSizeUnit())
                    .allowMultipleFiles(Boolean.TRUE.equals(e.getAllowMultipleFiles()))
                    .build();

            default -> throw new IllegalStateException(
                    "Unknown question type in database: " + e.getType());
        };
    }
}
