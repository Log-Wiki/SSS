package com.logwiki.specialsurveyservice.api.service.survey.response;

import static com.logwiki.specialsurveyservice.domain.surveycategory.SurveyCategoryType.NORMAL;

import com.logwiki.specialsurveyservice.api.service.giveaway.response.SurveyGiveawayResponse;
import com.logwiki.specialsurveyservice.domain.accountcode.AccountCodeType;
import com.logwiki.specialsurveyservice.domain.survey.Survey;
import com.logwiki.specialsurveyservice.domain.surveycategory.SurveyCategoryType;
import com.logwiki.specialsurveyservice.domain.surveyresult.SurveyResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class AbstractSurveyResponse {

    private Long id;
    private String title;
    private String content;
    private String img;
    private SurveyCategoryType surveyCategoryType;
    private List<AccountCodeType> surveyTarget;
    private String writerName;
    private Double winningPercent;
    private int requiredTimeInSeconds;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int headCount;
    private int closedHeadCount;
    private int questionCount;
    private long winHeadCount;
    private List<SurveyGiveawayResponse> surveyGiveaways;

    @Builder
    public AbstractSurveyResponse(String content, String img,Long id , String title, SurveyCategoryType surveyCategoryType,
                                  List<AccountCodeType> surveyTarget, String writerName, Double winningPercent,
                                  int requiredTimeInSeconds, LocalDateTime startTime, LocalDateTime endTime,
                                  int headCount, int closedHeadCount, int questionCount, long winHeadCount, List<SurveyGiveawayResponse> surveyGiveaways) {
        this.id = id;
        this.img = img;
        this.title = title;
        this.content = content;
        this.surveyCategoryType = surveyCategoryType;
        this.surveyTarget = surveyTarget;
        this.writerName = writerName;
        this.winningPercent = winningPercent;
        this.requiredTimeInSeconds = requiredTimeInSeconds;
        this.startTime = startTime;
        this.endTime = endTime;
        this.headCount = headCount;
        this.closedHeadCount = closedHeadCount;
        this.questionCount = questionCount;
        this.winHeadCount = winHeadCount;
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

        long winHeadCount = survey.getSurveyResults().stream()
                .filter(SurveyResult::isWin)
                .count();
        if(survey.getSurveyCategory().getType().equals(NORMAL))
            winHeadCount = 0;

        return AbstractSurveyResponse.builder()
                .id(survey.getId())
                .title(survey.getTitle())
                .content(survey.getContent())
                .img(survey.getImg())
                .surveyCategoryType(survey.getSurveyCategory().getType())
                .surveyTarget(survey.getSurveyTargets().stream()
                        .map(surveyTarget1 -> {
                            return (surveyTarget1.getAccountCode().getType());
                        })
                        .collect(Collectors.toList()))
                .writerName(writerName)
                .winningPercent(winningPercent)
                .requiredTimeInSeconds(survey.getRequiredTimeInSeconds())
                .startTime(survey.getStartTime())
                .endTime(survey.getEndTime())
                .headCount(survey.getHeadCount())
                .closedHeadCount(survey.getClosedHeadCount())
                .questionCount(survey.getQuestions().size())
                .winHeadCount(winHeadCount)
                .surveyGiveaways(survey.getSurveyGiveaways().stream()
                        .map(SurveyGiveawayResponse::from)
                        .collect(Collectors.toList()))
                .build();
    }
}
