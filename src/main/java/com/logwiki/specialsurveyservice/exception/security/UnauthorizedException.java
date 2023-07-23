package com.logwiki.specialsurveyservice.exception.security;

import com.logwiki.specialsurveyservice.exception.BaseException;

public class UnauthorizedException extends BaseException {

    public UnauthorizedException(String message) {
        super(message, 1000);
    }
}
