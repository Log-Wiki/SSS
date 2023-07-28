package com.logwiki.specialsurveyservice.api.service.giveaway.response;

import com.logwiki.specialsurveyservice.domain.surveygiveaway.SurveyGiveaway;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SurveyGiveawayResponse {

    private Long id;
    private int count;
    private GiveawayResponse giveawayResponse;

    @Builder
    public SurveyGiveawayResponse(Long id, int count, GiveawayResponse giveawayResponse) {
        this.id = id;
        this.count = count;
        this.giveawayResponse = giveawayResponse;
    }

    public static SurveyGiveawayResponse from(SurveyGiveaway surveyGiveaway) {
        if (surveyGiveaway == null) {
            return null;
        }
        return SurveyGiveawayResponse.builder()
                .id(surveyGiveaway.getId())
                .count(surveyGiveaway.getCount())
                .giveawayResponse(GiveawayResponse.of(surveyGiveaway.getGiveaway()))
                .build();
    }
}
