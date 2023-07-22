package com.logwiki.specialsurveyservice.domain.surveyresult;

import com.logwiki.specialsurveyservice.domain.account.Account;
import com.logwiki.specialsurveyservice.domain.survey.Survey;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class SurveyResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean isWin;

    private LocalDateTime endTime;

    private Long submitOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    private Survey survey;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    @Builder
    public SurveyResult(Boolean isWin, LocalDateTime endTime, Long submitOrder, Survey survey, Account account) {
        this.isWin = isWin;
        this.endTime = endTime;
        this.submitOrder = submitOrder;
        this.survey = survey;
        this.account = account;
    }
}
