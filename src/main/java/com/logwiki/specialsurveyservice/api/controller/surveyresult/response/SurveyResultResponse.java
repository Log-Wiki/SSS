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
    private LocalDateTime answerDateTime;
    private int submitOrder;
    private boolean userCheck;

    @Builder
    public SurveyResultResponse(Long id, boolean win, LocalDateTime answerDateTime, int submitOrder, boolean userCheck) {
        this.id = id;
        this.win = win;
        this.answerDateTime = answerDateTime;
        this.submitOrder = submitOrder;
        this.userCheck = userCheck;
    }

    public static SurveyResultResponse from(SurveyResult surveyResult) {
        return SurveyResultResponse.builder()
                .id(surveyResult.getId())
                .win(surveyResult.isWin())
                .answerDateTime(surveyResult.getAnswerDateTime())
                .submitOrder(surveyResult.getSubmitOrder())
                .userCheck(surveyResult.isUserCheck())
                .build();
    }
}
