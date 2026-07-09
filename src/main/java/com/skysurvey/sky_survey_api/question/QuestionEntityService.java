package com.skysurvey.sky_survey_api.question;

import com.skysurvey.sky_survey_api.domain.Question;
import com.skysurvey.sky_survey_api.domain.QuestionRequest;
import com.skysurvey.sky_survey_api.survey.InvalidSurveyStateException;
import com.skysurvey.sky_survey_api.survey.SurveyEntity;
import com.skysurvey.sky_survey_api.survey.SurveyNotFoundException;
import com.skysurvey.sky_survey_api.survey.SurveyRepository;
import com.skysurvey.sky_survey_api.domain.QuestionFactory;
import org.springframework.stereotype.Service;

import static com.skysurvey.sky_survey_api.question.QuestionEntityFromQuestion.createQuestionEntity;
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
}
