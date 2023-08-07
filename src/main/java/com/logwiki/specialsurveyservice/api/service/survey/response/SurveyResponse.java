package com.logwiki.specialsurveyservice.api.service.survey.response;

import com.logwiki.specialsurveyservice.api.service.giveaway.response.SurveyGiveawayResponse;
import com.logwiki.specialsurveyservice.api.service.question.response.QuestionResponse;
import com.logwiki.specialsurveyservice.domain.accountcode.AccountCodeType;
import com.logwiki.specialsurveyservice.domain.survey.Survey;
import com.logwiki.specialsurveyservice.domain.surveycategory.SurveyCategoryType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class SurveyResponse {

    private Long id;

    private String title;

    private String content;

    private String img;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private int headCount;

    private int closedHeadCount;

    private Long writer;

    private int totalGiveawayCount;

    private int requiredTimeInSeconds;

    private boolean closed;

    private Double winningPercent;

    private SurveyCategoryType surveyCategoryType;

    private List<QuestionResponse> questions;

    private List<SurveyGiveawayResponse> surveyGiveaways;

    private List<AccountCodeType> surveyTarget;

    @Builder
    public SurveyResponse(String content, String img, Long id, String title, LocalDateTime startTime,
            LocalDateTime endTime, int headCount, int closedHeadCount, Long writer,
            int totalGiveawayCount, boolean closed, int requiredTimeInSeconds,
            Double winningPercent, SurveyCategoryType surveyCategoryType, List<QuestionResponse> questions,
            List<SurveyGiveawayResponse> surveyGiveaways, List<AccountCodeType> surveyTarget) {
        this.id = id;
        this.title = title;
        this.img = img;
        this.content = content;
        this.startTime = startTime;
        this.endTime = endTime;
        this.headCount = headCount;
        this.closedHeadCount = closedHeadCount;
        this.writer = writer;
        this.totalGiveawayCount = totalGiveawayCount;
        this.requiredTimeInSeconds = requiredTimeInSeconds;
        this.closed = closed;
        this.winningPercent = winningPercent;
        this.surveyCategoryType = surveyCategoryType;
        this.questions = questions;
        this.surveyGiveaways = surveyGiveaways;
        this.surveyTarget = surveyTarget;
    }

    public static SurveyResponse from(Survey survey) {
        if (survey == null) {
            return null;
        }

        double winningPercent = 0D;
        switch (survey.getSurveyCategory().getType()) {
            case INSTANT_WIN:
                winningPercent = ((double) survey.getTotalGiveawayCount() / survey.getClosedHeadCount()) * 100;
                break;
            case NORMAL:
                if (survey.getHeadCount() == 0
                        || (double) survey.getTotalGiveawayCount() / survey.getHeadCount() >= 1D)
                    winningPercent = 100D;
                else
                    winningPercent = ((double) survey.getTotalGiveawayCount() / survey.getHeadCount()) * 100;
                break;
        }

        return SurveyResponse.builder()
                .id(survey.getId())
                .title(survey.getTitle())
                .content(survey.getContent())
                .img(survey.getImg())
                .startTime(survey.getStartTime())
                .endTime(survey.getEndTime())
                .headCount(survey.getHeadCount())
                .closedHeadCount(survey.getClosedHeadCount())
                .writer(survey.getWriter())
                .totalGiveawayCount(survey.getTotalGiveawayCount())
                .requiredTimeInSeconds(survey.getRequiredTimeInSeconds())
                .closed(survey.isClosed())
                .winningPercent(winningPercent)
                .surveyCategoryType(survey.getSurveyCategory().getType())
                .questions(survey.getQuestions().stream()
                        .map(QuestionResponse::from)
                        .collect(Collectors.toList()))
                .surveyGiveaways(survey.getSurveyGiveaways().stream()
                        .map(SurveyGiveawayResponse::from)
                        .collect(Collectors.toList()))
                .surveyTarget(survey.getSurveyTargets().stream()
                        .map(surveyTarget1 -> {
                            return (surveyTarget1.getAccountCode().getType());
                        })
                        .collect(Collectors.toList()))
                .build();
    }
}