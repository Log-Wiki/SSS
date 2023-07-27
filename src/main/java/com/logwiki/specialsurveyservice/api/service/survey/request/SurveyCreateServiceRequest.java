package com.logwiki.specialsurveyservice.api.service.survey.request;

import com.logwiki.specialsurveyservice.api.service.question.request.QuestionCreateServiceRequest;
import com.logwiki.specialsurveyservice.domain.survey.Survey;
import com.logwiki.specialsurveyservice.domain.surveycategory.SurveyCategory;
import com.logwiki.specialsurveyservice.domain.surveycategory.SurveyCategoryType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SurveyCreateServiceRequest {

    private String title;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private int headCount;

    private int closedHeadCount;

    private SurveyCategoryType type;

    private List<QuestionCreateServiceRequest> questions;

    private List<GiveawayAssignServiceRequest> giveaways;

    private List<Long> surveyTarget;

    @Builder
    public SurveyCreateServiceRequest(String title, LocalDateTime startTime, LocalDateTime endTime,
            int headCount, int closedHeadCount, SurveyCategoryType type,
            List<QuestionCreateServiceRequest> questions,
            List<GiveawayAssignServiceRequest> giveaways,
            List<Long> surveyTarget) {
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.headCount = headCount;
        this.closedHeadCount = closedHeadCount;
        this.type = type;
        this.questions = questions;
        this.giveaways = giveaways;
        this.surveyTarget = surveyTarget;
    }

    public Survey toEntity(Long userId) {
        Survey survey = Survey.builder()
                .title(title)
                .startTime(startTime)
                .endTime(endTime)
                .headCount(headCount)
                .closedHeadCount(closedHeadCount)
                .writer(userId)
                .type(SurveyCategory.builder()
                        .type(type)
                        .build())
                .build();
        survey.addQuestions(questions.stream()
                .map(questionCreateServiceRequest -> {
                    return questionCreateServiceRequest.toEntity(survey);
                }).collect(Collectors.toList()));
        return survey;
    }


}
