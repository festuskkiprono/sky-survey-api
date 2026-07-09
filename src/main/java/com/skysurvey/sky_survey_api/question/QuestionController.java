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
    @GetMapping
    public QuestionsListDto list(@PathVariable Integer surveyId) {
        List<QuestionResponseDto> dtos = service.findQuestionsBySurveyId(surveyId).stream()
                .map(QuestionResponseDto::from)
                .toList();
        return new QuestionsListDto(dtos);
    }

}
