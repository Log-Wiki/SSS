package com.logwiki.specialsurveyservice.domain.giveaway;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@lombok.Generated
public enum GiveawayType {

    @JsonProperty("COFFEE")
    COFFEE("커피"),
    @JsonProperty("CHICKEN")
    CHICKEN("치킨");

    private final String text;

    @JsonCreator
    public static GiveawayType from(String s) {
        return GiveawayType.valueOf(s);
    }
}
