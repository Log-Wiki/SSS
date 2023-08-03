package com.logwiki.specialsurveyservice.api.controller.survey;

import com.logwiki.specialsurveyservice.api.controller.survey.request.SurveyCreateRequest;
import com.logwiki.specialsurveyservice.api.service.schedule.ScheduleService;
import com.logwiki.specialsurveyservice.api.service.survey.SurveyService;
import com.logwiki.specialsurveyservice.api.service.survey.response.AbstractSurveyResponse;
import com.logwiki.specialsurveyservice.api.service.survey.response.SurveyResponse;
import com.logwiki.specialsurveyservice.api.utils.ApiResponse;
import com.logwiki.specialsurveyservice.api.utils.ApiUtils;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class SurveyController {

    private final SurveyService surveyService;

    private final ScheduleService scheduleService;

    @PostMapping("/survey")
    public ApiResponse<?> surveyAdd(@Valid @RequestBody SurveyCreateRequest dto) throws SchedulerException {

        SurveyResponse surveyResponse = surveyService.addSurvey(dto.from());

        scheduleService.addSurveySchedule(surveyResponse.getId(),
                surveyResponse.getStartTime(), surveyResponse.getEndTime());

        return ApiUtils.success(surveyResponse);
    }

    @GetMapping("/survey/recommend/normal")
    public ApiResponse<List<AbstractSurveyResponse>> getRecommendNormalSurveyForUser() {
        return ApiUtils.success(surveyService.getRecommendNormalSurvey());
    }

    @GetMapping("/survey/recommend/instant")
    public ApiResponse<List<AbstractSurveyResponse>> getRecommendInstantSurveyForUser() {
        return ApiUtils.success(surveyService.getRecommendInstantSurvey());
    }

    @GetMapping("/survey/recommend/time")
    public ApiResponse<List<AbstractSurveyResponse>> getRecommendShortTimeSurveyForUser() {
        return ApiUtils.success(surveyService.getRecommendShortTimeSurvey());
    }

    @GetMapping("/survey/{surveyId}")
    public ApiResponse<SurveyResponse> getSurvey(@PathVariable Long surveyId) {
        return ApiUtils.success(surveyService.getSurvey(surveyId));
    }

    @GetMapping("/survey/writing")
    public ApiResponse<List<AbstractSurveyResponse>> getWritingSurveys() {
        return ApiUtils.success(surveyService.getMySurveys());
    }

    @GetMapping("/survey/answered")
    public ApiResponse<List<AbstractSurveyResponse>> getAnsweredSurveys() {
        return ApiUtils.success(surveyService.getAnsweredSurveys());
    }
}
