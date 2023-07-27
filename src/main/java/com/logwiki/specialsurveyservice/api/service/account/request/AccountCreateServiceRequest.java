package com.logwiki.specialsurveyservice.api.service.account.request;

import com.logwiki.specialsurveyservice.domain.accountcode.AccountCodeType;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AccountCreateServiceRequest {

    private String email;

    private String password;

    private AccountCodeType gender;

    private AccountCodeType age;

    private String name;

    private String phoneNumber;

    private LocalDate birthday;

    @Builder
    private AccountCreateServiceRequest(String email, String password, AccountCodeType gender,
            AccountCodeType age, String name, String phoneNumber, LocalDate birthday) {
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.age = age;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.birthday = birthday;
    }
}
