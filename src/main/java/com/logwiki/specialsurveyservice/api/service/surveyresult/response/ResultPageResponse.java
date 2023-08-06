package com.logwiki.specialsurveyservice.api.service.surveyresult.response;

import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResultPageResponse {
    private boolean isWin;
    private GiveawayType giveawayType;
    private String giveawayName;

    @Builder
    public ResultPageResponse(boolean isWin, GiveawayType giveawayType, String giveawayName) {
        this.isWin = isWin;
        this.giveawayType = giveawayType;
        this.giveawayName = giveawayName;
    }
}
