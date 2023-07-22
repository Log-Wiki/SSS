package com.logwiki.specialsurveyservice.domain.surveytarget;

import com.logwiki.specialsurveyservice.domain.accountcode.AccountCode;
import com.logwiki.specialsurveyservice.domain.survey.Survey;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class SurveyTarget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Survey survey;

    @ManyToOne(fetch = FetchType.LAZY)
    private AccountCode accountCode;

    @Builder
    public SurveyTarget(Survey survey, AccountCode accountCode) {
        this.survey = survey;
        this.accountCode = accountCode;
    }
}
