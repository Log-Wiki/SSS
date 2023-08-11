package com.logwiki.specialsurveyservice.api.service.survey.response;

import com.logwiki.specialsurveyservice.api.service.giveaway.response.SurveyGiveawayResponse;
import com.logwiki.specialsurveyservice.api.service.question.response.QuestionAnswerStatisticsResponse;
import com.logwiki.specialsurveyservice.api.service.question.response.QuestionResponse;
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
public class StatisticsSurveyResponse {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String img;
    private int headCount;
    private int closedHeadCount;
    private String writerName;
    private int totalGiveawayCount;
    private int requiredTimeInSeconds;
    private boolean closed;
    private SurveyCategoryType surveyCategoryType;
    private List<QuestionResponse> questions;
    private List<SurveyGiveawayResponse> surveyGiveaways;
    private List<AccountCodeType> surveyTarget;
    private List<QuestionAnswerStatisticsResponse> questionAnswers;

    @Builder
    public StatisticsSurveyResponse(Long id, String title, String content, LocalDateTime startTime,
            LocalDateTime endTime, String img, int headCount, int closedHeadCount, String writerName,
            int totalGiveawayCount, int requiredTimeInSeconds, boolean closed, SurveyCategoryType surveyCategoryType,
            List<QuestionResponse> questions, List<SurveyGiveawayResponse> surveyGiveaways,
            List<AccountCodeType> surveyTarget, List<QuestionAnswerStatisticsResponse> questionAnswers) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.startTime = startTime;
        this.endTime = endTime;
        this.img = img;
        this.headCount = headCount;
        this.closedHeadCount = closedHeadCount;
        this.writerName = writerName;
        this.totalGiveawayCount = totalGiveawayCount;
        this.requiredTimeInSeconds = requiredTimeInSeconds;
        this.closed = closed;
        this.surveyCategoryType = surveyCategoryType;
        this.questions = questions;
        this.surveyGiveaways = surveyGiveaways;
        this.surveyTarget = surveyTarget;
        this.questionAnswers = questionAnswers;
    }

    public static StatisticsSurveyResponse from(Survey survey, String writerName,
            List<QuestionAnswerStatisticsResponse> questionAnswers) {

        return StatisticsSurveyResponse.builder()
                .id(survey.getId())
                .title(survey.getTitle())
                .content(survey.getContent())
                .startTime(survey.getStartTime())
                .endTime(survey.getEndTime())
                .img(survey.getImg())
                .headCount(survey.getHeadCount())
                .closedHeadCount(survey.getClosedHeadCount())
                .writerName(writerName)
                .totalGiveawayCount(survey.getTotalGiveawayCount())
                .requiredTimeInSeconds(survey.getRequiredTimeInSeconds())
                .closed(survey.isClosed())
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
                .questionAnswers(questionAnswers)
                .build();
    }
}
