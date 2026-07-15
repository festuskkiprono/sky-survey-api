package com.skysurvey.sky_survey_api.survey.api;

import com.skysurvey.sky_survey_api.question.service.QuestionEntityService;
import com.skysurvey.sky_survey_api.survey.dto.CreateSurveyRequest;
import com.skysurvey.sky_survey_api.survey.dto.SurveyActivatedDeactivatedDto;
import com.skysurvey.sky_survey_api.survey.dto.SurveyResponseDto;
import com.skysurvey.sky_survey_api.survey.dto.SurveysListDto;
import com.skysurvey.sky_survey_api.survey.entity.SurveyEntity;
import com.skysurvey.sky_survey_api.survey.service.SurveyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/surveys",produces = MediaType.APPLICATION_XML_VALUE)
public class SurveyController {
    private final SurveyService surveyService;
    private final QuestionEntityService questionEntityService;
    public SurveyController(SurveyService surveyService,QuestionEntityService questionEntityService) {

        this.surveyService = surveyService;
        this.questionEntityService = questionEntityService;
    }

    @PostMapping(consumes=MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<SurveyResponseDto> createSurvey(@RequestBody CreateSurveyRequest createSurveyRequest) {
        SurveyEntity createdEntity = surveyService.createSurvey(createSurveyRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SurveyResponseDto.from(createdEntity));

    }
    @GetMapping
    public SurveysListDto getAllSurveys() {
        List<SurveyResponseDto> dtos = surveyService.findAllSurveys()
                .stream()
                .map(SurveyResponseDto::from)
                .toList();
        return new SurveysListDto(dtos);

    }
    @GetMapping("/{id}") public SurveyResponseDto get(@PathVariable Integer id) {

        return SurveyResponseDto.from(surveyService.findSurveyById(id));
    }
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_XML_VALUE)
    public SurveyResponseDto update(@PathVariable Integer id, @RequestBody CreateSurveyRequest request) {
        return SurveyResponseDto.from(surveyService.updateSurvey(id, request));

    }
    @PatchMapping("/{id}/activate")
    public SurveyActivatedDeactivatedDto activate(@PathVariable Integer id) {
        SurveyEntity survey = surveyService.activateSurvey(id);
        return new SurveyActivatedDeactivatedDto("Survey '" + survey.getName() + "' has been activated");
    }


    @PatchMapping("/{id}/deactivate")
    public SurveyActivatedDeactivatedDto deactivate(@PathVariable Integer id) {
        SurveyEntity survey = surveyService.deActivateSurvey(id);
        return new SurveyActivatedDeactivatedDto("Survey '" + survey.getName() + "' has been deactivated");
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        surveyService.deleteSurvey(id);
    }

    //  User-facing: only ACTIVE, non-deleted surveys
    @GetMapping("/available")
    public SurveysListDto listAvailable() {
        List<SurveyResponseDto> dtos = surveyService.findAvailableSurveys().stream()
                .map(SurveyResponseDto::from)
                .toList();
        return new SurveysListDto(dtos);
    }

}
