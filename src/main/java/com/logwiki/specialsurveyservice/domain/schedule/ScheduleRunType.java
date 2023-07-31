package com.logwiki.specialsurveyservice.domain.schedule;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor

public enum ScheduleRunType {

    BEFORE_RUN("동작 전"), AFTER_RUN("동작 후");

    private final String text;
    
}
