package com.skysurvey.sky_survey_api.question.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JacksonXmlRootElement(localName = "questions")
public class QuestionsListDto {
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "question")
    private List<QuestionResponseDto> questions;

    public QuestionsListDto(List<QuestionResponseDto> questions) {
        this.questions = questions;
    }

    public List<QuestionResponseDto> getQuestions() { return questions; }
}
