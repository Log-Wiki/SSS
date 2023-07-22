package com.logwiki.specialsurveyservice.api.service.account.response;

import com.logwiki.specialsurveyservice.domain.account.Account;
import com.logwiki.specialsurveyservice.domain.gender.Gender;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AccountResponse {

    private String email;
    private Gender gender;
    private String name;
    private String phoneNumber;
    private LocalDate birthday;

    @Builder
    private AccountResponse(String email, Gender gender, String name, String phoneNumber,
            LocalDate birthday) {
        this.email = email;
        this.gender = gender;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.birthday = birthday;
    }

    public static AccountResponse from(Account account) {
      if (account == null) {
        return null;
      }

        return AccountResponse.builder()
                .email(account.getEmail())
                .gender(account.getGender())
                .name(account.getName())
                .phoneNumber(account.getPhoneNumber())
                .birthday(account.getBirthday())
                .build();
    }
}
