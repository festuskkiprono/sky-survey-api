package com.skysurvey.sky_survey_api.response.repository;

import com.skysurvey.sky_survey_api.response.entity.AnswerOptionEntity;
import com.skysurvey.sky_survey_api.response.entity.AnswerOptionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface AnswerOptionRepository extends JpaRepository<AnswerOptionEntity, AnswerOptionId> {
    List<AnswerOptionEntity> findByIdAnswerId(Integer answerId);
}
