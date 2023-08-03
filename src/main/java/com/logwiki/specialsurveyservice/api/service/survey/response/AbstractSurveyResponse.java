package com.logwiki.specialsurveyservice.api.service.survey.response;

import com.logwiki.specialsurveyservice.api.service.giveaway.response.SurveyGiveawayResponse;
import com.logwiki.specialsurveyservice.domain.accountcode.AccountCodeType;
import com.logwiki.specialsurveyservice.domain.survey.Survey;
import com.logwiki.specialsurveyservice.domain.surveycategory.SurveyCategoryType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AbstractSurveyResponse {

    private Long id;

    private String title;

    private SurveyCategoryType surveyCategoryType;

    private List<AccountCodeType> surveyTarget;

    private String writerName;

    private Double winningPercent;

    private int requiredTimeInSeconds;

    private LocalDateTime endTime;

    private int headCount;

    private int closedHeadCount;

    private List<SurveyGiveawayResponse> surveyGiveaways;

    @Builder
    public AbstractSurveyResponse(Long id , String title, SurveyCategoryType surveyCategoryType,
            List<AccountCodeType> surveyTarget, String writerName, Double winningPercent,
            int requiredTimeInSeconds, LocalDateTime endTime,
            int headCount, int closedHeadCount, List<SurveyGiveawayResponse> surveyGiveaways) {
        this.id = id;
        this.title = title;
        this.surveyCategoryType = surveyCategoryType;
        this.surveyTarget = surveyTarget;
        this.writerName = writerName;
        this.winningPercent = winningPercent;
        this.requiredTimeInSeconds = requiredTimeInSeconds;
        this.endTime = endTime;
        this.headCount = headCount;
        this.closedHeadCount = closedHeadCount;
        this.surveyGiveaways = surveyGiveaways;
    }

    public static AbstractSurveyResponse from(Survey survey, String writerName) {
        if (survey == null) {
            return null;
        }

        double winningPercent = 0D;
        switch (survey.getSurveyCategory().getType()) {
            case INSTANT_WIN:
                winningPercent =
                        ((double) survey.getTotalGiveawayCount() / survey.getClosedHeadCount())
                                * 100;
                break;
            case NORMAL:
                if (survey.getHeadCount() == 0
                        || (double) survey.getTotalGiveawayCount() / survey.getHeadCount() >= 1D)
                    winningPercent = 100D;
                else
                    winningPercent =
                            ((double) survey.getTotalGiveawayCount() / survey.getHeadCount()) * 100;
                break;
        }

        return AbstractSurveyResponse.builder()
                .id(survey.getId())
                .title(survey.getTitle())
                .surveyCategoryType(survey.getSurveyCategory().getType())
                .surveyTarget(survey.getSurveyTargets().stream()
                        .map(surveyTarget1 -> {
                            return (surveyTarget1.getAccountCode().getType());
                        })
                        .collect(Collectors.toList()))
                .writerName(writerName)
                .winningPercent(winningPercent)
                .requiredTimeInSeconds(survey.getRequiredTimeInSeconds())
                .endTime(survey.getEndTime())
                .headCount(survey.getHeadCount())
                .closedHeadCount(survey.getClosedHeadCount())
                .surveyGiveaways(survey.getSurveyGiveaways().stream()
                        .map(SurveyGiveawayResponse::from)
                        .collect(Collectors.toList()))
                .build();
    }
}
