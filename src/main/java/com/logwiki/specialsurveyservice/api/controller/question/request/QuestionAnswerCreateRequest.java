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

    private Long multipleChoiceAnswer;

    private String shorFormAnswer;

    @NotNull(message = "문항 번호는 필수입니다.")
    private Long questionNumber;

    @NotNull(message = "설문 번호는 필수입니다.")
    private Long surveyNumber;

    @Builder
    public QuestionAnswerCreateRequest(Long multipleChoiceAnswer, String shorFormAnswer,
                                       Long questionNumber, Long surveyNumber) {
        this.multipleChoiceAnswer = multipleChoiceAnswer;
        this.shorFormAnswer = shorFormAnswer;
        this.questionNumber = questionNumber;
        this.surveyNumber = surveyNumber;
    }

    public QuestionAnswerCreateServiceRequest toServiceRequest() {
        if (multipleChoiceAnswer != null && shorFormAnswer != null) {
            throw new BaseException("객관식 또는 주관식 답변 중 하나만 가질 수 없습니다.", 1000);
        } else if (shorFormAnswer == null && multipleChoiceAnswer == null) {
            throw new BaseException("주관식 또는 객관식 답변이 필요합니다.", 1000);
        }
        return QuestionAnswerCreateServiceRequest.builder()
                .multipleChoiceAnswer(multipleChoiceAnswer)
                .shorFormAnswer(shorFormAnswer)
                .questionNumber(questionNumber)
                .build();
    }
}
