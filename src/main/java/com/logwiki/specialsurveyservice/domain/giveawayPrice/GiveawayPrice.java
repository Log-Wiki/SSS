package com.logwiki.specialsurveyservice.domain.giveawayPrice;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GiveawayPrice {

  @JsonProperty("COFFEE")
  COFFEE(1300),
  @JsonProperty("CHICKEN")
  CHICKEN(20000);

  private final Integer price;

  @JsonCreator
  public static GiveawayPrice from(String s) {
    return GiveawayPrice.valueOf(s);
  }
}
