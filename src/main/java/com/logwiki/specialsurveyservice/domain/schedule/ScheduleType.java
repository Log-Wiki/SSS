package com.logwiki.specialsurveyservice.domain.schedule;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor

public enum ScheduleType {

    START_SURVEY("설문 시작"), END_SURVEY("설문 마감");

    private final String text;

}
