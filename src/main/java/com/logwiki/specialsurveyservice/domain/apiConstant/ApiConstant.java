package com.logwiki.specialsurveyservice.domain.apiConstant;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApiConstant {
    IamportApiKey("7343166512186774"),
    IamportApiSecretKey("AFgMzlcN1niQMqo4QjGlV7hyBuQOmuKFnKHqtHRzeCme37sbo6b8zKRhfAMtQKzX5BgPSrbE0pfdAEDK"),
    StoreKey("imp36635434"),
    NaverAccessKey("mgIU1Z68m4jAneZU7f37"),
    NaverSecretKey("DpDVZgrDqbK5neoRR9oqlAAReOn6vBPzAmsMEBvj"),
    NaverMessageServiceKey("ncp:sms:kr:299147416082:supersurveyservice");


    private final String text;

    @JsonCreator
    public static ApiConstant from(String s) {
        return ApiConstant.from(s);
    }
}

