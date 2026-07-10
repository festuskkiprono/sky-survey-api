package com.skysurvey.sky_survey_api.response;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JacksonXmlRootElement(localName = "question_responses")
public class QuestionResponsesPageDto {
    @JacksonXmlProperty(isAttribute = true, localName = "current_page")
    private final int currentPage;

    @JacksonXmlProperty(isAttribute = true, localName = "last_page")
    private final int lastPage;

    @JacksonXmlProperty(isAttribute = true, localName = "page_size")
    private final int pageSize;

    @JacksonXmlProperty(isAttribute = true, localName = "total_count")
    private final long totalCount;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "question_response")
    private final List<QuestionResponseXmlDto> responses;

    public QuestionResponsesPageDto(int currentPage, int lastPage, int pageSize,
                                    long totalCount, List<QuestionResponseXmlDto> responses) {
        this.currentPage = currentPage;
        this.lastPage = Math.max(lastPage, 1);
        this.pageSize = pageSize;
        this.totalCount = totalCount;
        this.responses = responses;
    }

    public int getCurrentPage() { return currentPage; }
    public int getLastPage() { return lastPage; }
    public int getPageSize() { return pageSize; }
    public long getTotalCount() { return totalCount; }
    public List<QuestionResponseXmlDto> getResponses() { return responses; }
}
