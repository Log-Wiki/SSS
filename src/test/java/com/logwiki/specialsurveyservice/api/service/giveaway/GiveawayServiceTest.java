package com.logwiki.specialsurveyservice.api.service.giveaway;

import static org.assertj.core.api.Assertions.*;

import com.logwiki.specialsurveyservice.IntegrationTestSupport;
import com.logwiki.specialsurveyservice.api.controller.giveaway.request.GiveawayRequest;
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

        GiveawayRequest request = createGiveawayRequest(giveawayType, name, price);

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
        // given
        GiveawayType giveawayType = GiveawayType.COFFEE;
        String name = "스타벅스 아메리카노";
        int price = 4500;

        GiveawayRequest request = createGiveawayRequest(giveawayType, name, price);

        return List.of(
                DynamicTest.dynamicTest("중복된 상품 이름이 없는 경우 상품을 등록할 수 있다.", () -> {
                    // when
                    GiveawayResponse saveGiveaway = giveawayService.createGiveaway(request);

                    // then
                    assertThat(saveGiveaway).isNotNull();
                    assertThat(saveGiveaway)
                            .extracting("giveawayType", "name", "price")
                            .contains(giveawayType, name, price);
                }),
                DynamicTest.dynamicTest("중복된 상품 이름이 있는 경우 상품을 등록할 수 없다.", () -> {
                    // when // then
                    assertThatThrownBy(() -> giveawayService.createGiveaway(request))
                            .isInstanceOf(BaseException.class)
                            .hasMessage("이미 등록되어 있는 상품이 있습니다.");
                })
        );
    }

    @DisplayName("등록된 경품 목록을 조회한다.")
    @Test
    void getGiveaways() {
        // given
        GiveawayRequest giveaway1 = createGiveawayRequest(GiveawayType.COFFEE, "스타벅스 아메리카노", 4500);
        GiveawayRequest giveaway2 = createGiveawayRequest(GiveawayType.COFFEE, "컴포즈 아메리카노", 4500);
        GiveawayRequest giveaway3 = createGiveawayRequest(GiveawayType.CHICKEN, "BHC 뿌링클", 20_000);
        giveawayService.createGiveaway(giveaway1);
        giveawayService.createGiveaway(giveaway2);
        giveawayService.createGiveaway(giveaway3);

        // when
        List<GiveawayResponse> giveaways = giveawayService.getGiveaways();

        // then
        assertThat(giveaways).hasSize(3)
                .extracting("giveawayType", "name", "price")
                .contains(
                        tuple(giveaway1.getGiveawayType(), giveaway1.getName(), giveaway1.getPrice()),
                        tuple(giveaway2.getGiveawayType(), giveaway2.getName(), giveaway2.getPrice()),
                        tuple(giveaway3.getGiveawayType(), giveaway3.getName(), giveaway3.getPrice())
                );
    }

    @DisplayName("등록된 상품을 삭제합니다.")
    @Test
    void deleteGiveaway() {
        // given
        GiveawayType giveawayType = GiveawayType.COFFEE;
        String name = "스타벅스 아메리카노";
        int price = 4500;

        GiveawayRequest request = createGiveawayRequest(giveawayType, name, price);

        GiveawayResponse saveGiveaway = giveawayService.createGiveaway(request);

        // when
        GiveawayResponse giveawayResponse = giveawayService.deleteGiveaway(saveGiveaway.getId());

        // then
        assertThat(giveawayResponse).isNotNull();
        assertThat(giveawayResponse)
                .extracting("giveawayType", "name", "price")
                .contains(giveawayType, name, price);
    }

    @DisplayName("삭제할 상품의 PK를 올바르게 입력하지 않은 경우 예외를 발생한다.")
    @Test
    void deleteGiveawayWithWrongName() {
        // given
        GiveawayType giveawayType = GiveawayType.COFFEE;
        String name = "스타벅스 아메리카노";
        int price = 4500;

        GiveawayRequest request = createGiveawayRequest(giveawayType, name, price);

        GiveawayResponse saveGiveaway = giveawayService.createGiveaway(request);

        // when // then
        assertThatThrownBy(() -> giveawayService.deleteGiveaway(saveGiveaway.getId() + 1L))
                .isInstanceOf(BaseException.class)
                .hasMessage("삭제할 상품의 PK가 올바르지 않습니다.");
    }

    @DisplayName("요청으로 들어온 정보를 이용하여 상품의 정보를 수정한다.")
    @Test
    void updateGiveaway() {
        // given
        GiveawayRequest originGiveaway = createGiveawayRequest(GiveawayType.COFFEE, "스타벅스 아메리카노", 4500);
        GiveawayResponse saveGiveaway = giveawayService.createGiveaway(originGiveaway);

        GiveawayType updateGiveawayType = GiveawayType.COFFEE;
        String updateName = "스타벅스 카페라떼";
        int updatePrice = 5000;

        GiveawayRequest giveawayUpdateRequest = createGiveawayRequest(updateGiveawayType, updateName, updatePrice);

        // when
        GiveawayResponse updateGiveaway = giveawayService.updateGiveaway(saveGiveaway.getId(), giveawayUpdateRequest);

        // then
        assertThat(updateGiveaway)
                .extracting("giveawayType", "name", "price")
                .contains(updateGiveawayType, updateName, updatePrice);
    }

    @DisplayName("수정할 상품의 PK 값이 올바르지 않으면 예외를 반환한다.")
    @Test
    void updateGiveawayWithInvalidPK() {
        // given
        GiveawayRequest giveawayUpdateRequest = createGiveawayRequest(GiveawayType.COFFEE, "스타벅스 카페라떼", 5000);

        Long wrongPK = 0L;

        // when // then
        assertThatThrownBy(() -> giveawayService.updateGiveaway(wrongPK, giveawayUpdateRequest))
                .isInstanceOf(BaseException.class)
                .hasMessage("수정할 상품의 PK가 올바르지 않습니다.");
    }

    private static GiveawayRequest createGiveawayRequest(GiveawayType giveawayType, String name, int price) {
        return GiveawayRequest.builder()
                .giveawayType(giveawayType)
                .name(name)
                .price(price)
                .build();
    }
}