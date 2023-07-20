package com.logwiki.specialsurveyservice.domain.sex;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Sex {

    MALE("남자"),
    FEMALE("여자");

    private final String text;
}
