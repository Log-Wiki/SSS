package com.logwiki.specialsurveyservice.exception;

public class NonExistOrderException extends RuntimeException {

    public NonExistOrderException(String message) {
        super(message);
    }
}
