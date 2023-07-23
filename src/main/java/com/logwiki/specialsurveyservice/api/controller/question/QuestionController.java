package com.logwiki.specialsurveyservice.api.controller.question;

import com.logwiki.specialsurveyservice.api.controller.question.request.MultipleChoiceCreateRequest;
import com.logwiki.specialsurveyservice.api.controller.question.request.QuestionModifyRequest;
import com.logwiki.specialsurveyservice.api.service.question.QuestionService;
import com.logwiki.specialsurveyservice.api.utils.ApiResponse;
import com.logwiki.specialsurveyservice.api.utils.ApiUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping("/question")
    public ApiResponse<?> question(
            @Valid @RequestBody MultipleChoiceCreateRequest dto) {
        return ApiUtils.success(dto);
    }

    @PutMapping("/question/{id}")
    public ApiResponse<?> questionModify(@PathVariable Long id,
            @Valid @RequestBody QuestionModifyRequest dto) {
        questionService.modifyQuestion(dto.toServiceRequest(id));
        return ApiUtils.success(dto);
    }
}
