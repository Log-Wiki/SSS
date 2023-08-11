package com.logwiki.specialsurveyservice.domain.survey;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@lombok.Generated
public enum AnswerPossibleType {

    @JsonProperty("TIMEOVER")
    TIMEOVER("설문시간마감"),
    @JsonProperty("TIMEBEFORE")
    TIMEBEFORE("설문시간이전"),
    @JsonProperty("DIDANSWER")
    DIDANSWER("이미응답하였음"),
    @JsonProperty("HEADFULL")
    HEADFULL("응답인원마감"),
    @JsonProperty("TYPENOTMATCH")
    TYPENOTMATCH("설문대상이아님"),
    @JsonProperty("CANANSWER")
    CANANSWER("설문응답가능"),
    @JsonProperty("NEEDLOGIN")
    NEEDLOGIN("로그인하지않음");

    private final String text;

    @JsonCreator
    public static AnswerPossibleType from(String s) {
        return AnswerPossibleType.valueOf(s);
    }
}
