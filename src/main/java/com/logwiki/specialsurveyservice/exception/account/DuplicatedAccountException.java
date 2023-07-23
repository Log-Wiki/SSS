package com.logwiki.specialsurveyservice.exception.account;

import com.logwiki.specialsurveyservice.exception.BaseException;

public class DuplicatedAccountException extends BaseException {

    public DuplicatedAccountException(String message) {
        super(message, 1000);
    }
}
