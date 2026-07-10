package com.skysurvey.sky_survey_api.response;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyResponseRepository extends JpaRepository<SurveyResponseEntity, Integer> {

    Page<SurveyResponseEntity> findBySurveyId(Integer surveyId, Pageable pageable);

    Page<SurveyResponseEntity> findBySurveyIdAndEmailAddress(
            Integer surveyId, String emailAddress, Pageable pageable);
}