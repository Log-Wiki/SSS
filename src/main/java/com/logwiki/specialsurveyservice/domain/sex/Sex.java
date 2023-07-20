package com.logwiki.specialsurveyservice.domain.sex;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Sex {

  @JsonProperty("M")
  MALE("남자"),
  @JsonProperty("F")
  FEMALE("여자");

  private final String text;
}
