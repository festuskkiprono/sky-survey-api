package com.skysurvey.sky_survey_api.response;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface FileStorage {
    String store(MultipartFile file);
    Path resolve(String storagePath);
    void deleteQuietly(String storagePath);
}
