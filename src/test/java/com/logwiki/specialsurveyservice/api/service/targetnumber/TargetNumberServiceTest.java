package com.logwiki.specialsurveyservice.api.service.targetnumber;

import static org.assertj.core.api.Assertions.*;

import com.logwiki.specialsurveyservice.IntegrationTestSupport;
import com.logwiki.specialsurveyservice.api.controller.giveaway.request.GiveawayRequest;
import com.logwiki.specialsurveyservice.api.service.giveaway.GiveawayService;
import com.logwiki.specialsurveyservice.api.service.giveaway.response.GiveawayResponse;
import com.logwiki.specialsurveyservice.api.service.survey.request.GiveawayAssignServiceRequest;
import com.logwiki.specialsurveyservice.api.service.targetnumber.request.TargetNumberCreateServiceRequest;
import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayType;
import com.logwiki.specialsurveyservice.domain.survey.Survey;
import com.logwiki.specialsurveyservice.domain.targetnumber.TargetNumber;
import com.logwiki.specialsurveyservice.exception.BaseException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class TargetNumberServiceTest extends IntegrationTestSupport {

    @Autowired
    TargetNumberService targetNumberService;
    @Autowired
    GiveawayService giveawayService;

    @DisplayName("당첨 상품과 마감인원수를 통해 당첨 번호를 추출한다.")
    @Test
    void createTargetNumbers() {
        // given
        GiveawayType giveawayType1 = GiveawayType.COFFEE;
        String name1 = "스타벅스 아이스 아메리카노";
        int price1 = 4500;
        GiveawayRequest request1 = createGiveawayRequest(giveawayType1, name1, price1);
        GiveawayResponse saveGiveaway1 = giveawayService.createGiveaway(request1);

        GiveawayType giveawayType2 = GiveawayType.CHICKEN;
        String name2 = "BHC 뿌링클";
        int price2 = 20_000;
        GiveawayRequest request2 = createGiveawayRequest(giveawayType2, name2, price2);
        GiveawayResponse saveGiveaway2 = giveawayService.createGiveaway(request2);

        int giveawayCount1 = 3;
        int giveawayCount2 = 5;
        GiveawayAssignServiceRequest giveawayAssignServiceRequest1 = GiveawayAssignServiceRequest.builder()
                .id(saveGiveaway1.getId())
                .count(giveawayCount1)
                .build();

        GiveawayAssignServiceRequest giveawayAssignServiceRequest2 = GiveawayAssignServiceRequest.builder()
                .id(saveGiveaway2.getId())
                .count(giveawayCount2)
                .build();

        List<GiveawayAssignServiceRequest> giveawayAssignServiceRequests = List.of(
                giveawayAssignServiceRequest1, giveawayAssignServiceRequest2);
        int closedHeadCount = 100;
        String surveyName = "설문조사 제목";
        Survey survey = Survey.builder()
                .title("설문조사 제목")
                .build();
        TargetNumberCreateServiceRequest request = TargetNumberCreateServiceRequest.create(
                closedHeadCount, giveawayAssignServiceRequests, survey);

        // when
        List<TargetNumber> targetNumbers = targetNumberService.createTargetNumbers(request);

        // then
        assertThat(request.getGiveaways().values().stream()
                .mapToInt(Integer::intValue)
                .sum()).isEqualTo(targetNumbers.size());
        assertThat(targetNumbers.stream()
                .filter(targetNumber -> targetNumber.getSurvey().getTitle().equals(surveyName))
                .count()).isEqualTo(giveawayCount1 + giveawayCount2);
        assertThat(targetNumbers.stream()
                .filter(targetNumber -> targetNumber.getNumber() <= closedHeadCount)
                .count())
                .isEqualTo(giveawayCount1 + giveawayCount2);
    }

    @DisplayName("'마감 인원 수'와 '당첨상품ID - 당첨상품개수'를 이용하여 '당첨 번호 - 당첨 상품 ID'를 얻는다.")
    @Test
    void getTargetNumberAndGiveawayId() {
        // given
        int closedHeadCount = 100;
        Map<Long, Integer> giveawayIdAndCount = new HashMap<>();
        Long giveawayId1 = 1L;
        Long giveawayId2 = 2L;
        int giveawayCount1 = 3;
        int giveawayCount2 = 5;
        giveawayIdAndCount.put(giveawayId1, giveawayCount1);
        giveawayIdAndCount.put(giveawayId2, giveawayCount2);

        // when
        Map<Integer, Long> targetNumberAndGiveawayId = targetNumberService.getTargetNumberAndGiveawayId(
                closedHeadCount, giveawayIdAndCount);

        // then
        assertThat((long) targetNumberAndGiveawayId.keySet().size())
                .isEqualTo(giveawayCount1 + giveawayCount2);
        assertThat(targetNumberAndGiveawayId.keySet().stream()
                .allMatch(targetNumber -> targetNumber <= closedHeadCount))
                .isTrue();
        assertThat(targetNumberAndGiveawayId.values().stream()
                .allMatch(giveawayId -> (giveawayId.equals(giveawayId1)) | (giveawayId.equals(
                        giveawayId2))))
                .isTrue();
    }

    @DisplayName("'당첨 상품 번호 - 당첨 상품 id'와 설문 조사를 이용하여 당첨 상품 목록을 만든다.")
    @Test
    void getTargetNumbers() {
        // given
        GiveawayType giveawayType1 = GiveawayType.COFFEE;
        String name1 = "스타벅스 아이스 아메리카노";
        int price1 = 4500;
        GiveawayRequest request1 = createGiveawayRequest(giveawayType1, name1, price1);
        GiveawayResponse giveaway1 = giveawayService.createGiveaway(request1);

        GiveawayType giveawayType2 = GiveawayType.CHICKEN;
        String name2 = "BHC 뿌링클";
        int price2 = 20_000;
        GiveawayRequest request2 = createGiveawayRequest(giveawayType2, name2, price2);
        GiveawayResponse giveaway2 = giveawayService.createGiveaway(request2);

        int closedHeadCount = 100;
        Map<Long, Integer> giveawayIdAndCount = new HashMap<>();
        Long giveawayId1 = giveaway1.getId();
        Long giveawayId2 = giveaway2.getId();
        int giveawayCount1 = 3;
        int giveawayCount2 = 5;
        giveawayIdAndCount.put(giveawayId1, giveawayCount1);
        giveawayIdAndCount.put(giveawayId2, giveawayCount2);

        Map<Integer, Long> targetNumberAndGiveawayId = targetNumberService.getTargetNumberAndGiveawayId(
                closedHeadCount, giveawayIdAndCount);

        Survey survey = Survey.builder()
                .title("당신은 어떤 과일을 좋아하십니까?")
                .startTime(LocalDateTime.now().minusMinutes(5))
                .endTime(LocalDateTime.now())
                .headCount(50)
                .closedHeadCount(100)
                .writer(1L)
                .build();

        // when
        List<TargetNumber> targetNumbers = targetNumberService.getTargetNumbers(
                targetNumberAndGiveawayId, survey);

        // then
        List<Integer> numbersInTargetNumbers = targetNumbers.stream()
                .map(TargetNumber::getNumber)
                .toList();

        assertThat(targetNumbers.size()).isEqualTo(giveawayCount1 + giveawayCount2);
        assertThat(numbersInTargetNumbers.stream()
                .allMatch(targetNumberAndGiveawayId::containsKey))
                .isTrue();
        assertThat(targetNumbers.stream()
                .map(TargetNumber::getSurvey)
                .allMatch(surveyInTargetNumber -> surveyInTargetNumber.equals(survey)))
                .isTrue();
        assertThat(targetNumbers.stream()
                .map(TargetNumber::getGiveaway)
                .allMatch(giveaway -> giveaway.getId().equals(giveawayId1) || giveaway.getId().equals(giveawayId2)))
                .isTrue();
    }

    @DisplayName("당첨 상품 목록을 만들 때 조회된 당첨 상품이 없는 경우 예외를 발생시킨다.")
    @Test
    void getTargetNumbersWithInvalidGiveawayId() {
        // given
        int closedHeadCount = 100;
        Map<Long, Integer> giveawayIdAndCount = new HashMap<>();
        Long giveawayId = 1L;
        int giveawayCount = 3;
        giveawayIdAndCount.put(giveawayId, giveawayCount);

        Map<Integer, Long> targetNumberAndGiveawayId = targetNumberService.getTargetNumberAndGiveawayId(
                closedHeadCount, giveawayIdAndCount);

        Survey survey = Survey.builder()
                .title("당신은 어떤 과일을 좋아하십니까?")
                .startTime(LocalDateTime.now().minusMinutes(5))
                .endTime(LocalDateTime.now())
                .headCount(50)
                .closedHeadCount(100)
                .writer(1L)
                .build();

        // when // then
        assertThatThrownBy(() -> targetNumberService.getTargetNumbers(
                targetNumberAndGiveawayId, survey))
                .isInstanceOf(BaseException.class)
                .hasMessage("설문에 등록할 당첨 상품의 값이 올바르지 않습니다.");
    }

    private static GiveawayRequest createGiveawayRequest(GiveawayType giveawayType, String name,
            int price) {
        return GiveawayRequest.builder()
                .giveawayType(giveawayType)
                .name(name)
                .price(price)
                .build();
    }
}