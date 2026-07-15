package com.skysurvey.sky_survey_api.response.api;

import com.skysurvey.sky_survey_api.response.dto.SubmissionReceiptDto;
import com.skysurvey.sky_survey_api.response.service.SubmissionService;
import com.skysurvey.sky_survey_api.response.entity.SurveyResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Endpoint  POST /api/surveys/{surveyId}/responses -- multipart in, XML out.
 *
 * Note the asymmetry, unique in this API: this endpoint CONSUMES multipart/form-data
 * (text answers and binary files travel together, as the spec chose) but still
 * PRODUCES XML like everything else. Content-Type and Accept diverge here.
 *
 * The controller stays thin: its only job is flattening the multipart request
 * into two maps keyed by question name -- the shape the service speaks.
 */
@RestController
@RequestMapping(value = "/api/surveys/{surveyId}/responses",
        produces = MediaType.APPLICATION_XML_VALUE)
public class SubmissionController {

    private final SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SubmissionReceiptDto> submit(@PathVariable Integer surveyId,
                                                       MultipartHttpServletRequest request) {

        // Text form fields: question name -> value. getParameterMap gives String[]
        // (HTTP allows repeated keys); our contract sends one value per name --
        // multi-select arrives comma-joined INSIDE one value ("REACT,VUE") -- so
        // taking the first element is faithful, not lossy.
        Map<String, String> formFields = new LinkedHashMap<>();
        request.getParameterMap().forEach(
                (name, values) -> formFields.put(name, values.length > 0 ? values[0] : null));

        // File parts: question name -> one-or-many files (allow_multiple_files
        // means one key CAN carry several parts -- hence the List).
        Map<String, List<MultipartFile>> fileParts = request.getMultiFileMap();

        SurveyResponseEntity saved = submissionService.submit(surveyId, formFields, fileParts);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SubmissionReceiptDto.from(saved));
    }
}
