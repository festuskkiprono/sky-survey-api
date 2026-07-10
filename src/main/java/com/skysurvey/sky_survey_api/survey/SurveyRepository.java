package com.skysurvey.sky_survey_api.survey;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SurveyRepository extends JpaRepository<SurveyEntity, Integer> {
    List<SurveyEntity> findByStatusAndDeletedAtIsNull(String status);
}
