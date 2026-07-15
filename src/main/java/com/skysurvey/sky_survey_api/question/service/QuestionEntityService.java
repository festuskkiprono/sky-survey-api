package com.skysurvey.sky_survey_api.question.service;
import com.skysurvey.sky_survey_api.domain.entity.*;
import com.skysurvey.sky_survey_api.domain.repository.Question;
import com.skysurvey.sky_survey_api.question.entity.OptionEntity;
import com.skysurvey.sky_survey_api.question.entity.QuestionEntity;
import com.skysurvey.sky_survey_api.question.exception.QuestionNotFoundException;
import com.skysurvey.sky_survey_api.question.repository.QuestionEntityRepository;
import org.springframework.transaction.annotation.Transactional;

import com.skysurvey.sky_survey_api.survey.exception.InvalidSurveyStateException;
import com.skysurvey.sky_survey_api.survey.entity.SurveyEntity;
import com.skysurvey.sky_survey_api.survey.exception.SurveyNotFoundException;
import com.skysurvey.sky_survey_api.survey.repository.SurveyRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.skysurvey.sky_survey_api.question.entity.QuestionEntityFromQuestion.createQuestionEntity;
@Service
public class QuestionEntityService {

    private final SurveyRepository surveyRepository;
    private final QuestionEntityRepository questionEntityRepository;
    public QuestionEntityService(SurveyRepository surveyRepository,QuestionEntityRepository questionEntityRepository) {
        this.surveyRepository = surveyRepository;
        this.questionEntityRepository = questionEntityRepository;
    }

    public QuestionEntity createQuestion(Integer surveyId, QuestionRequest questionRequest) {
        SurveyEntity surveyEntity = surveyRepository.findById(surveyId).orElseThrow(()->new SurveyNotFoundException(surveyId));
        if (surveyEntity.getDeletedAt() != null)
            throw new InvalidSurveyStateException("Cannot add questions to a deleted survey");

        Question question = QuestionFactory.createQuestion(questionRequest);

        QuestionEntity questionEntity  = createQuestionEntity(question,surveyEntity);
        questionEntity.setDisplayOrder(questionEntityRepository.countBySurveyId(surveyEntity.getId())+1);
        return questionEntityRepository.save(questionEntity);
    }

    public List<QuestionEntity> findQuestionsBySurveyId(Integer surveyId) {
        if (!surveyRepository.existsById(surveyId))
            throw new SurveyNotFoundException(surveyId);
        return questionEntityRepository.findBySurveyIdOrderByDisplayOrder(surveyId);
    }

    private QuestionEntity findQuestionInSurvey(Integer surveyId, Integer questionId) {
        QuestionEntity q = questionEntityRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(questionId));
        if (q.getSurvey().getId() != (surveyId))
            throw new QuestionNotFoundException(questionId);
        return q;
    }
    @Transactional
    public QuestionEntity updateQuestion(Integer surveyId, Integer questionId, QuestionRequest request) {
        QuestionEntity entity = findQuestionInSurvey(surveyId, questionId);

        if (entity.getDeletedAt() != null)
            throw new InvalidSurveyStateException("Cannot edit a deleted question");
        if (entity.getSurvey().getDeletedAt() != null)
            throw new InvalidSurveyStateException("Cannot edit questions of a deleted survey");
        if (!entity.getType().equals(request.getType()))
            throw new IllegalArgumentException("Question type cannot be changed");

        Question rebuilt = QuestionFactory.createQuestion(request);   // full re-validation

        entity.setName(rebuilt.getName());
        entity.setQuestionText(rebuilt.getPrompt());
        entity.setDescription(rebuilt.getDescription());
        entity.setRequired(rebuilt.isRequired());

        if (rebuilt instanceof TextQuestion t) {
            entity.setMaxLength(t.getMaxLength());
        } else if (rebuilt instanceof ChoiceQuestion c) {
            entity.setAllowMultiple(c.isAllowMultiple());
            entity.setMinSelection(c.getMinSelection());
            entity.setMaxSelection(c.getMaxSelection());

            entity.getOptions().clear();
            questionEntityRepository.saveAndFlush(entity);
            int order = 1;
            for (Option o : c.getOptions()) {
                OptionEntity oe = new OptionEntity();
                oe.setOptionValue(o.getValue());
                oe.setLabel(o.getLabel());
                oe.setDisplayOrder(order++);
                entity.addOption(oe);
            }
        } else if (rebuilt instanceof FileQuestion f) {
            entity.setFileFormat(f.getFileFormat());
            entity.setMaxFileSize(f.getMaxFileSize());
            entity.setMaxFileSizeUnit(f.getMaxFileSizeUnit());
            entity.setAllowMultipleFiles(f.isAllowMultipleFiles());
        }

        return questionEntityRepository.save(entity);
    }


    public void activateQuestion(Integer surveyId, Integer questionId) {
        QuestionEntity q = findQuestionInSurvey(surveyId, questionId);
        if (q.getDeletedAt() != null)
            throw new InvalidSurveyStateException("Cannot activate a deleted question");
        q.setStatus("ACTIVE");
        questionEntityRepository.save(q);
    }

    public void deactivateQuestion(Integer surveyId, Integer questionId) {
        QuestionEntity q = findQuestionInSurvey(surveyId, questionId);
        if (q.getDeletedAt() != null)
            throw new InvalidSurveyStateException("Cannot deactivate a deleted question");
        q.setStatus("INACTIVE");
        questionEntityRepository.save(q);
    }


    public void deleteQuestion(Integer surveyId, Integer questionId) {
        QuestionEntity q = findQuestionInSurvey(surveyId, questionId);
        q.setDeletedAt(LocalDateTime.now());
        questionEntityRepository.save(q);
    }

    public List<QuestionEntity> findQuestionsBySurveyId(Integer surveyId, boolean includeInactive) {
        if (includeInactive) {
            // Admin view: survey just has to exist; returns everything (incl. deleted) — your existing behavior
            if (!surveyRepository.existsById(surveyId))
                throw new SurveyNotFoundException(surveyId);
            return questionEntityRepository.findBySurveyIdOrderByDisplayOrder(surveyId);
        }

        // User view: the SURVEY itself must be visible, not just the questions
        SurveyEntity survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new SurveyNotFoundException(surveyId));
        if (!"ACTIVE".equals(survey.getStatus()) || survey.getDeletedAt() != null)
            throw new SurveyNotFoundException(surveyId);   // information hiding: unpublished = nonexistent

        return questionEntityRepository
                .findBySurveyIdAndStatusAndDeletedAtIsNullOrderByDisplayOrder(surveyId, "ACTIVE");
    }
}
