package com.logwiki.specialsurveyservice.domain.surveycategory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SurveyCategoryType {

    TIME_ATTACK("타임어택");

    private final String text;
}
