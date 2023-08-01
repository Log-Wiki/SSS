package com.logwiki.specialsurveyservice.api.service.orders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.logwiki.specialsurveyservice.IntegrationTestSupport;
import com.logwiki.specialsurveyservice.domain.orders.OrderProductElement;
import com.logwiki.specialsurveyservice.api.service.giveaway.GiveawayService;
import com.logwiki.specialsurveyservice.api.service.order.RegistOrderService;
import com.logwiki.specialsurveyservice.api.service.order.request.OrderCreateServiceRequest;
import com.logwiki.specialsurveyservice.api.service.order.response.OrderResponse;
import com.logwiki.specialsurveyservice.domain.giveaway.Giveaway;
import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayRepository;
import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayType;
import com.logwiki.specialsurveyservice.exception.BaseException;
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
        List<OrderProductElement> giveaways = new ArrayList<>();
        giveaways.add(new OrderProductElement("컴포즈커피",3));
        giveaways.add(new OrderProductElement("BBQ후라이드치킨",2));
        giveawayRepository.save(new Giveaway(GiveawayType.COFFEE,"컴포즈커피",1300));
        giveawayRepository.save(new Giveaway(GiveawayType.CHICKEN,"BBQ후라이드치킨",20000));
        
        OrderCreateServiceRequest request = OrderCreateServiceRequest.builder().userId(userId).giveaways(giveaways).build();
        // when
        OrderResponse saveOrder = registOrderService.createOrder(request);

        // then
        assertThat(saveOrder).isNotNull();
        assertThat(saveOrder)
                .extracting( "orderAmount", "isVerificated")
                .contains( 43900 , false);

    }

    @DisplayName("주문 금액이 0원이어서는 안된다.")
    @Test
    void registOrderWithNoMoney() {
        // given

        String userId = "ksr4037@naver.com";
        List<OrderProductElement> giveaways = new ArrayList<>();
        giveaways.add(new OrderProductElement("컴포즈커피",0));
        giveaways.add(new OrderProductElement("BBQ후라이드치킨",0));
        giveawayRepository.save(new Giveaway(GiveawayType.COFFEE,"컴포즈커피",1300));
        giveawayRepository.save(new Giveaway(GiveawayType.CHICKEN,"BBQ후라이드치킨",20000));

        OrderCreateServiceRequest request = OrderCreateServiceRequest.builder().userId(userId).giveaways(giveaways).build();
        // when // then
        assertThatThrownBy(() ->registOrderService.createOrder(request))
                .isInstanceOf(BaseException.class)
                .hasMessage("주문 금액이 0원입니다.");

    }

    @DisplayName("입력받은 주문상품이 존재하지 않습니다.")
    @Test
    void registOrderWithNoProduct() {
        // given

        String userId = "ksr4037@naver.com";
        List<OrderProductElement> giveaways = new ArrayList<>();
        giveaways.add(new OrderProductElement("컴포즈커피",3));
        giveaways.add(new OrderProductElement("BBQ후라이드치킨",2));
        giveawayRepository.save(new Giveaway(GiveawayType.CHICKEN,"BBQ후라이드치킨",20000));

        OrderCreateServiceRequest request = OrderCreateServiceRequest.builder().userId(userId).giveaways(giveaways).build();
        // when // then
        assertThatThrownBy(() ->registOrderService.createOrder(request))
                .isInstanceOf(BaseException.class)
                .hasMessage("주문 상품이 존재하지 않습니다.");

    }


}