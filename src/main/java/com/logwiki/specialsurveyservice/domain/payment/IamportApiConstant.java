package com.logwiki.specialsurveyservice.domain.payment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IamportApiConstant {
    IamportApiKey("7343166512186774"),
    IamportApiSecretKey("AFgMzlcN1niQMqo4QjGlV7hyBuQOmuKFnKHqtHRzeCme37sbo6b8zKRhfAMtQKzX5BgPSrbE0pfdAEDK"),
    StoreKey("imp36635434");

    private final String text;

    @JsonCreator
    public static IamportApiConstant from(String s) {
        return IamportApiConstant.from(s);
    }
}

