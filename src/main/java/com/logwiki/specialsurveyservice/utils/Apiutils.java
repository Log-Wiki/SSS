package com.logwiki.specialsurveyservice.utils;

public class Apiutils {

  public static <T> ApiResult<T> success(T response) {
    return new ApiResult<>(true, response, null);
  }

  public static ApiResult<?> error(String message, int status) {
    return new ApiResult<>(false, null, new ApiError(message, status));
  }

  public static ApiResult<?> error(ApiError apiError) {
    return new ApiResult<>(false, null, apiError);
  }
}
