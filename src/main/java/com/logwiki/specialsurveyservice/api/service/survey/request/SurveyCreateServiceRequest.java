package com.logwiki.specialsurveyservice.api.service.survey.request;

import com.logwiki.specialsurveyservice.api.service.question.request.QuestionCreateServiceRequest;
import com.logwiki.specialsurveyservice.domain.accountcode.AccountCodeType;
import com.logwiki.specialsurveyservice.domain.questioncategory.QuestionCategoryType;
import com.logwiki.specialsurveyservice.domain.survey.Survey;
import com.logwiki.specialsurveyservice.domain.surveycategory.SurveyCategory;
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

    private static final int REQUIRED_TIME_FOR_SHORT_FORM_QUESTION = 20;
    private static final int REQUIRED_TIME_FOR_MULTIPLE_CHOICE_QUESTION = 10;

    private String title;

    private String img;

    private String content;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private int headCount;

    private int closedHeadCount;

    private SurveyCategoryType type;

    private List<QuestionCreateServiceRequest> questions;

    private List<GiveawayAssignServiceRequest> giveaways;

    private List<AccountCodeType> surveyTarget;

    @Builder
    public SurveyCreateServiceRequest(String content, String img, String title, LocalDateTime startTime, LocalDateTime endTime,
            int headCount, int closedHeadCount, SurveyCategoryType type,
            List<QuestionCreateServiceRequest> questions,
            List<GiveawayAssignServiceRequest> giveaways,
            List<AccountCodeType> surveyTarget) {
        this.title = title;
        this.img = img;
        this.content = content;
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

        int requiredTimeInSeconds = 0;
        for (QuestionCreateServiceRequest question : questions) {
            if (question.getType().equals(QuestionCategoryType.SHORT_FORM)) {
                requiredTimeInSeconds += REQUIRED_TIME_FOR_SHORT_FORM_QUESTION;
            } else if (question.getType().equals(QuestionCategoryType.MULTIPLE_CHOICE)) {
                requiredTimeInSeconds += REQUIRED_TIME_FOR_MULTIPLE_CHOICE_QUESTION;
            }
        }

        Survey survey = Survey.builder()
                .title(title)
                .startTime(startTime)
                .img(img)
                .content(content)
                .endTime(endTime)
                .headCount(headCount)
                .closedHeadCount(closedHeadCount)
                .writer(userId)
                .totalGiveawayCount(giveaways.stream()
                        .mapToInt(GiveawayAssignServiceRequest::getCount)
                        .sum())
                .requiredTimeInSeconds(requiredTimeInSeconds)
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
