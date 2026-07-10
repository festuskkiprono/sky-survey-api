package com.skysurvey.sky_survey_api.response;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<AnswerEntity, Integer> {
}
