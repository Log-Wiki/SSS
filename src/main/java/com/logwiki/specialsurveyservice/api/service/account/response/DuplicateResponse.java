package com.logwiki.specialsurveyservice.api.service.account.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class DuplicateResponse {

    private boolean duplicate;

    @Builder
    private DuplicateResponse(boolean duplicate) {
        this.duplicate = duplicate;
    }
}
