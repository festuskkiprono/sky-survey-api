package com.skysurvey.sky_survey_api.survey;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SurveyService {
    private SurveyRepository surveyRepository;
    public SurveyService(SurveyRepository surveyRepository) {
        this.surveyRepository = surveyRepository;
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
        entity.setDescription(dto.getDescription());
        return surveyRepository.save(entity);
    }


}
