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

    private Long questionId;

    private Long multipleChoiceAnswer;

    private String shorFormAnswer;
    
    @Builder
    public QuestionAnswerCreateServiceRequest(Long questionId, Long multipleChoiceAnswer,
            String shorFormAnswer) {
        this.questionId = questionId;
        this.multipleChoiceAnswer = multipleChoiceAnswer;
        this.shorFormAnswer = shorFormAnswer;
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
