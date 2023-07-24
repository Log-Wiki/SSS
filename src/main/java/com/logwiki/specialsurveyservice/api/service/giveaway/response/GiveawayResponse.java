package com.logwiki.specialsurveyservice.api.service.giveaway.response;

import com.logwiki.specialsurveyservice.domain.giveaway.Giveaway;
import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GiveawayResponse {

    private Long id;
    private GiveawayType giveawayType;
    private String name;
    private int price;

    @Builder
    private GiveawayResponse(Long id, GiveawayType giveawayType, String name, int price) {
        this.id = id;
        this.giveawayType = giveawayType;
        this.name = name;
        this.price = price;
    }

    public static GiveawayResponse of(Giveaway giveaway) {
        return GiveawayResponse.builder()
                .id(giveaway.getId())
                .giveawayType(giveaway.getGiveawayType())
                .name(giveaway.getName())
                .price(giveaway.getPrice())
                .build();
    }
}
