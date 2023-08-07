package com.logwiki.specialsurveyservice.domain.surveycategory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@lombok.Generated
public enum SurveyCategoryType {

    NORMAL("일반"), INSTANT_WIN("즉시 당첨");

    private final String text;
}
