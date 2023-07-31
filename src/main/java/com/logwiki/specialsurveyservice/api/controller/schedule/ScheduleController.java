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

    @PostMapping("/schedule")
    public ApiResponse<?> surveyAdd(@RequestBody ScheduleCreateRequest dto) throws Exception {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneMinuteLater = now.plusSeconds(5);
        dto.setStartTime(oneMinuteLater);
        return ApiUtils.success(scheduleService.addStartSurveySchedule(dto));
    }

    @GetMapping("/schedule")
    public ApiResponse<?> surveyGet() throws Exception {
        scheduleService.printAllJobs();
        return ApiUtils.success("success");
    }
}
