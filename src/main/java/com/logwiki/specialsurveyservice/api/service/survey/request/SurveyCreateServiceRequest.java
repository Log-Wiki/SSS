package com.logwiki.specialsurveyservice.api.service.survey.request;

import com.logwiki.specialsurveyservice.api.service.question.request.QuestionCreateServiceRequest;
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
public class SurveyCreateServiceRequest {

    private String title;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private int headCount;

    private int closedHeadCount;

    private SurveyCategoryType type;

    private List<QuestionCreateServiceRequest> questions;

    private List<GiveawayAssignServiceRequest> giveaways;

    @Builder
    public SurveyCreateServiceRequest(String title, LocalDateTime startTime, LocalDateTime endTime,
                                      int headCount, int closedHeadCount, SurveyCategoryType type,
                                      List<QuestionCreateServiceRequest> questions,
                                      List<GiveawayAssignServiceRequest> giveaways) {
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.headCount = headCount;
        this.closedHeadCount = closedHeadCount;
        this.type = type;
        this.questions = questions;
        this.giveaways = giveaways;
    }

    public Survey toEntity(Long userId) {
        return Survey.builder()
                .title(title)
                .startTime(startTime)
                .endTime(endTime)
                .headCount(headCount)
                .closedHeadCount(closedHeadCount)
                .writer(userId)
                .questions(questions.stream().map(
                        QuestionCreateServiceRequest::toEntity
                ).collect(Collectors.toList()))
                .build();
    }
}
