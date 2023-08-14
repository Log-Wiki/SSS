package com.logwiki.specialsurveyservice.api.controller.schedule;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.logwiki.specialsurveyservice.ControllerTestSupport;
import com.logwiki.specialsurveyservice.api.controller.schedule.request.ScheduleCreateRequest;
import com.logwiki.specialsurveyservice.api.service.schedule.response.ScheduleResponse;
import com.logwiki.specialsurveyservice.domain.schedule.ScheduleRunType;
import com.logwiki.specialsurveyservice.domain.schedule.ScheduleType;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

class ScheduleControllerTest extends ControllerTestSupport {

    @DisplayName("스케줄러를 이용하여 설문을 시작한다.")
    @WithMockUser
    @Test
    void surveyStartAdd() throws Exception {
        Long surveyId = 1L;
        ScheduleCreateRequest scheduleCreateRequest = ScheduleCreateRequest
                .builder()
                .surveyId(surveyId)
                .build();

        LocalDateTime startDateTime = LocalDateTime.now();
        ScheduleResponse scheduleResponse = ScheduleResponse
                .builder()
                .type(ScheduleType.START_SURVEY)
                .run(ScheduleRunType.BEFORE_RUN)
                .startTime(startDateTime)
                .surveyId(surveyId)
                .jobName("jobName")
                .jobGroup("jobGroup")
                .build();

        when(scheduleService.addStartSurveySchedule(any())).thenReturn(scheduleResponse);

        // when // then
        mockMvc.perform(
                        post("/api/schedule/start")
                                .content(objectMapper.writeValueAsString(scheduleCreateRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.response.surveyId").value(surveyId));
    }

    @DisplayName("스케줄러를 이용하여 설문을 마감한다.")
    @WithMockUser
    @Test
    void surveyEndAdd() throws Exception {
        Long surveyId = 1L;
        ScheduleCreateRequest scheduleCreateRequest = ScheduleCreateRequest
                .builder()
                .surveyId(surveyId)
                .build();

        LocalDateTime startDateTime = LocalDateTime.now();
        ScheduleResponse scheduleResponse = ScheduleResponse
                .builder()
                .type(ScheduleType.END_SURVEY)
                .run(ScheduleRunType.BEFORE_RUN)
                .startTime(startDateTime)
                .surveyId(surveyId)
                .jobName("jobName")
                .jobGroup("jobGroup")
                .build();

        when(scheduleService.addEndSurveySchedule(any())).thenReturn(scheduleResponse);

        // when // then
        mockMvc.perform(
                        post("/api/schedule/end")
                                .content(objectMapper.writeValueAsString(scheduleCreateRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.response.surveyId").value(surveyId));
    }

    @DisplayName("설문ID를 이용하여 설문 스케줄러 정보를 가져온다.")
    @WithMockUser
    @Test
    void surveyScheduleGet() throws Exception {
        Long surveyId = 1L;

        LocalDateTime scheduleStartDateTime = LocalDateTime.now().minusDays(1);
        ScheduleType startScheduleType = ScheduleType.START_SURVEY;
        ScheduleResponse startScheduleResponse = ScheduleResponse
                .builder()
                .type(startScheduleType)
                .run(ScheduleRunType.BEFORE_RUN)
                .startTime(scheduleStartDateTime)
                .surveyId(surveyId)
                .jobName("jobName")
                .jobGroup("jobGroup")
                .build();
        LocalDateTime scheduleEndDateTime = LocalDateTime.now();
        ScheduleType endScheduleType = ScheduleType.END_SURVEY;
        ScheduleResponse endScheduleResponse = ScheduleResponse
                .builder()
                .type(endScheduleType)
                .run(ScheduleRunType.BEFORE_RUN)
                .startTime(scheduleEndDateTime)
                .surveyId(surveyId)
                .jobName("jobName")
                .jobGroup("jobGroup")
                .build();

        when(scheduleService.getSchedulesBySurveyId(surveyId)).thenReturn(
                List.of(startScheduleResponse, endScheduleResponse));

        // when // then
        mockMvc.perform(
                        get("/api/schedule/{surveyId}", surveyId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.response[0].surveyId").value(surveyId))
                .andExpect(jsonPath("$.response[1].surveyId").value(surveyId))
                .andExpect(jsonPath("$.response[0].type").value(startScheduleType.toString()))
                .andExpect(jsonPath("$.response[1].type").value(endScheduleType.toString()));
    }

    @DisplayName("대기중인 스케줄러 정보를 가져온다.")
    @WithMockUser
    @Test
    void scheduleBeforeRunGet() throws Exception {
        Long surveyId = 1L;

        ScheduleType startScheduleType = ScheduleType.START_SURVEY;
        ScheduleResponse startScheduleResponse = ScheduleResponse
                .builder()
                .type(startScheduleType)
                .run(ScheduleRunType.BEFORE_RUN)
                .startTime(LocalDateTime.now().plusHours(1))
                .surveyId(surveyId)
                .jobName("jobName")
                .jobGroup("jobGroup")
                .build();
        ScheduleType endScheduleType = ScheduleType.END_SURVEY;
        ScheduleResponse endScheduleResponse = ScheduleResponse
                .builder()
                .type(endScheduleType)
                .run(ScheduleRunType.BEFORE_RUN)
                .startTime(LocalDateTime.now().plusHours(2))
                .surveyId(surveyId)
                .jobName("jobName")
                .jobGroup("jobGroup")
                .build();

        when(scheduleService.getBeforeRunSchedule()).thenReturn(
                List.of(startScheduleResponse, endScheduleResponse));

        // when // then
        mockMvc.perform(
                        get("/api/schedule/before-run")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.response[0].surveyId").value(surveyId))
                .andExpect(jsonPath("$.response[1].surveyId").value(surveyId))
                .andExpect(jsonPath("$.response[0].type").value(startScheduleType.toString()))
                .andExpect(jsonPath("$.response[1].type").value(endScheduleType.toString()));
    }
}