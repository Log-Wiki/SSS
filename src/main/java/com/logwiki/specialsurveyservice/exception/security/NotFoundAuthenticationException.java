package com.logwiki.specialsurveyservice.exception.security;

import com.logwiki.specialsurveyservice.exception.BaseException;

public class NotFoundAuthenticationException extends BaseException {

    public NotFoundAuthenticationException(String message) {
        super(message, 1000);
    }
}
