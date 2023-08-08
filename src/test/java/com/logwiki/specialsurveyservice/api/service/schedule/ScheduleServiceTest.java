package com.logwiki.specialsurveyservice.api.service.schedule;

import com.logwiki.specialsurveyservice.IntegrationTestSupport;
import com.logwiki.specialsurveyservice.api.controller.schedule.request.ScheduleCreateRequest;
import com.logwiki.specialsurveyservice.api.service.schedule.response.ScheduleResponse;
import com.logwiki.specialsurveyservice.domain.schedule.ScheduleRunType;
import com.logwiki.specialsurveyservice.domain.schedule.ScheduleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
class ScheduleServiceTest extends IntegrationTestSupport {

    @Autowired
    private ScheduleService scheduleService;

    @DisplayName("설문 시작 스케줄러를 등록한다.")
    @Test
    void addStartSurveyScheduleTest() throws SchedulerException {
        Long surveyId = 1L;
        ScheduleCreateRequest scheduleCreateRequest = ScheduleCreateRequest.builder()
                .surveyId(surveyId)
                .startTime(LocalDateTime.now().plusSeconds(5))
                .build();
        ScheduleResponse scheduleResponse = scheduleService.addStartSurveySchedule(scheduleCreateRequest);
        assertAll(() -> {
            assertEquals(scheduleResponse.getSurveyId(), surveyId);
            assertEquals(scheduleResponse.getRun(), ScheduleRunType.BEFORE_RUN);
            assertEquals(scheduleResponse.getType(), ScheduleType.START_SURVEY);
        });
    }

    @DisplayName("설문 종료 스케줄러를 등록한다.")
    @Test
    void addEndSurveyScheduleTest() throws SchedulerException {
        Long surveyId = 1L;
        ScheduleCreateRequest scheduleCreateRequest = ScheduleCreateRequest.builder()
                .surveyId(surveyId)
                .startTime(LocalDateTime.now().plusSeconds(5))
                .build();
        ScheduleResponse scheduleResponse = scheduleService.addEndSurveySchedule(scheduleCreateRequest);
        assertAll(() -> {
            assertEquals(scheduleResponse.getSurveyId(), surveyId);
            assertEquals(scheduleResponse.getRun(), ScheduleRunType.BEFORE_RUN);
            assertEquals(scheduleResponse.getType(), ScheduleType.END_SURVEY);
        });
    }

    @DisplayName("시작시간이 현재보다 작으면 5초뒤 시간으로 스케줄러를 등록한다.")
    @Test
    void addStartSurveyScheduleAfter5SecondsTest() throws SchedulerException {
        Long surveyId = 1L;
        LocalDateTime nowDate = LocalDateTime.now();
        ScheduleCreateRequest scheduleCreateRequest = ScheduleCreateRequest.builder()
                .surveyId(surveyId)
                .startTime(LocalDateTime.now().minusSeconds(5))
                .build();
        ScheduleResponse scheduleResponse = scheduleService.addStartSurveySchedule(scheduleCreateRequest);
        assertAll(() -> {
            assertEquals(scheduleResponse.getSurveyId(), surveyId);
            assertEquals(scheduleResponse.getRun(), ScheduleRunType.BEFORE_RUN);
            assertEquals(scheduleResponse.getType(), ScheduleType.START_SURVEY);
            assertEquals(nowDate.plusSeconds(5).isBefore(scheduleResponse.getStartTime()), true);
        });
    }

}