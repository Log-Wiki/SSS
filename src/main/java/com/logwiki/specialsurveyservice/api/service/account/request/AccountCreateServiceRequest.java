package com.logwiki.specialsurveyservice.api.service.account.request;

import com.logwiki.specialsurveyservice.domain.sex.Sex;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AccountCreateServiceRequest {

  private String email;

  private String password;

  private Sex sex;

  private String name;

  private String phoneNumber;

  private LocalDateTime birthday;

  @Builder
  private AccountCreateServiceRequest(String email, String password, Sex sex, String name, String phoneNumber, LocalDateTime birthday) {
    this.email = email;
    this.password = password;
    this.sex = sex;
    this.name = name;
    this.phoneNumber = phoneNumber;
    this.birthday = birthday;
  }
}
