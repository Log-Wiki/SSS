package com.logwiki.specialsurveyservice.api.service.surveyresult.response;

import com.logwiki.specialsurveyservice.domain.giveaway.Giveaway;
import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayType;
import com.logwiki.specialsurveyservice.domain.surveyresult.SurveyResult;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MyGiveawayResponse {

    private boolean win;
    private boolean userCheck;
    private String surveyTitle;
    private Long giveawayId;
    private GiveawayType giveawayType;
    private String giveawayName;
    private String surveyWriter;
    private LocalDateTime answerDateTime;

    @Builder
    private MyGiveawayResponse(boolean win, boolean userCheck, String surveyTitle, Long giveawayId,
            GiveawayType giveawayType, String giveawayName, String surveyWriter, LocalDateTime answerDateTime) {
        this.win = win;
        this.userCheck = userCheck;
        this.surveyTitle = surveyTitle;
        this.giveawayId = giveawayId;
        this.giveawayType = giveawayType;
        this.giveawayName = giveawayName;
        this.surveyWriter = surveyWriter;
        this.answerDateTime = answerDateTime;
    }

    public static MyGiveawayResponse of(SurveyResult surveyResult, Giveaway giveaway, String surveyWriter) {

        return MyGiveawayResponse.builder()
                .win(surveyResult.isWin())
                .userCheck(surveyResult.isUserCheck())
                .surveyTitle(surveyResult.getSurvey().getTitle())
                .giveawayId(giveaway.getId())
                .giveawayType(giveaway.getGiveawayType())
                .giveawayName(giveaway.getName())
                .surveyWriter(surveyWriter)
                .answerDateTime(surveyResult.getAnswerDateTime())
                .build();
    }


}
