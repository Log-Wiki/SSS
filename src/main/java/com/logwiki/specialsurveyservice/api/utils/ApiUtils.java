package com.logwiki.specialsurveyservice.api.utils;

public class ApiUtils {

    public static <T> ApiResponse<T> success(T response) {
        return new ApiResponse<>(true, response, null, null);
    }

    public static ApiResponse<?> error(String message, int status) {
        return new ApiResponse<>(false, null, new ApiError(message, status), null);
    }

    public static ApiResponse<?> error(ApiError apiError) {
        return new ApiResponse<>(false, null, apiError, null);
    }
}
