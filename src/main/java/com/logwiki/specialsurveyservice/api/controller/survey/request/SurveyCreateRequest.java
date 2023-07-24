package com.logwiki.specialsurveyservice.api.controller.survey.request;

import com.logwiki.specialsurveyservice.domain.surveycategory.SurveyCategoryType;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SurveyCreateRequest {

    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int headCount;
    private int closedHeadCount;
    private SurveyCategoryType type;
    // writer는 서비스에서 넣는걸로
//    private Long writer;
}
