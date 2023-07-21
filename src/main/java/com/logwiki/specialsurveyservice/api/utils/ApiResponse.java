package com.logwiki.specialsurveyservice.api.utils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse<T> {

    private final boolean success;
    private final T response;
    private final ApiError apiError;
    private final String accessToken;

    public ApiResponse(boolean success, T response, ApiError apiError, String accessToken) {
        this.success = success;
        this.response = response;
        this.apiError = apiError;
        this.accessToken = accessToken;
    }

}

