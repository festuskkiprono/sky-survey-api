package com.skysurvey.sky_survey_api.survey;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/surveys",produces = MediaType.APPLICATION_XML_VALUE)
public class SurveyController {
    private final SurveyService surveyService;
    public SurveyController(SurveyService surveyService) {
        this.surveyService = surveyService;
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
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable Integer id) {
        surveyService.activateSurvey(id);
    }
    @PatchMapping("/{id}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable Integer id) {
        surveyService.deActivateSurvey(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        surveyService.deleteSurvey(id);
    }

}
