package com.skysurvey.sky_survey_api.commonFiles;

import com.skysurvey.sky_survey_api.survey.ErrorDto;
import com.skysurvey.sky_survey_api.survey.InvalidSurveyActivationException;
import com.skysurvey.sky_survey_api.survey.SurveyNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

public class GlobalExceptionHandler {
    @ExceptionHandler(SurveyNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto handleNotFound(SurveyNotFoundException ex) {
        return new ErrorDto(ex.getMessage());
    }
    @ExceptionHandler(InvalidSurveyActivationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleInvalidState(InvalidSurveyActivationException ex) {
        return new ErrorDto(ex.getMessage());
    }
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleInvalidPayload(IllegalStateException ex) {
        return new ErrorDto(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleIllegalArgument(IllegalArgumentException ex) {
        return new ErrorDto(ex.getMessage());
    }
}
