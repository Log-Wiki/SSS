package com.logwiki.specialsurveyservice.api.controller.auth.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RefreshRequest {

    @NotEmpty(message = "이메일은 필수입니다.")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$", message = "이메일 형식에 맞지 않습니다.")
    private String email;

    @NotEmpty(message = "패스워드는 필수입니다.")
    @Size(min = 3, max = 30, message = "패스워드의 길이는 3이상 30이하 입니다.")
    private String password;

    @NotEmpty(message = "refresh token은 필수입니다.")
    private String refreshToken;

    @Builder
    private RefreshRequest(String email, String password, String refreshToken) {
        this.email = email;
        this.password = password;
        this.refreshToken = refreshToken;
    }
}
