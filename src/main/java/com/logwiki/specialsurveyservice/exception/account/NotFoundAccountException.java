package com.logwiki.specialsurveyservice.exception.account;

import com.logwiki.specialsurveyservice.exception.BaseException;

public class NotFoundAccountException extends BaseException {

    public NotFoundAccountException(String message) {
        super(message, 1000);
    }
}
