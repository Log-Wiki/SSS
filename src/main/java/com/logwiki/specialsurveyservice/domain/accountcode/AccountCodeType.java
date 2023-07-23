package com.logwiki.specialsurveyservice.domain.accountcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountCodeType {

    MAN("남자");

    private final String text;
}
