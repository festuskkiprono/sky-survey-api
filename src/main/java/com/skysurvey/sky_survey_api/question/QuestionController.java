package com.skysurvey.sky_survey_api.question;

import com.skysurvey.sky_survey_api.domain.QuestionRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/surveys/{surveyId}/questions", produces = MediaType.APPLICATION_XML_VALUE)
public class QuestionController {
    private final QuestionEntityService service;

    public QuestionController(QuestionEntityService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<QuestionCreatedDto> create(@PathVariable Integer surveyId,
                                                     @RequestBody QuestionRequest request) {
        QuestionEntity createdQuestion = service.createQuestion(surveyId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new QuestionCreatedDto(createdQuestion.getId(), createdQuestion.getName()));
    }


    @PutMapping(value = "/{questionId}", consumes = MediaType.APPLICATION_XML_VALUE)
    public QuestionResponseDto update(@PathVariable Integer surveyId,
                                      @PathVariable Integer questionId,
                                      @RequestBody QuestionRequest request) {
        return QuestionResponseDto.from(service.updateQuestion(surveyId, questionId, request));
    }

    @PatchMapping("/{questionId}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable Integer surveyId, @PathVariable Integer questionId) {
        service.activateQuestion(surveyId, questionId);
    }

    @PatchMapping("/{questionId}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable Integer surveyId, @PathVariable Integer questionId) {
        service.deactivateQuestion(surveyId, questionId);
    }

    @DeleteMapping("/{questionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer surveyId, @PathVariable Integer questionId) {
        service.deleteQuestion(surveyId, questionId);
    }

    @GetMapping
    public QuestionsListDto list(@PathVariable Integer surveyId,
                                 @RequestParam(defaultValue = "false") boolean includeInactive) {
        List<QuestionResponseDto> dtos = service.findQuestionsBySurveyId(surveyId, includeInactive).stream()
                .map(QuestionResponseDto::from)
                .toList();
        return new QuestionsListDto(dtos);
    }


}
