package com.logwiki.specialsurveyservice.domain;

public enum Status {
  ACTIVE("Active"),
  INACTIVE("Inactive");

  private final String value;

  Status(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
