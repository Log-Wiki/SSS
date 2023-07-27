package com.logwiki.specialsurveyservice.api.controller.question;

import com.logwiki.specialsurveyservice.api.controller.question.request.QuestionAnswerCreateRequest;
import com.logwiki.specialsurveyservice.api.controller.question.request.QuestionAnswersCreateRequest;
import com.logwiki.specialsurveyservice.api.controller.question.request.QuestionModifyRequest;
import com.logwiki.specialsurveyservice.api.service.question.QuestionAnswerService;
import com.logwiki.specialsurveyservice.api.service.question.QuestionService;
import com.logwiki.specialsurveyservice.api.utils.ApiResponse;
import com.logwiki.specialsurveyservice.api.utils.ApiUtils;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ApiResponse<?> questionAnswersAdd(@RequestParam Long surveyId,
            @Valid @RequestBody QuestionAnswersCreateRequest dto,
            Authentication authentication) {
        String userEmail = authentication.getName();
        LocalDateTime writeDate = LocalDateTime.now();
        return ApiUtils.success(
                questionAnswerService.addQuestionAnswer(writeDate, surveyId, userEmail,
                        dto.getAnswers().stream()
                                .map(QuestionAnswerCreateRequest::toServiceRequest)
                                .collect(Collectors.toList())));
    }
}
