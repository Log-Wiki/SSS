package com.logwiki.specialsurveyservice.status;

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
