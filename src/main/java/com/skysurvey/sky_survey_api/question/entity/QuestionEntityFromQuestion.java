package com.skysurvey.sky_survey_api.question.entity;

import com.skysurvey.sky_survey_api.domain.entity.ChoiceQuestion;
import com.skysurvey.sky_survey_api.domain.entity.FileQuestion;
import com.skysurvey.sky_survey_api.domain.entity.Option;
import com.skysurvey.sky_survey_api.domain.entity.TextQuestion;
import com.skysurvey.sky_survey_api.domain.repository.Question;
import com.skysurvey.sky_survey_api.survey.entity.SurveyEntity;

public class QuestionEntityFromQuestion {
    public QuestionEntityFromQuestion() {
    }

    public static QuestionEntity createQuestionEntity(Question question, SurveyEntity surveyEntity) {
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setSurvey(surveyEntity);
        questionEntity.setName(question.getName());
        questionEntity.setQuestionText(question.getPrompt());
        questionEntity.setDescription(question.getDescription());
        questionEntity.setType(question.getType());
        questionEntity.setRequired(question.isRequired());
        switch (question) {
            case TextQuestion t -> questionEntity.setMaxLength(t.getMaxLength());

            case ChoiceQuestion c -> {
                questionEntity.setAllowMultiple(c.isAllowMultiple());
                questionEntity.setMinSelection(c.getMinSelection());
                questionEntity.setMaxSelection(c.getMaxSelection());
                int order = 1;
                for (Option o : c.getOptions()) {
                    OptionEntity oe = new OptionEntity();
                    oe.setOptionValue(o.getValue());
                    oe.setLabel(o.getLabel());
                    oe.setDisplayOrder(order++);
                    questionEntity.addOption(oe);
                }
            }

            case FileQuestion f -> {
                questionEntity.setFileFormat(f.getFileFormat());
                questionEntity.setMaxFileSize(f.getMaxFileSize());
                questionEntity.setMaxFileSizeUnit(f.getMaxFileSizeUnit());
                questionEntity.setAllowMultipleFiles(f.isAllowMultipleFiles());
            }

            default -> throw new IllegalStateException(
                    "Unknown question type: " + question.getClass().getName());
        }
        return  questionEntity;
    }
}
