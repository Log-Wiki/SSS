package com.logwiki.specialsurveyservice.api.service.schedule.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class SurveyStartScheduleCreateRequest {

    private Long surveyId;

    private LocalDateTime startScheduleTime;

}
