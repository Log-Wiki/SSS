package com.logwiki.specialsurveyservice.exception;

import com.logwiki.specialsurveyservice.utils.ApiError;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BaseException extends RuntimeException {

  private ApiError apiError;

  public BaseException(String message) {
    super(message); // RuntimeException 클래스의 생성자를 호출합니다.
  }

  public BaseException(ApiError apiError) {
    this.apiError = apiError;
  }

}
