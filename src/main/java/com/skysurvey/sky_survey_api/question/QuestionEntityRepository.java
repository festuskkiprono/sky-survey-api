package com.skysurvey.sky_survey_api.question;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionEntityRepository extends JpaRepository<QuestionEntity, Integer> {
    int countBySurveyId(Integer surveyId);
}
