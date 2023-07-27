package com.logwiki.specialsurveyservice.api.controller.survey;

import com.logwiki.specialsurveyservice.api.controller.survey.request.SurveyCreateRequest;
import com.logwiki.specialsurveyservice.api.service.survey.SurveyService;
import com.logwiki.specialsurveyservice.api.utils.ApiResponse;
import com.logwiki.specialsurveyservice.api.utils.ApiUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class SurveyController {

    private final SurveyService surveyService;

    @PostMapping("/survey")
    public ApiResponse<?> surveyAdd(@Valid @RequestBody SurveyCreateRequest dto, Authentication authentication) {
        String userId = authentication.getName();
        return ApiUtils.success(surveyService.addSurvey(userId, dto.from()));
    }
}
