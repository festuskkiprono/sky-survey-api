package com.skysurvey.sky_survey_api.response;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UploadedFileRepository extends JpaRepository<UploadedFileEntity, Integer> {
    List<UploadedFileEntity> findByAnswerId(Integer answerId);
}
