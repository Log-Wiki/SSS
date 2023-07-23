package com.logwiki.specialsurveyservice.exception.security;

import com.logwiki.specialsurveyservice.exception.BaseException;

public class ForbiddenException extends BaseException {

    public ForbiddenException(String message) {
        super(message, 1000);
    }
}
