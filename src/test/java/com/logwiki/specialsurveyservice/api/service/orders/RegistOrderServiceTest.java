package com.logwiki.specialsurveyservice.api.service.orders;

import static org.assertj.core.api.Assertions.assertThat;

import com.logwiki.specialsurveyservice.IntegrationTestSupport;
import com.logwiki.specialsurveyservice.api.controller.orders.request.OrderCreateRequest;
import com.logwiki.specialsurveyservice.api.service.giveaway.GiveawayService;
import com.logwiki.specialsurveyservice.api.service.order.RegistOrderService;
import com.logwiki.specialsurveyservice.api.service.order.request.OrderCreateServiceRequest;
import com.logwiki.specialsurveyservice.api.service.order.response.OrderResponse;
import com.logwiki.specialsurveyservice.domain.giveaway.Giveaway;
import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayRepository;
import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayType;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class RegistOrderServiceTest extends IntegrationTestSupport {

    @Autowired
    RegistOrderService registOrderService;

    @Autowired
    GiveawayRepository giveawayRepository;
    @Autowired
    GiveawayService giveawayService;
    @DisplayName("사용자ID , {상품명 , 상품금액}리스트로 주문을 등록한다.")
    @Test
    void registOrder() {
        // given

        String userId = "ksr4037@naver.com";
        List<OrderCreateRequest> giveaways = new ArrayList<>();
        giveaways.add(new OrderCreateRequest("컴포즈커피",3));
        giveaways.add(new OrderCreateRequest("BBQ후라이드치킨",2));
        giveawayRepository.save(new Giveaway(GiveawayType.COFFEE,"컴포즈커피",1300));
        giveawayRepository.save(new Giveaway(GiveawayType.CHICKEN,"BBQ후라이드치킨",20000));
        OrderCreateServiceRequest request = OrderCreateServiceRequest.builder().userId(userId).giveaways(giveaways).build();
        // when
        OrderResponse saveOrder = registOrderService.regist(request);

        // then
        assertThat(saveOrder).isNotNull();
        assertThat(saveOrder)
                .extracting( "orderAmount", "isSuccess")
                .contains( 43900 , false);

    }


}