package com.logwiki.specialsurveyservice.api.controller.surveyresult;

import com.logwiki.specialsurveyservice.api.service.surveyresult.SurveyResultService;
import com.logwiki.specialsurveyservice.api.service.surveyresult.response.WinningAccountResponse;
import com.logwiki.specialsurveyservice.api.utils.ApiResponse;
import com.logwiki.specialsurveyservice.api.utils.ApiUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class SurveyResultController {

    private final SurveyResultService surveyResultService;

    @GetMapping("/user/survey/{surveyId}")
    public ApiResponse<?> getAnsweredSurveys(@PathVariable Long surveyId) {
        return ApiUtils.success(surveyResultService.getSurveyResult(surveyId));
    }

    @PatchMapping("/user/survey-result/check/{surveyId}")
    public ApiResponse<?> patchSurveyResult(@PathVariable Long surveyId) {
        return ApiUtils.success(surveyResultService.patchSurveyResult(surveyId));
    }

    @GetMapping("/survey-result/users/{surveyId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ApiResponse<List<WinningAccountResponse>> getWinningUsers(@PathVariable Long surveyId) {
        return ApiUtils.success(surveyResultService.getWinningUsers(surveyId));
    }
}
