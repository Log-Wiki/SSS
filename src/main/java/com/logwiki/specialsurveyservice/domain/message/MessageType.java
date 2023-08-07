package com.logwiki.specialsurveyservice.domain.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@lombok.Generated
public enum MessageType {

    @JsonProperty("SHORTMESSAGE")
    SHORTMESSAGE("SMS"),
    @JsonProperty("LONGMESSAGE")
    LONGMESSAGE("LMS"),
    @JsonProperty("MULTIMEDIAMESSAGE")
    MULTIMEDIAMESSAGE("MMS");

    private final String text;

    @JsonCreator
    public static com.logwiki.specialsurveyservice.domain.message.MessageType of(String s) {
        return com.logwiki.specialsurveyservice.domain.message.MessageType.valueOf(s);
    }

}
