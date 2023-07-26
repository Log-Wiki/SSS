package com.logwiki.specialsurveyservice.api.service.payment;

import static org.assertj.core.api.Assertions.assertThat;

import com.logwiki.specialsurveyservice.IntegrationTestSupport;
import com.logwiki.specialsurveyservice.api.controller.orders.request.OrderCreateRequest;
import com.logwiki.specialsurveyservice.api.service.order.RegistOrderService;
import com.logwiki.specialsurveyservice.api.service.order.request.OrderCreateServiceRequest;
import com.logwiki.specialsurveyservice.api.service.order.response.OrderResponse;
import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayType;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class AuthenticationPaymentServiceTest extends IntegrationTestSupport {

    @Autowired
    RegistOrderService registOrderService;

    @Autowired
    AuthenticationPaymentService authenticationPaymentService;

    @DisplayName("주문정보 , 수신한 결제정보 , 결제API의 결제정보를 검사하고 처리결과를 반환한다.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    ")
    @Test
    void registOrder() {
        // given
        GiveawayType giveawayType = GiveawayType.COFFEE;
        String userId = "ksr4037@naver.com";
        List<OrderCreateRequest> giveaways = new ArrayList<>();
        giveaways.add(new OrderCreateRequest("CHICKEN",1));
        giveaways.add(new OrderCreateRequest("COFFEE",2));

        OrderCreateServiceRequest request = OrderCreateServiceRequest.builder().userId(userId).giveaways(giveaways).build();
        // when
        OrderResponse saveOrder = registOrderService.regist(request);

        // then
        assertThat(saveOrder).isNotNull();
        assertThat(saveOrder)
                .extracting("orderId", "orderAmount", "isSuccess")
                .contains("ksr4037@gmail.com", 26000 , true);
    }

    
}