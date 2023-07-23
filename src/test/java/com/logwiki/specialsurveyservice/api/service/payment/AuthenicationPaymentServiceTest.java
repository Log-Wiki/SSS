//package com.logwiki.specialsurveyservice.api.service.payment;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//import com.logwiki.specialsurveyservice.IntegrationTestSupport;
//import com.logwiki.specialsurveyservice.api.service.order.RegistOrderService;
//import com.logwiki.specialsurveyservice.api.service.order.request.OrderCreateServiceRequest;
//import com.logwiki.specialsurveyservice.api.service.order.response.OrderResponse;
//import com.logwiki.specialsurveyservice.api.service.payment.request.PaymentAuthenticationServiceRequest;
//import com.logwiki.specialsurveyservice.api.service.payment.response.PaymentResponse;
//import com.logwiki.specialsurveyservice.domain.orders.Orders;
//import com.logwiki.specialsurveyservice.domain.orders.OrdersRepository;
//import com.siot.IamportRestClient.IamportClient;
//import java.util.Optional;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.transaction.annotation.Transactional;
//
//@Transactional
//class AuthenicationPaymentServiceTest extends IntegrationTestSupport {
//
//  @Autowired
//  private RegistOrderService registOrderService;
//
//  @Autowired
//  private  OrdersRepository ordersRepository;
//
//  @Autowired
//  private AuthenticationPaymentService authenticationPaymentService;
//
//  @DisplayName("클라이언트 결제정보, Iamport 결제정보 , DB 주문정보를 비교한다.")
//  @Test
//  void regist() {
//    PaymentAuthenticationServiceRequest paymentAuthenticationServiceRequest = PaymentAuthenticationServiceRequest
//            .builder()
//            .imp_uid("imp_419227842466")
//            .orderId(35L)
//            .build();
//
//    PaymentResponse paymentResponse = authenticationPaymentService
//            .authentication(paymentAuthenticationServiceRequest,
//            new IamportClient("7343166512186774"
//                    ,"AFgMzlcN1niQMqo4QjGlV7hyBuQOmuKFnKHqtHRzeCme37sbo6b8zKRhfAMtQKzX5BgPSrbE0pfdAEDK"));
//
//    assertThat(paymentResponse.getImp_uid()).isEqualTo("imp_419227842466");
//    assertThat(paymentResponse.getOrderAmount()).isEqualTo()
//  }
//
//}