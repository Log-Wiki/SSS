package com.logwiki.specialsurveyservice.api.service.targetnumber.request;

import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TargetNumberCreateServiceRequest {

    private int headCount;

    private Map<Long, Integer> giveaways;

    @Builder
    private TargetNumberCreateServiceRequest(int headCount, Map<Long, Integer> giveaways) {
        this.headCount = headCount;
        this.giveaways = giveaways;
    }

    public static TargetNumberCreateServiceRequest create(int headCount, Map<Long, Integer> giveaways) {
        return TargetNumberCreateServiceRequest.builder()
                .headCount(headCount)
                .giveaways(giveaways)
                .build();
    }
}
