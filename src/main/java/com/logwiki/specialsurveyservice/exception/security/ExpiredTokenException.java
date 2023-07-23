package com.logwiki.specialsurveyservice.exception.security;

import com.logwiki.specialsurveyservice.exception.BaseException;

public class ExpiredTokenException extends BaseException {

    public ExpiredTokenException(String message) {
        super(message, 1000);
    }
}
