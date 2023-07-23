package com.logwiki.specialsurveyservice.domain.questioncategory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QuestionCategoryType {

    SHORT_FORM("주관식"), MULTIPLE_CHOICE("객관식");

    private final String text;
}
