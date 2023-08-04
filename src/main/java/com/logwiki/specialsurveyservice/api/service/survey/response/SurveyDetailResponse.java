package com.logwiki.specialsurveyservice.api.service.survey.response;

import com.logwiki.specialsurveyservice.domain.survey.Survey;
import com.logwiki.specialsurveyservice.domain.surveycategory.SurveyCategoryType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SurveyDetailResponse {

    private final String title;

    private final LocalDateTime startTime;

    private final LocalDateTime endTime;

    private final int headCount;

    private final int closedHeadCount;

    private final Long writer;

    private final String writerName;

    private final SurveyCategoryType surveyCategoryType;

    private final List<String> giveawayNames;

    private final double winRate;

    private final int questionCount;

    private final double estimateTime;

    private final int requiredTimeInSeconds;

    @Builder
    public SurveyDetailResponse(String title, LocalDateTime startTime , LocalDateTime endTime,
            int headCount , int closedHeadCount , Long writer, String writerName , SurveyCategoryType surveyCategoryType,
            List<String> giveawayNames, double winRate , int questionCount , double estimateTime,
            int requiredTimeInSeconds) {
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.headCount = headCount;
        this.closedHeadCount = closedHeadCount;
        this.writer = writer;
        this.writerName = writerName;
        this.surveyCategoryType = surveyCategoryType;
        this.giveawayNames = giveawayNames;
        this.winRate = winRate;
        this.questionCount = questionCount;
        this.estimateTime = estimateTime;
        this.requiredTimeInSeconds = requiredTimeInSeconds;
    }

    public static SurveyDetailResponse of(Survey targetSurvey, double winRate, List<String> giveawayNames, String writerName) {
        return SurveyDetailResponse.builder()
                .surveyCategoryType(targetSurvey.getSurveyCategory().getType())
                .title(targetSurvey.getTitle())
                .headCount(targetSurvey.getHeadCount())
                .closedHeadCount(targetSurvey.getClosedHeadCount())
                .startTime(targetSurvey.getStartTime())
                .endTime(targetSurvey.getEndTime())
                .requiredTimeInSeconds(targetSurvey.getRequiredTimeInSeconds())
                .writer(targetSurvey.getWriter())
                .writerName(writerName)
                .winRate(winRate)
                .estimateTime(0)
                .questionCount(targetSurvey.getQuestions().size())
                .giveawayNames(giveawayNames)
                .build();
    }
}