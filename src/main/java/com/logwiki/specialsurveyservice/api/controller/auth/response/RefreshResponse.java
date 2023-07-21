package com.logwiki.specialsurveyservice.api.controller.auth.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RefreshResponse {

    private String accessToken;

    public RefreshResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}
