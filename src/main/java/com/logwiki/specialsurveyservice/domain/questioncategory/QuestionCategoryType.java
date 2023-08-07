package com.logwiki.specialsurveyservice.domain.questioncategory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@lombok.Generated
public enum QuestionCategoryType {

    SHORT_FORM("주관식"), MULTIPLE_CHOICE("객관식"),
    DATE_FORM("날짜"), TIME_FORM("시간"), DROP_DOWN("드롭다운"),
    CHECK_BOX("체크박스");

    private final String text;
}
