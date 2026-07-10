package com.skysurvey.sky_survey_api.response;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface AnswerOptionRepository extends JpaRepository<AnswerOptionEntity, AnswerOptionId> {
    List<AnswerOptionEntity> findByIdAnswerId(Integer answerId);
}
