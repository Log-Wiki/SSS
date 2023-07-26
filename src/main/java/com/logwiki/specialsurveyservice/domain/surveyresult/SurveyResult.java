package com.logwiki.specialsurveyservice.domain.surveyresult;

import com.logwiki.specialsurveyservice.domain.BaseEntity;
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
public class SurveyResult extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean isWin;

    private LocalDateTime endTime;

    private int submitOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    private Survey survey;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    @Builder
    private SurveyResult(Boolean isWin, LocalDateTime endTime, int submitOrder, Survey survey, Account account) {
        this.isWin = isWin;
        this.endTime = endTime;
        this.submitOrder = submitOrder;
        this.survey = survey;
        this.account = account;
    }

    public static SurveyResult create(Boolean isWin, LocalDateTime endTime, int submitOrder, Survey survey, Account account) {
        return SurveyResult.builder()
                .isWin(isWin)
                .endTime(endTime)
                .submitOrder(submitOrder)
                .survey(survey)
                .account(account)
                .build();
    }
}
