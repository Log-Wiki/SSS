package com.logwiki.specialsurveyservice.api.controller.account.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AccountUpdateRequest {

    @Nullable
    @Size(min = 3, max = 30, message = "패스워드의 길이는 3이상 30이하 입니다.")
    private String password;

    @Nullable
    private String name;

    @Nullable
    @Pattern(regexp = "^01(?:0|1|[6-9])[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$", message = "휴대폰 번호는 10~11자리의 숫자만 입력가능합니다.")
    private String phoneNumber;

    @Builder
    private AccountUpdateRequest(String password, String name, String phoneNumber) {
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public void encodePassword(String encodedPassword) {
        this.password = encodedPassword;
    }
}
