package com.logwiki.specialsurveyservice.api.service.surveyresult.response;

import com.logwiki.specialsurveyservice.domain.account.Account;
import com.logwiki.specialsurveyservice.domain.survey.Survey;
import com.logwiki.specialsurveyservice.domain.surveyresult.SurveyResult;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SurveyResultResponse {

    private Long id;
    private Boolean isWin;
    private LocalDateTime endTime;
    private int submitOrder;
    private Survey survey;
    private Account account;

    @Builder
    private SurveyResultResponse(Long id, Boolean isWin, LocalDateTime endTime, int submitOrder, Survey survey, Account account) {
        this.id = id;
        this.isWin = isWin;
        this.endTime = endTime;
        this.submitOrder = submitOrder;
        this.survey = survey;
        this.account = account;
    }

    public static SurveyResultResponse of(SurveyResult surveyResult) {
        return SurveyResultResponse.builder()
                .id(surveyResult.getId())
                .isWin(surveyResult.getIsWin())
                .endTime(surveyResult.getEndTime())
                .submitOrder(surveyResult.getSubmitOrder())
                .survey(surveyResult.getSurvey())
                .account(surveyResult.getAccount())
                .build();
    }
}
