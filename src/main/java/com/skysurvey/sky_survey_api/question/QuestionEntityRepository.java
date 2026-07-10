package com.skysurvey.sky_survey_api.question;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionEntityRepository extends JpaRepository<QuestionEntity, Integer> {
    int countBySurveyId(Integer surveyId);
    List<QuestionEntity> findBySurveyIdOrderByDisplayOrder(Integer surveyId);
    int countBySurveyIdAndStatusAndDeletedAtIsNull(Integer surveyId, String status);
    List<QuestionEntity> findBySurveyIdAndStatusAndDeletedAtIsNullOrderByDisplayOrder(Integer surveyId, String status);
}
