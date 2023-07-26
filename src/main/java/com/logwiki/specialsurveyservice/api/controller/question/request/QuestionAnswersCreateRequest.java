package com.logwiki.specialsurveyservice.api.controller.question.request;

import jakarta.validation.Valid;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class QuestionAnswersCreateRequest {

    @Valid
    private List<QuestionAnswerCreateRequest> answers;

    @Builder
    public QuestionAnswersCreateRequest(List<QuestionAnswerCreateRequest> answers) {
        this.answers = answers;
    }
}
