package com.logwiki.specialsurveyservice.domain.questionanswer;

import com.logwiki.specialsurveyservice.domain.account.Account;
import com.logwiki.specialsurveyservice.domain.question.Question;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class QuestionAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long answerNumber;

    private String shortFormAnswer;

    @ManyToOne(fetch = FetchType.LAZY)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    @Builder
    public QuestionAnswer(Long answerNumber, String shortFormAnswer, Question question, Account account) {
        this.answerNumber = answerNumber;
        this.shortFormAnswer = shortFormAnswer;
        this.question = question;
        this.account = account;
    }

}
