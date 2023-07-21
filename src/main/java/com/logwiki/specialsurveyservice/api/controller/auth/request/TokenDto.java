package com.logwiki.specialsurveyservice.api.controller.auth.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenDto {

    private String token;

    public TokenDto(String token) {
        this.token = token;
    }
}
