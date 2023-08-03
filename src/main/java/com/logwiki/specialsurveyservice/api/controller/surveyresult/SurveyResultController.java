package com.logwiki.specialsurveyservice.api.controller.surveyresult;

import com.logwiki.specialsurveyservice.api.service.survey.response.SurveyResponse;
import com.logwiki.specialsurveyservice.api.service.surveyresult.SurveyResultService;
import com.logwiki.specialsurveyservice.api.service.surveyresult.response.MyGiveawayResponse;
import com.logwiki.specialsurveyservice.api.utils.ApiResponse;
import com.logwiki.specialsurveyservice.api.utils.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class SurveyResultController {

    private final SurveyResultService surveyResultService;

    @GetMapping("/user/giveaways")
    public ApiResponse<List<MyGiveawayResponse>> getMyGiveaways() {
        return ApiUtils.success(surveyResultService.getMyGiveaways());
    }

    @GetMapping("/user/surveys")
    public ApiResponse<List<SurveyResponse>> getAnsweredSurveys() {
        return ApiUtils.success(surveyResultService.getAnsweredSurveys());
    }

    @GetMapping("/user/survey/{surveyId}")
    public ApiResponse<?> getAnsweredSurveys(@PathVariable Long surveyId) {
        return ApiUtils.success(surveyResultService.getSurveyResult(surveyId));
    }
}
