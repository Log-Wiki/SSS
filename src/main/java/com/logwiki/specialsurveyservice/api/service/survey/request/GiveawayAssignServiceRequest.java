package com.logwiki.specialsurveyservice.api.service.survey.request;

import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GiveawayAssignServiceRequest {

    private Long id;

    private GiveawayType giveawayType;

    private String name;

    private int count;

    @Builder
    private GiveawayAssignServiceRequest(Long id, GiveawayType giveawayType, String name, int count) {
        this.id = id;
        this.giveawayType = giveawayType;
        this.name = name;
        this.count = count;
    }
}
