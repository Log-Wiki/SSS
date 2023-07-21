package com.logwiki.specialsurveyservice.domain.gender;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Gender {

    @JsonProperty("MALE")
    MALE("남자"),
    @JsonProperty("FEMALE")
    FEMALE("여자");

    private final String text;

    @JsonCreator
    public static Gender from(String s) {
        return Gender.valueOf(s);
    }
}
