package com.logwiki.specialsurveyservice.exception;

public class NonExistOrderException extends BaseException {

    public NonExistOrderException(String message) {
        super(message , 2020);
    }
}
