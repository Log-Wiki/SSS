package com.logwiki.specialsurveyservice.api.service.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.logwiki.specialsurveyservice.IntegrationTestSupport;
import com.logwiki.specialsurveyservice.domain.orders.OrderProductElement;
import com.logwiki.specialsurveyservice.api.controller.payment.request.PaymentAuthenticationRequest;
import com.logwiki.specialsurveyservice.api.service.order.RegistOrderService;
import com.logwiki.specialsurveyservice.api.service.order.request.OrderCreateServiceRequest;
import com.logwiki.specialsurveyservice.api.service.order.response.OrderResponse;
import com.logwiki.specialsurveyservice.api.service.payment.response.PaymentResponse;
import com.logwiki.specialsurveyservice.domain.giveaway.Giveaway;
import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayRepository;
import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayType;
import com.logwiki.specialsurveyservice.domain.payment.IamportApiConstant;
import com.logwiki.specialsurveyservice.exception.BaseException;
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

    private static IamportClient iamportClientApi = new IamportClient(IamportApiConstant.IamportApiKey.getText(),
            IamportApiConstant.IamportApiSecretKey.getText());

    @DisplayName("주문정보 , 수신한 결제정보 , 결제API의 결제정보를 검사하고 처리결과를 반환한다.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    ")
    @Test
    void registOrder() {
        // given
        String userId = "ksr4037@naver.com";
        List<OrderProductElement> giveaways = new ArrayList<>();
        giveaways.add(new OrderProductElement("컴포즈커피",3));
        giveaways.add(new OrderProductElement("BBQ후라이드치킨",2));
        giveawayRepository.save(new Giveaway(GiveawayType.COFFEE,"컴포즈커피",1300));
        giveawayRepository.save(new Giveaway(GiveawayType.CHICKEN,"BBQ후라이드치킨",20000));

        OrderCreateServiceRequest request = OrderCreateServiceRequest.builder().userId(userId).giveaways(giveaways)
                .requestTime(1690416454492L).build();
        OrderResponse saveOrder = registOrderService.createOrder(request);

        // when
        PaymentAuthenticationRequest request1 = new PaymentAuthenticationRequest(userId + "_" + 1690416454492L , "imp_780428188220");
        PaymentResponse paymentResponse = authenticationPaymentService.authenticatePayment(request1.toServiceRequest(),iamportClientApi);


        // then
        assertThat(paymentResponse).isNotNull();
        assertThat(paymentResponse)
                .extracting("imp_uid", "orderId", "orderAmount", "isSucess")
                .contains("imp_780428188220", userId + "_" + 1690416454492L, 43900 , "paid");
    }

    @DisplayName("결제검증을 요청한 주문의 정보가 없는 경우")
    @Test
    void noOrderExist() {
        // given
        String userId = "ksr4037@naver.com";
        List<OrderProductElement> giveaways = new ArrayList<>();
        giveaways.add(new OrderProductElement("컴포즈커피",3));
        giveaways.add(new OrderProductElement("BBQ후라이드치킨",2));
        giveawayRepository.save(new Giveaway(GiveawayType.COFFEE,"컴포즈커피",1300));
        giveawayRepository.save(new Giveaway(GiveawayType.CHICKEN,"BBQ후라이드치킨",20000));

        OrderCreateServiceRequest request = OrderCreateServiceRequest.builder().userId(userId).giveaways(giveaways)
                .requestTime(1690416454492L).build();
        OrderResponse saveOrder = registOrderService.createOrder(request);


        PaymentAuthenticationRequest request1 = new PaymentAuthenticationRequest(userId + "_" + 1690416454493L , "imp_780428188220");

        // then
        assertThatThrownBy(() ->  authenticationPaymentService.authenticatePayment(request1.toServiceRequest(),iamportClientApi))
                .isInstanceOf(BaseException.class)
                .hasMessage("주문 정보가 없는 결제인증 요청입니다.");

    }

    @DisplayName("클라이언트의 결제 정보와 IAMPORT의 결제 정보가 다른 경우")
    @Test
    void notVaildInfo() {
        // given
        String userId = "ksr4037@naver.com";
        List<OrderProductElement> giveaways = new ArrayList<>();
        giveaways.add(new OrderProductElement("컴포즈커피",3));
        giveaways.add(new OrderProductElement("BBQ후라이드치킨",2));
        giveawayRepository.save(new Giveaway(GiveawayType.COFFEE,"컴포즈커피",1200));
        giveawayRepository.save(new Giveaway(GiveawayType.CHICKEN,"BBQ후라이드치킨",20000));

        OrderCreateServiceRequest request = OrderCreateServiceRequest.builder().userId(userId).giveaways(giveaways)
                .requestTime(1690416454492L).build();
        OrderResponse saveOrder = registOrderService.createOrder(request);


        PaymentAuthenticationRequest request1 = new PaymentAuthenticationRequest(userId + "_" + 1690416454492L , "imp_780428188220");

        // then
        assertThatThrownBy(() ->  authenticationPaymentService.authenticatePayment(request1.toServiceRequest(),iamportClientApi))
                .isInstanceOf(BaseException.class)
                .hasMessage("주문 금액과 결제금액이 다릅니다.");

    }

    
}