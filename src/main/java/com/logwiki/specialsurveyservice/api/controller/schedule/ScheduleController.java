package com.logwiki.specialsurveyservice.api.controller.schedule;

import com.logwiki.specialsurveyservice.api.controller.schedule.request.ScheduleCreateRequest;
import com.logwiki.specialsurveyservice.api.service.schedule.ScheduleService;
import com.logwiki.specialsurveyservice.api.utils.ApiResponse;
import com.logwiki.specialsurveyservice.api.utils.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping("/schedule/start")
    public ApiResponse<?> surveyStartAdd(@RequestBody ScheduleCreateRequest dto) throws Exception {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneMinuteLater = now.plusSeconds(5);
        dto.setStartTime(oneMinuteLater);
        return ApiUtils.success(scheduleService.addStartSurveySchedule(dto));
    }

    @PostMapping("/schedule/end")
    public ApiResponse<?> surveyEndAdd(@RequestBody ScheduleCreateRequest dto) throws Exception {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneMinuteLater = now.plusSeconds(5);
        dto.setStartTime(oneMinuteLater);
        return ApiUtils.success(scheduleService.addEndSurveySchedule(dto));
    }

    @GetMapping("/schedule/{surveyId}")
    public ApiResponse<?> surveyScheduleGet(@PathVariable Long surveyId) throws Exception {
        return ApiUtils.success(scheduleService.getSchedulesBySurveyId(surveyId));
    }

    @GetMapping("/schedule/before-run")
    public ApiResponse<?> scheduleBeforeRunGet() throws Exception {
        return ApiUtils.success(scheduleService.getBeforeRunSchedule());
    }

}
