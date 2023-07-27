package com.logwiki.specialsurveyservice.api.service.survey.response;

import com.logwiki.specialsurveyservice.api.service.question.response.QuestionAnswerResponse;
import com.logwiki.specialsurveyservice.domain.surveycategory.SurveyCategoryType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SurveyCreateServiceResponse {

    private Long id;

    private String title;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private int headCount;

    private int closedHeadCount;

    private Long writer;

    private SurveyCategoryType surveyCategoryType;

    private List<QuestionAnswerResponse> questions;

    private List<SurveyCreateServiceResponse> surveyGiveaways;

}
