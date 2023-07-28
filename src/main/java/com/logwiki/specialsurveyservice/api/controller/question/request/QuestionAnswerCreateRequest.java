package com.logwiki.specialsurveyservice.api.controller.question.request;

import com.logwiki.specialsurveyservice.api.service.question.request.QuestionAnswerCreateServiceRequest;
import com.logwiki.specialsurveyservice.exception.BaseException;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class QuestionAnswerCreateRequest {

    @NotNull(message = "문항 아이디는 필수입니다.")
    private Long questionId;

    private Long multipleChoiceAnswer;

    private String shorFormAnswer;


    @Builder
    public QuestionAnswerCreateRequest(Long questionId, Long multipleChoiceAnswer,
                                       String shorFormAnswer,
                                       Long questionNumber) {
        this.questionId = questionId;
        this.multipleChoiceAnswer = multipleChoiceAnswer;
        this.shorFormAnswer = shorFormAnswer;
    }

    public QuestionAnswerCreateServiceRequest toServiceRequest() {
        if (multipleChoiceAnswer != null && shorFormAnswer != null) {
            throw new BaseException("객관식 또는 주관식 답변 중 하나만 가질 수 없습니다.", 3006);
        } else if (shorFormAnswer == null && multipleChoiceAnswer == null) {
            throw new BaseException("주관식 또는 객관식 답변이 필요합니다.", 3007);
        }
        return QuestionAnswerCreateServiceRequest.builder()
                .multipleChoiceAnswer(multipleChoiceAnswer)
                .shorFormAnswer(shorFormAnswer)
                .questionId(questionId)
                .build();
    }
}
