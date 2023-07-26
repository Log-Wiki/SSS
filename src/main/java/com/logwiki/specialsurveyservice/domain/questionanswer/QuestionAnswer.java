package com.logwiki.specialsurveyservice.domain.questionanswer;

import com.logwiki.specialsurveyservice.domain.BaseEntity;
import com.logwiki.specialsurveyservice.domain.account.Account;
import com.logwiki.specialsurveyservice.domain.question.Question;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class QuestionAnswer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long answerNumber;

    private String shortFormAnswer;

    @ManyToOne(fetch = FetchType.LAZY)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    private LocalDateTime writeDate;

    @Builder
    public QuestionAnswer(Long answerNumber, String shortFormAnswer, Question question,
            Account account, LocalDateTime writeDate) {
        this.answerNumber = answerNumber;
        this.shortFormAnswer = shortFormAnswer;
        this.question = question;
        this.account = account;
        this.writeDate = writeDate;
    }

    public void setWriteDate(LocalDateTime writeDate) {
        this.writeDate = writeDate;
    }

}
