package com.logwiki.specialsurveyservice.api.service.account.response;

import com.logwiki.specialsurveyservice.domain.account.Account;
import com.logwiki.specialsurveyservice.domain.sex.Sex;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AccountResponse {

  private String email;
  private Sex sex;
  private String name;
  private String phoneNumber;
  private LocalDateTime birthday;

  @Builder
  private AccountResponse(String email, Sex sex, String name, String phoneNumber, LocalDateTime birthday) {
    this.email = email;
    this.sex = sex;
    this.name = name;
    this.phoneNumber = phoneNumber;
    this.birthday = birthday;
  }

  public static AccountResponse from(Account account) {
    if(account == null)
      return null;

    return AccountResponse.builder()
        .email(account.getEmail())
        .sex(account.getSex())
        .name(account.getName())
        .phoneNumber(account.getPhoneNumber())
        .birthday(account.getBirthday())
        .build();
  }
}
