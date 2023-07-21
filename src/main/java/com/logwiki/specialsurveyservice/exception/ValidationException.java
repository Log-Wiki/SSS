package com.logwiki.specialsurveyservice.exception;

import java.util.List;
import lombok.Getter;
import org.springframework.validation.FieldError;

@Getter
public class ValidationException extends RuntimeException {

    private List<FieldError> fieldErrors;

    public ValidationException() {
    }

    public ValidationException(String message) {
        super(message); // RuntimeException 클래스의 생성자를 호출합니다.
    }

    public ValidationException(List<FieldError> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }

}
