package com.logwiki.specialsurveyservice.api.service.schedule.response;

import com.logwiki.specialsurveyservice.domain.schedule.Schedule;
import com.logwiki.specialsurveyservice.domain.schedule.ScheduleRunType;
import com.logwiki.specialsurveyservice.domain.schedule.ScheduleType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ScheduleResponse {

    private Long id;

    private ScheduleType type;

    private ScheduleRunType run;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Long surveyId;

    private String jobName;

    private String jobGroup;

    @Builder
    public ScheduleResponse(Long id, ScheduleType type,
            ScheduleRunType run, LocalDateTime startTime,
            LocalDateTime endTime, Long surveyId,
            String jobName, String jobGroup) {
        this.id = id;
        this.type = type;
        this.run = run;
        this.startTime = startTime;
        this.endTime = endTime;
        this.surveyId = surveyId;
        this.jobName = jobName;
        this.jobGroup = jobGroup;
    }

    public static ScheduleResponse from(Schedule schedule) {
        return ScheduleResponse.builder()
                .id(schedule.getId())
                .type(schedule.getType())
                .run(schedule.getRun())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .surveyId(schedule.getSurveyId())
                .jobName(schedule.getJobName())
                .jobGroup(schedule.getJobGroup())
                .build();
    }
}
