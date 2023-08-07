package com.logwiki.specialsurveyservice.domain.accountcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@lombok.Generated
public enum AccountCodeType {

    MAN("남자"), WOMAN("여자"), UNDER_TEENS("10세 미만"),
    TEENS("10대"), TWENTIES("20대"), THIRTIES("30대"),
    FORTIES("40대"), FIFTIES("50대"), SIXTIES("60대");
    private final String text;
}
