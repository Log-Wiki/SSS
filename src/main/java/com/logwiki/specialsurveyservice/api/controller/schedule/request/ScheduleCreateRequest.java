package com.logwiki.specialsurveyservice.api.controller.schedule.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ScheduleCreateRequest {

    private Long surveyId;

    private LocalDateTime startTime;

    @Builder
    public ScheduleCreateRequest(Long surveyId, LocalDateTime startTime) {
        this.surveyId = surveyId;
        this.startTime = startTime;
    }
}
