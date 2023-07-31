package com.logwiki.specialsurveyservice.api.service.survey.response;

import com.logwiki.specialsurveyservice.api.controller.sse.response.SurveyResponseResult;
import com.logwiki.specialsurveyservice.domain.giveaway.Giveaway;
import com.logwiki.specialsurveyservice.domain.surveycategory.SurveyCategory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SurveyDetailgetServiceResponse {

    private final String title;

    private final LocalDateTime startTime;

    private final LocalDateTime endTime;

    private final int headCount;

    private final int closedHeadCount;

    private final Long writer;

    private final String writerName;

    private final SurveyCategory surveyCategory;

    private final List<Giveaway> giveaways;

    private final double winRate;

    private final int questionCount;

    private final double estimateTime;

    private final List<SurveyResponseResult> surveyResponseResults;

    @Builder
    public SurveyDetailgetServiceResponse(String title, LocalDateTime startTime , LocalDateTime endTime,
            int headCount , int closedHeadCount , Long writer, String writerName , SurveyCategory surveyCategory,
            List<Giveaway> giveaways , double winRate , int questionCount , double estimateTime , List<SurveyResponseResult>
            surveyResponseResults) {
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.headCount = headCount;
        this.closedHeadCount = closedHeadCount;
        this.writer = writer;
        this.writerName = writerName;
        this.surveyCategory = surveyCategory;
        this.giveaways = giveaways;
        this.winRate = winRate;
        this.questionCount = questionCount;
        this.estimateTime = estimateTime;
        this.surveyResponseResults = surveyResponseResults;
    }

}
