package com.logwiki.specialsurveyservice.api.service.question.request;

import com.logwiki.specialsurveyservice.domain.account.Account;
import com.logwiki.specialsurveyservice.domain.question.Question;
import com.logwiki.specialsurveyservice.domain.questionanswer.QuestionAnswer;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class QuestionAnswerCreateServiceRequest {

    private Long multipleChoiceAnswer;

    private String shorFormAnswer;

    private Long questionNumber;

    private Long surveyNumber;

    @Builder
    public QuestionAnswerCreateServiceRequest(Long multipleChoiceAnswer, String shorFormAnswer,
                                              Long questionNumber, Long surveyNumber) {
        this.multipleChoiceAnswer = multipleChoiceAnswer;
        this.shorFormAnswer = shorFormAnswer;
        this.questionNumber = questionNumber;
        this.surveyNumber = surveyNumber;
    }

    public QuestionAnswer toEntity(Question question, Account account) {
        return QuestionAnswer.builder()
                .account(account)
                .question(question)
                .shortFormAnswer(shorFormAnswer)
                .answerNumber(multipleChoiceAnswer)
                .build();
    }
}
