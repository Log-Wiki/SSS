package com.logwiki.specialsurveyservice.api.service.survey.response;

import com.logwiki.specialsurveyservice.api.service.giveaway.response.SurveyGiveawayResponse;
import com.logwiki.specialsurveyservice.api.service.question.response.QuestionResponse;
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
public class SurveyResponse {

    private Long id;

    private String title;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private int headCount;

    private int closedHeadCount;

    private Long writer;

    private SurveyCategoryType surveyCategoryType;

    private List<QuestionResponse> questions;

    private List<SurveyGiveawayResponse> surveyGiveaways;

    @Builder
    public SurveyResponse(Long id, String title, LocalDateTime startTime,
            LocalDateTime endTime, int headCount, int closedHeadCount, Long writer, SurveyCategoryType surveyCategoryType,
            List<QuestionResponse> questions, List<SurveyGiveawayResponse> surveyGiveaways) {
        this.id = id;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.headCount = headCount;
        this.closedHeadCount = closedHeadCount;
        this.writer = writer;
        this.surveyCategoryType = surveyCategoryType;
        this.questions = questions;
        this.surveyGiveaways = surveyGiveaways;
    }

    public static SurveyResponse from(Survey survey) {
        if (survey == null) {
            return null;
        }
        return SurveyResponse.builder()
                .id(survey.getId())
                .title(survey.getTitle())
                .startTime(survey.getStartTime())
                .endTime(survey.getEndTime())
                .headCount(survey.getHeadCount())
                .closedHeadCount(survey.getClosedHeadCount())
                .writer(survey.getWriter())
                .surveyCategoryType(survey.getSurveyCategory().getType())
                .questions(survey.getQuestions().stream().map(QuestionResponse::from).collect(Collectors.toList()))
                .surveyGiveaways(survey.getSurveyGiveaways().stream()
                        .map(SurveyGiveawayResponse::from).collect(Collectors.toList()))
                .build();
    }
}