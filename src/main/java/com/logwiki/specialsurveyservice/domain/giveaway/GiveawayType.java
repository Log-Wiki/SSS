package com.logwiki.specialsurveyservice.domain.giveaway;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GiveawayType {

    COFFEE("커피");

    private final String text;
}
