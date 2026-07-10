package com.skysurvey.sky_survey_api.response;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/surveys/{surveyId}/responses",produces = MediaType.APPLICATION_XML_VALUE)
public class ResponseQueryController {
    private final ResponseQueryService responseQueryService;

    public ResponseQueryController(ResponseQueryService responseQueryService) {
        this.responseQueryService = responseQueryService;
    }

    @GetMapping
    public QuestionResponsesPageDto list(@PathVariable Integer surveyId,
                                         @RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "10") int pageSize,
                                         @RequestParam(required = false) String email) {
        return responseQueryService.list(surveyId, page, pageSize, email);
    }
}
