package com.logwiki.specialsurveyservice.domain.questionanswer;

import com.logwiki.specialsurveyservice.domain.BaseEntity;
import com.logwiki.specialsurveyservice.domain.account.Account;
import com.logwiki.specialsurveyservice.domain.question.Question;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@lombok.Generated
public class QuestionAnswer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long answerNumber;

    private String shortFormAnswer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    private LocalDateTime answerDateTime;

    @Builder
    public QuestionAnswer(Long answerNumber, String shortFormAnswer, Question question,
            Account account, LocalDateTime answerDateTime) {
        this.answerNumber = answerNumber;
        this.shortFormAnswer = shortFormAnswer;
        this.question = question;
        this.account = account;
        this.answerDateTime = answerDateTime;
    }

    public void setAnswerDateTime(LocalDateTime writeDate) {
        this.answerDateTime = writeDate;
    }

}
