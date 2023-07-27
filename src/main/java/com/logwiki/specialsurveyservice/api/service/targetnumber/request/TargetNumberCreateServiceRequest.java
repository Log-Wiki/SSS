package com.logwiki.specialsurveyservice.api.service.targetnumber.request;

import com.logwiki.specialsurveyservice.api.service.survey.request.GiveawayAssignServiceRequest;
import com.logwiki.specialsurveyservice.domain.survey.Survey;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TargetNumberCreateServiceRequest {

    private int closedHeadCount;

    private Map<Long, Integer> giveaways;

    private Survey survey;

    @Builder
    private TargetNumberCreateServiceRequest(int closedHeadCount, Map<Long, Integer> giveaways, Survey survey) {
        this.closedHeadCount = closedHeadCount;
        this.giveaways = giveaways;
        this.survey = survey;
    }

    public static TargetNumberCreateServiceRequest create(int closedHeadCount, List<GiveawayAssignServiceRequest> giveawayAssignServiceRequests, Survey survey) {
        return TargetNumberCreateServiceRequest.builder()
                .closedHeadCount(closedHeadCount)
                .giveaways(giveawayAssignServiceRequests.stream()
                        .collect(Collectors.toMap(
                                GiveawayAssignServiceRequest::getId,
                                GiveawayAssignServiceRequest::getCount
                        )))
                .survey(survey)
                .build();
    }
}
