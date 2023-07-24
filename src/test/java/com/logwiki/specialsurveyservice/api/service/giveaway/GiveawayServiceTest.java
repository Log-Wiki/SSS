package com.logwiki.specialsurveyservice.api.service.giveaway;

import static org.assertj.core.api.Assertions.*;

import com.logwiki.specialsurveyservice.IntegrationTestSupport;
import com.logwiki.specialsurveyservice.api.controller.giveaway.request.GiveawayDto;
import com.logwiki.specialsurveyservice.api.service.giveaway.response.GiveawayResponse;
import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayRepository;
import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayType;
import com.logwiki.specialsurveyservice.exception.BaseException;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class GiveawayServiceTest extends IntegrationTestSupport {

    @Autowired
    GiveawayService giveawayService;
    @Autowired
    GiveawayRepository giveawayRepository;

    @DisplayName("상품 타입, 상품 이름, 상품 가격으로 상품을 등록한다.")
    @Test
    void createGiveaway() {
        // given
        GiveawayType giveawayType = GiveawayType.COFFEE;
        String name = "스타벅스 아메리카노";
        int price = 4500;

        GiveawayDto request = GiveawayDto.builder()
                .giveawayType(giveawayType)
                .name(name)
                .price(price)
                .build();

        // when
        GiveawayResponse saveGiveaway = giveawayService.createGiveaway(request);

        // then
        assertThat(saveGiveaway).isNotNull();
        assertThat(saveGiveaway)
                .extracting("giveawayType", "name", "price")
                .contains(giveawayType, name, price);
    }

    @DisplayName("중복 상품 등록 시나리오")
    @TestFactory
    Collection<DynamicTest> createDuplicateGiveaway() {

        GiveawayType giveawayType = GiveawayType.COFFEE;
        String name = "스타벅스 아메리카노";
        int price = 4500;

        GiveawayDto request = GiveawayDto.builder()
                .giveawayType(giveawayType)
                .name(name)
                .price(price)
                .build();

        return List.of(
                DynamicTest.dynamicTest("중복된 상품 이름이 없는 경우 상품을 등록할 수 있다.", () -> {
                    GiveawayResponse saveGiveaway = giveawayService.createGiveaway(request);

                    // then
                    assertThat(saveGiveaway).isNotNull();
                    assertThat(saveGiveaway)
                            .extracting("giveawayType", "name", "price")
                            .contains(giveawayType, name, price);
                }),
                DynamicTest.dynamicTest("중복된 상품 이름이 있는 경우 상품을 등록할 수 없다.", () -> {
                    assertThatThrownBy(() -> giveawayService.createGiveaway(request))
                            .isInstanceOf(BaseException.class)
                            .hasMessage("이미 등록되어 있는 상품이 있습니다");
                })
        );
    }
}