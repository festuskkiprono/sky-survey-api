package com.skysurvey.sky_survey_api.response.service;

import com.skysurvey.sky_survey_api.response.dto.QuestionResponseXmlDto;
import com.skysurvey.sky_survey_api.response.dto.QuestionResponsesPageDto;
import com.skysurvey.sky_survey_api.response.entity.SurveyResponseEntity;
import com.skysurvey.sky_survey_api.response.repository.SurveyResponseRepository;
import com.skysurvey.sky_survey_api.survey.exception.SurveyNotFoundException;
import com.skysurvey.sky_survey_api.survey.repository.SurveyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Endpoint #16's logic: page through a survey's responses, optionally filtered
 * by email, each one assembled into the dynamic XML shape.
 *
 * Admin semantics: the survey just has to EXIST (deleted/inactive surveys'
 * historical responses remain viewable -- deactivating a survey shouldn't
 * hide the data it already collected).
 *
 * The email filter is one derived-query WHERE on responses.email_address --
 * the payoff of Slice 5's email promotion. Without it, this would be a join
 * through answers and a scan of value_text.
 */
@Service
public class ResponseQueryService {

    private final SurveyRepository surveyRepository;
    private final SurveyResponseRepository responseRepository;
    private final ResponseAssembler assembler;

    public ResponseQueryService(SurveyRepository surveyRepository,
                                SurveyResponseRepository responseRepository,
                                ResponseAssembler assembler) {
        this.surveyRepository = surveyRepository;
        this.responseRepository = responseRepository;
        this.assembler = assembler;
    }

    /**
     * @param page 1-BASED, as the contract's ?page=1 implies.
     *             Spring's Pageable is 0-based -- the -1 below is the bridge.
     *             Keep the off-by-one in exactly one place: here.
     */
    @Transactional(readOnly = true)   // keeps the session open for the assembler's lazy walks
    public QuestionResponsesPageDto list(Integer surveyId, int page, int pageSize, String email) {
        if (!surveyRepository.existsById(surveyId)) {
            throw new SurveyNotFoundException(surveyId);
        }
        if (page < 1 || pageSize < 1) {
            throw new IllegalArgumentException("page and pageSize must be >= 1");
        }

        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("id").ascending());

        Page<SurveyResponseEntity> result = (email == null || email.isBlank())
                ? responseRepository.findBySurveyId(surveyId, pageable)
                : responseRepository.findBySurveyIdAndEmailAddress(surveyId, email.trim(), pageable);

        List<QuestionResponseXmlDto> assembled = result.getContent().stream()
                .map(assembler::assemble)
                .toList();

        return new QuestionResponsesPageDto(
                page,
                result.getTotalPages(),
                pageSize,
                result.getTotalElements(),
                assembled);
    }
}
