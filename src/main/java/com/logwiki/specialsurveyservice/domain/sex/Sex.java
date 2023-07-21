package com.logwiki.specialsurveyservice.domain.sex;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Sex {

  @JsonProperty("MALE")
  MALE("남자"),
  @JsonProperty("FEMALE")
  FEMALE("여자");

  private final String text;

  @JsonCreator
  public static Sex from(String s) {
    return Sex.valueOf(s);
  }
}
