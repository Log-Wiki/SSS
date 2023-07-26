package com.logwiki.specialsurveyservice.api.service.targetnumber;

import com.logwiki.specialsurveyservice.IntegrationTestSupport;
import com.logwiki.specialsurveyservice.api.service.targetnumber.request.TargetNumberCreateServiceRequest;
import java.util.HashMap;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class TargetNumberServiceTest extends IntegrationTestSupport {

    @Autowired
    TargetNumberService targetNumberService;

    @DisplayName("")
    @Test
    void createTargetNumbers() {
        Map<Long, Integer> giveaways = new HashMap<>();
        Long giveawayId1 = 1L;
        int giveawayCount1 = 3;
        Long giveawayId2 = 2L;
        int giveawayCount2 = 5;
        giveaways.put(giveawayId1, giveawayCount1);
        giveaways.put(giveawayId2, giveawayCount2);

        TargetNumberCreateServiceRequest targetNumberCreateServiceRequest = TargetNumberCreateServiceRequest.create(100,
                giveaways);

        Map<Integer, Long> targetNumbers = targetNumberService.createTargetNumbers(targetNumberCreateServiceRequest);

        Assertions.assertThat(targetNumbers.size()).isEqualTo(giveawayCount1 + giveawayCount2);

        Assertions.assertThat(targetNumbers.values().stream()
                        .filter(value -> value.equals(giveawayId1))
                        .count())
                .isEqualTo(giveawayCount1);
        Assertions.assertThat(targetNumbers.values().stream()
                        .filter(value -> value.equals(giveawayId2))
                        .count())
                .isEqualTo(giveawayCount2);
    }
}