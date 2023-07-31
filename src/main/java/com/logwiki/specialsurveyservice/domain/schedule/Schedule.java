package com.logwiki.specialsurveyservice.domain.schedule;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ScheduleType type;
    
    @Enumerated(EnumType.STRING)
    private ScheduleRunType run;

    private LocalDateTime endTime;

    private Long surveyId;

    private String jobName;

    private String jobGroup;

    @Builder
    public Schedule(ScheduleType type, ScheduleRunType run, LocalDateTime endTime, Long surveyId,
            String jobName, String jobGroup) {
        this.type = type;
        this.run = run;
        this.endTime = endTime;
        this.surveyId = surveyId;
        this.jobName = jobName;
        this.jobGroup = jobGroup;
    }
}
