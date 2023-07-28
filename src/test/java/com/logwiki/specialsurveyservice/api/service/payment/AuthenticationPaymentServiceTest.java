package com.logwiki.specialsurveyservice.api.service.payment;

import static org.assertj.core.api.Assertions.assertThat;

import com.logwiki.specialsurveyservice.IntegrationTestSupport;
import com.logwiki.specialsurveyservice.api.controller.orders.request.OrderCreateRequest;
import com.logwiki.specialsurveyservice.api.controller.payment.request.PaymentAuthenticationRequest;
import com.logwiki.specialsurveyservice.api.service.order.RegistOrderService;
import com.logwiki.specialsurveyservice.api.service.order.request.OrderCreateServiceRequest;
import com.logwiki.specialsurveyservice.api.service.order.response.OrderResponse;
import com.logwiki.specialsurveyservice.api.service.payment.response.PaymentResponse;
import com.logwiki.specialsurveyservice.domain.giveaway.Giveaway;
import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayRepository;
import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayType;
import com.siot.IamportRestClient.IamportClient;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AuthenticationPaymentServiceTest extends IntegrationTestSupport {

    @Autowired
    RegistOrderService registOrderService;

    @Autowired
    AuthenticationPaymentService authenticationPaymentService;

    @Autowired
    GiveawayRepository giveawayRepository;

    private static IamportClient iamportClientApi = new IamportClient("7343166512186774"
            ,"AFgMzlcN1niQMqo4QjGlV7hyBuQOmuKFnKHqtHRzeCme37sbo6b8zKRhfAMtQKzX5BgPSrbE0pfdAEDK");

    @DisplayName("주문정보 , 수신한 결제정보 , 결제API의 결제정보를 검사하고 처리결과를 반환한다.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    ")
    @Test
    void registOrder() {
        // given
        String userId = "ksr4037@naver.com";
        List<OrderCreateRequest> giveaways = new ArrayList<>();
        giveaways.add(new OrderCreateRequest("컴포즈커피",3));
        giveaways.add(new OrderCreateRequest("BBQ후라이드치킨",2));
        giveawayRepository.save(new Giveaway(GiveawayType.COFFEE,"컴포즈커피",1300));
        giveawayRepository.save(new Giveaway(GiveawayType.CHICKEN,"BBQ후라이드치킨",20000));

        OrderCreateServiceRequest request = OrderCreateServiceRequest.builder().userId(userId).giveaways(giveaways)
                .requestTime(1690416454492L).build();
        OrderResponse saveOrder = registOrderService.regist(request);

        // when
        PaymentAuthenticationRequest request1 = new PaymentAuthenticationRequest(userId + "_" + 1690416454492L , "imp_780428188220");
        PaymentResponse paymentResponse = authenticationPaymentService.authentication(request1.toServiceRequest(),iamportClientApi);


        // then
        assertThat(paymentResponse).isNotNull();
        assertThat(paymentResponse)
                .extracting("imp_uid", "orderId", "orderAmount", "isSucess")
                .contains("imp_780428188220", userId + "_" + 1690416454492L, 43900 , "paid");
    }

    
}