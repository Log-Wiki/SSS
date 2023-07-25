package com.logwiki.specialsurveyservice.api.controller.question;

import com.logwiki.specialsurveyservice.api.controller.question.request.QuestionAnswerCreateRequest;
import com.logwiki.specialsurveyservice.api.controller.question.request.QuestionModifyRequest;
import com.logwiki.specialsurveyservice.api.service.question.QuestionAnswerService;
import com.logwiki.specialsurveyservice.api.service.question.QuestionService;
import com.logwiki.specialsurveyservice.api.utils.ApiResponse;
import com.logwiki.specialsurveyservice.api.utils.ApiUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class QuestionController {

    private final QuestionService questionService;
    private final QuestionAnswerService questionAnswerService;

    @PutMapping("/question/{id}")
    public ApiResponse<?> questionModify(@PathVariable Long id,
                                         @Valid @RequestBody QuestionModifyRequest dto) {
        questionService.modifyQuestion(dto.toServiceRequest(id));
        return ApiUtils.success(dto);
    }

    @PostMapping("/question/answers")
    public ApiResponse<?> questionAnswersAdd(@PathVariable Long surveyId,
                                             @Valid @RequestBody List<QuestionAnswerCreateRequest> dto,
                                             Authentication authentication) {
        String userEmail = authentication.getName();

        return ApiUtils.success(
                questionAnswerService.addQuestionAnswer(surveyId, userEmail,
                        dto.stream()
                                .map(QuestionAnswerCreateRequest::toServiceRequest)
                                .collect(Collectors.toList())));
    }
}
