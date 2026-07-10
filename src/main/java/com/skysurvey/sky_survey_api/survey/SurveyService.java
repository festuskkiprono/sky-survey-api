package com.skysurvey.sky_survey_api.survey;

import com.skysurvey.sky_survey_api.question.QuestionEntityRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SurveyService {
    private SurveyRepository surveyRepository;
    private final QuestionEntityRepository questionEntityRepository;
    public SurveyService(SurveyRepository surveyRepository, QuestionEntityRepository questionEntityRepository) {

        this.surveyRepository = surveyRepository;
        this.questionEntityRepository= questionEntityRepository;
    }

    public SurveyEntity createSurvey(CreateSurveyRequest surveyRequest) {
        SurveyEntity surveyEntity = new SurveyEntity();
        surveyEntity.setName(surveyRequest.getName());
        surveyEntity.setDescription(surveyRequest.getDescription());
        return surveyRepository.save(surveyEntity);
    }

    public List<SurveyEntity> findAllSurveys(){
        return surveyRepository.findAll();
    }
    public SurveyEntity findSurveyById(Integer id){
        return surveyRepository.findById(id).orElseThrow(()->new SurveyNotFoundException(id));
    }
    public SurveyEntity updateSurvey(Integer id, CreateSurveyRequest dto) {
        SurveyEntity entity = surveyRepository.findById(id)
                .orElseThrow(()->new SurveyNotFoundException(id));
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        return surveyRepository.save(entity);
    }

    public void activateSurvey(Integer id){
        SurveyEntity survey = surveyRepository.findById(id)
                .orElseThrow(()->new SurveyNotFoundException(id));


        if (questionEntityRepository.countBySurveyIdAndStatusAndDeletedAtIsNull(survey.getId(), "ACTIVE") == 0)
            throw new InvalidSurveyStateException("Cannot activate a survey with no active questions");


        if (survey.getDeletedAt() != null)
            throw new InvalidSurveyActivationException("Cannot activate  a deleted survey");
        survey.setStatus("ACTIVE");
        surveyRepository.save(survey);
    }
    public void deActivateSurvey(Integer id){
        SurveyEntity survey = surveyRepository.findById(id)
                .orElseThrow(()->new SurveyNotFoundException(id));

        if(survey.getDeletedAt() != null)
            throw new InvalidSurveyActivationException("Cannot deactivate a deleted survey");
        survey.setStatus("INACTIVE");
        surveyRepository.save(survey);
    }

    public void deleteSurvey(Integer id){
        SurveyEntity survey = surveyRepository.findById(id)
                .orElseThrow(()->new SurveyNotFoundException(id));
        survey.setDeletedAt(LocalDateTime.now());
        surveyRepository.save(survey);

    }


}
