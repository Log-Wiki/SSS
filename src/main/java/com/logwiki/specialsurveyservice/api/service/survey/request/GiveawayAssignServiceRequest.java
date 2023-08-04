package com.logwiki.specialsurveyservice.api.service.survey.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GiveawayAssignServiceRequest {

    private Long id;

    private int count;

    @Builder
    private GiveawayAssignServiceRequest(Long id, int count) {
        this.id = id;
        this.count = count;
    }
}
