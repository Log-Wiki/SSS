package com.logwiki.specialsurveyservice.api.controller.surveyresult.response;

import com.logwiki.specialsurveyservice.domain.surveyresult.SurveyResult;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class SurveyResultResponse {

    private Long id;
    private boolean win;
    private LocalDateTime endTime;
    private int submitOrder;
    private boolean userCheck;

    @Builder
    public SurveyResultResponse(Long id, boolean win, LocalDateTime endTime, int submitOrder, boolean userCheck) {
        this.id = id;
        this.win = win;
        this.endTime = endTime;
        this.submitOrder = submitOrder;
        this.userCheck = userCheck;
    }

    public static SurveyResultResponse from(SurveyResult surveyResult) {
        return SurveyResultResponse.builder()
                .id(surveyResult.getId())
                .win(surveyResult.isWin())
                .endTime(surveyResult.getEndTime())
                .submitOrder(surveyResult.getSubmitOrder())
                .userCheck(surveyResult.isUserCheck())
                .build();
    }
}
