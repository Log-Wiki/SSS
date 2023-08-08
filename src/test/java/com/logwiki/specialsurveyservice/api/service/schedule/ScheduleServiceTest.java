package com.logwiki.specialsurveyservice.api.service.schedule;

import com.logwiki.specialsurveyservice.IntegrationTestSupport;
import com.logwiki.specialsurveyservice.api.controller.schedule.request.ScheduleCreateRequest;
import com.logwiki.specialsurveyservice.api.service.schedule.response.ScheduleResponse;
import com.logwiki.specialsurveyservice.domain.schedule.ScheduleRunType;
import com.logwiki.specialsurveyservice.domain.schedule.ScheduleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
class ScheduleServiceTest extends IntegrationTestSupport {

    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private Scheduler scheduler;

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
            assertTrue(nowDate.plusSeconds(5).isBefore(scheduleResponse.getStartTime()));
        });
    }

    @DisplayName("시작 , 종료 스케줄러를 모두 등록한다.")
    @Test
    void addScheduleAndCheckRegister() throws SchedulerException {
        Long surveyId = 1L;
        LocalDateTime nowDate = LocalDateTime.now();
        scheduleService.addSurveySchedule(surveyId, nowDate, nowDate.plusSeconds(10));
        List<ScheduleResponse> scheduleResponseList = scheduleService.getBeforeRunSchedule();
        assertAll(() -> {
            assertEquals(scheduleResponseList.size(), 2);
            assertEquals(scheduleResponseList.get(0).getRun(), ScheduleRunType.BEFORE_RUN);
            assertEquals(scheduleResponseList.get(1).getRun(), ScheduleRunType.BEFORE_RUN);
        });
    }

    @DisplayName("스케줄러 아이디로 스케줄러를 검색한다.")
    @Test
    void nothingSchedulerThrowError() throws SchedulerException {
        Long surveyId = 1L;
        LocalDateTime nowDate = LocalDateTime.now();
        scheduleService.addSurveySchedule(surveyId, nowDate, nowDate.plusSeconds(10));
        List<ScheduleResponse> scheduleResponseList = scheduleService.getSchedulesBySurveyId(surveyId);
        assertAll(() -> {
            assertEquals(scheduleResponseList.size(), 2);
            assertEquals(scheduleResponseList.get(0).getRun(), ScheduleRunType.BEFORE_RUN);
            assertEquals(scheduleResponseList.get(1).getRun(), ScheduleRunType.BEFORE_RUN);
            assertEquals(scheduleResponseList.get(0).getSurveyId(), surveyId);
            assertEquals(scheduleResponseList.get(1).getSurveyId(), surveyId);
        });
    }

    @DisplayName("스케줄러를 등록 후 삭제한다.")
    @Test
    void addSchedulerAndDeleteSchedule() throws SchedulerException {
        Long surveyId = 1L;
        int Empty = 0;
        LocalDateTime nowDate = LocalDateTime.now();
        ScheduleCreateRequest scheduleCreateRequest = ScheduleCreateRequest.builder()
                .surveyId(surveyId)
                .startTime(LocalDateTime.now().minusSeconds(5))
                .build();
        ScheduleResponse scheduleResponse = scheduleService.addStartSurveySchedule(scheduleCreateRequest);
        scheduleService.deleteJob(scheduleResponse.getJobName(), scheduleResponse.getJobGroup());

        assertAll(() -> {
            assertEquals(scheduler.getJobGroupNames().size(), Empty);
        });
    }

    @BeforeEach
    void clearScheduler() {
        try {
            for (String groupName : scheduler.getJobGroupNames()) {
                for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                    scheduler.deleteJob(jobKey);
                }
            }
        } catch (SchedulerException se) {
            se.printStackTrace();
        }
    }

}