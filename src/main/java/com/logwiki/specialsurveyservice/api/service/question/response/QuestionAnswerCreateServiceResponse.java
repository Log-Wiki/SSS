package com.logwiki.specialsurveyservice.api.service.question.response;

import com.logwiki.specialsurveyservice.domain.questionanswer.QuestionAnswer;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class QuestionAnswerCreateServiceResponse {

    private Long id;
    private Long multipleChoiceAnswer;
    private String shortFormAnswer;
    private Long writer;

    @Builder
    public QuestionAnswerCreateServiceResponse(QuestionAnswer questionAnswer) {
        this.id = questionAnswer.getId();
        this.multipleChoiceAnswer = questionAnswer.getAnswerNumber();
        this.shortFormAnswer = questionAnswer.getShortFormAnswer();
        this.writer = questionAnswer.getAccount().getId();
    }
}
