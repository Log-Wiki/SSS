package com.logwiki.specialsurveyservice.api.utils;

import com.logwiki.specialsurveyservice.exception.BaseException;
import com.logwiki.specialsurveyservice.exception.ValidationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    @ResponseBody
    public ApiResponse<?> handleAllExceptions(BaseException ex) {
        return ApiUtils.error(ex.getApiError());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseBody
    public ApiResponse<?> handleAllExceptions(ValidationException ex) {
        for (FieldError fieldError : ex.getFieldErrors()) {
            String fieldName = fieldError.getField();
            String errorMessage = fieldError.getDefaultMessage();
            Object rejectedValue = fieldError.getRejectedValue();
            return ApiUtils.error(errorMessage, 1000);
        }
        return ApiUtils.error("Valid 에러", 1000);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<?> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        return ApiUtils.error(
                e.getBindingResult().getAllErrors().get(0).getDefaultMessage(),
                1000
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResponse<?> illegalArgumentException(IllegalArgumentException e) {
        return ApiUtils.error(
                e.getMessage(),
                1000
        );
    }
}