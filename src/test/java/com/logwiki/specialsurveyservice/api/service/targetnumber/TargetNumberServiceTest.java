package com.logwiki.specialsurveyservice.api.service.targetnumber;

import static org.assertj.core.api.Assertions.*;

import com.logwiki.specialsurveyservice.IntegrationTestSupport;
import com.logwiki.specialsurveyservice.api.service.survey.request.GiveawayAssignServiceRequest;
import com.logwiki.specialsurveyservice.api.service.targetnumber.request.TargetNumberCreateServiceRequest;
import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayType;
import com.logwiki.specialsurveyservice.domain.survey.Survey;
import java.util.List;
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
        GiveawayAssignServiceRequest giveawayAssignServiceRequest1 = GiveawayAssignServiceRequest.builder()
                .id(1L)
                .giveawayType(GiveawayType.COFFEE)
                .name("스타벅스 아이스 아메리카노")
                .count(3)
                .build();

        GiveawayAssignServiceRequest giveawayAssignServiceRequest2 = GiveawayAssignServiceRequest.builder()
                .id(2L)
                .giveawayType(GiveawayType.COFFEE)
                .name("컴포즈 아이스 아메리카노")
                .count(5)
                .build();

        List<GiveawayAssignServiceRequest> giveawayAssignServiceRequests = List.of(giveawayAssignServiceRequest1, giveawayAssignServiceRequest2);
        int closedHeadCount = 100;
        Survey survey = Survey.builder()
                .title("설문조사 제목")
                .build();
        TargetNumberCreateServiceRequest targetNumbers = TargetNumberCreateServiceRequest.create(
                closedHeadCount, giveawayAssignServiceRequests, survey);

        assertThat(targetNumbers.getClosedHeadCount()).isEqualTo(closedHeadCount);
        assertThat(targetNumbers.getGiveaways().size()).isEqualTo(2);
        assertThat(targetNumbers.getGiveaways().values().stream()
                .mapToInt(Integer::intValue)
                .sum())
                .isEqualTo(giveawayAssignServiceRequest1.getCount() + giveawayAssignServiceRequest2.getCount());
    }
}