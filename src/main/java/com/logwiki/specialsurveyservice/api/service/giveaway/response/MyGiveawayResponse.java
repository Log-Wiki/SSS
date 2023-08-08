package com.logwiki.specialsurveyservice.api.service.giveaway.response;

import com.logwiki.specialsurveyservice.domain.giveaway.Giveaway;
import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayType;
import com.logwiki.specialsurveyservice.domain.surveyresult.SurveyResult;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MyGiveawayResponse {

    private boolean win;
    private boolean userCheck;
    private Long surveyId;
    private String surveyTitle;
    private double probabilty;
    private Long giveawayId;
    private GiveawayType giveawayType;
    private String giveawayName;
    private String surveyWriter;
    private LocalDateTime answerDateTime;

    @Builder
    private MyGiveawayResponse(Long surveyId, boolean win, boolean userCheck, String surveyTitle, Long giveawayId,
            GiveawayType giveawayType, String giveawayName, String surveyWriter, LocalDateTime answerDateTime, double probabilty) {
        this.win = win;
        this.surveyId = surveyId;
        this.userCheck = userCheck;
        this.surveyTitle = surveyTitle;
        this.giveawayId = giveawayId;
        this.giveawayType = giveawayType;
        this.giveawayName = giveawayName;
        this.surveyWriter = surveyWriter;
        this.answerDateTime = answerDateTime;
        this.probabilty = probabilty;
    }

    public static MyGiveawayResponse of(SurveyResult surveyResult, Giveaway giveaway, String surveyWriter, double probabilty) {

        return MyGiveawayResponse.builder()
                .win(surveyResult.isWin())
                .surveyId(surveyResult.getSurvey().getId())
                .userCheck(surveyResult.isUserCheck())
                .surveyTitle(surveyResult.getSurvey().getTitle())
                .giveawayId(giveaway.getId())
                .giveawayType(giveaway.getGiveawayType())
                .giveawayName(giveaway.getName())
                .surveyWriter(surveyWriter)
                .answerDateTime(surveyResult.getAnswerDateTime())
                .probabilty(probabilty)
                .build();
    }


}
