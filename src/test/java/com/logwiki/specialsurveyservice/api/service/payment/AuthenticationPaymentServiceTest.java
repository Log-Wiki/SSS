package com.logwiki.specialsurveyservice.api.service.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.logwiki.specialsurveyservice.IntegrationTestSupport;
import com.logwiki.specialsurveyservice.api.service.account.AccountService;
import com.logwiki.specialsurveyservice.api.service.account.request.AccountCreateServiceRequest;
import com.logwiki.specialsurveyservice.domain.accountcode.AccountCode;
import com.logwiki.specialsurveyservice.domain.accountcode.AccountCodeRepository;
import com.logwiki.specialsurveyservice.domain.accountcode.AccountCodeType;
import com.logwiki.specialsurveyservice.domain.authority.Authority;
import com.logwiki.specialsurveyservice.domain.authority.AuthorityRepository;
import com.logwiki.specialsurveyservice.domain.authority.AuthorityType;
import com.logwiki.specialsurveyservice.domain.orders.OrderProductElement;
import com.logwiki.specialsurveyservice.api.controller.payment.request.PaymentAuthenticationRequest;
import com.logwiki.specialsurveyservice.api.service.order.RegistOrderService;
import com.logwiki.specialsurveyservice.api.service.order.request.OrderCreateServiceRequest;
import com.logwiki.specialsurveyservice.api.service.order.response.OrderResponse;
import com.logwiki.specialsurveyservice.api.service.payment.response.PaymentResponse;
import com.logwiki.specialsurveyservice.domain.giveaway.Giveaway;
import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayRepository;
import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayType;
import com.logwiki.specialsurveyservice.exception.BaseException;
import com.siot.IamportRestClient.IamportClient;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AuthenticationPaymentServiceTest extends IntegrationTestSupport {

    @Autowired
    RegistOrderService registOrderService;
    @Autowired
    AuthenticationPaymentService authenticationPaymentService;
    @Autowired
    GiveawayRepository giveawayRepository;
    @Autowired
    AccountService accountService;
    @Autowired
    AuthorityRepository authorityRepository;
    @Autowired
    AccountCodeRepository accountCodeRepository;

    private final static String IMPUID = "imp_780428188220";
    private final static int CORRECTPRICE = 43900;
    private final static Long CORRECTTIME = 1690416454492L;

    @BeforeEach
    void setUp() {
        setAuthority();
        setAccountCode();
    }

    @DisplayName("주문정보 , 수신한 결제정보 , 결제API의 결제정보를 검사하고 처리결과를 반환한다.")
    @WithMockUser(username = "ksr4037@naver.com")
    @Test
    void registOrder() {
        // given
        String email = "ksr4037@naver.com";
        AccountCreateServiceRequest accountCreateServiceRequest = AccountCreateServiceRequest.builder()
                .email(email)
                .password("1234")
                .gender(AccountCodeType.MAN)
                .age(AccountCodeType.TWENTIES)
                .name("최연재")
                .phoneNumber("010-1234-5678")
                .birthday(LocalDate.of(1997, 6, 24))
                .build();
        accountService.signup(accountCreateServiceRequest);
        String userId = email;

        List<OrderProductElement> giveaways = new ArrayList<>();
        giveaways.add(new OrderProductElement("컴포즈커피",3));
        giveaways.add(new OrderProductElement("BBQ후라이드치킨",2));
        giveawayRepository.save(new Giveaway(GiveawayType.COFFEE,"컴포즈커피",1300));
        giveawayRepository.save(new Giveaway(GiveawayType.CHICKEN,"BBQ후라이드치킨",20000));

        OrderCreateServiceRequest request = OrderCreateServiceRequest.builder().giveaways(giveaways)
                .requestTime(CORRECTTIME).build();
        OrderResponse saveOrder = registOrderService.createOrder(request);

        // when
        PaymentAuthenticationRequest request1 = new PaymentAuthenticationRequest(userId + "_" + CORRECTTIME , IMPUID);
        PaymentResponse paymentResponse = authenticationPaymentService.authenticatePayment(request1.toServiceRequest());


        // then
        assertThat(paymentResponse).isNotNull();
        assertThat(paymentResponse)
                .extracting("imp_uid", "orderId", "orderAmount", "isSucess")
                .contains(IMPUID, userId + "_" + CORRECTTIME, CORRECTPRICE , "paid");
    }

    @DisplayName("결제검증을 요청한 주문의 정보가 없는 경우")
    @WithMockUser(username = "ksr4037@naver.com")
    @Test
    void noOrderExist() {
        String email = "ksr4037@naver.com";
        AccountCreateServiceRequest accountCreateServiceRequest = AccountCreateServiceRequest.builder()
                .email(email)
                .password("1234")
                .gender(AccountCodeType.MAN)
                .age(AccountCodeType.TWENTIES)
                .name("최연재")
                .phoneNumber("010-1234-5678")
                .birthday(LocalDate.of(1997, 6, 24))
                .build();
        accountService.signup(accountCreateServiceRequest);
        String userId = email;
        List<OrderProductElement> giveaways = new ArrayList<>();
        giveaways.add(new OrderProductElement("컴포즈커피",3));
        giveaways.add(new OrderProductElement("BBQ후라이드치킨",2));
        giveawayRepository.save(new Giveaway(GiveawayType.COFFEE,"컴포즈커피",1300));
        giveawayRepository.save(new Giveaway(GiveawayType.CHICKEN,"BBQ후라이드치킨",20000));

        OrderCreateServiceRequest request = OrderCreateServiceRequest.builder().giveaways(giveaways)
                .requestTime(CORRECTTIME).build();
        OrderResponse saveOrder = registOrderService.createOrder(request);


        PaymentAuthenticationRequest request1 = new PaymentAuthenticationRequest(userId + "_" + CORRECTTIME+1 , IMPUID);

        // then
        assertThatThrownBy(() ->  authenticationPaymentService.authenticatePayment(request1.toServiceRequest()))
                .isInstanceOf(BaseException.class)
                .hasMessage("주문 정보가 없는 결제인증 요청입니다.");

    }

    @DisplayName("클라이언트의 결제 정보와 IAMPORT의 결제 정보가 다른 경우")
    @WithMockUser(username = "ksr4037@naver.com")
    @Test
    void notVaildInfo() {
        String email = "ksr4037@naver.com";
        AccountCreateServiceRequest accountCreateServiceRequest = AccountCreateServiceRequest.builder()
                .email(email)
                .password("1234")
                .gender(AccountCodeType.MAN)
                .age(AccountCodeType.TWENTIES)
                .name("최연재")
                .phoneNumber("010-1234-5678")
                .birthday(LocalDate.of(1997, 6, 24))
                .build();
        accountService.signup(accountCreateServiceRequest);
        String userId = email;
        List<OrderProductElement> giveaways = new ArrayList<>();
        giveaways.add(new OrderProductElement("컴포즈커피",3));
        giveaways.add(new OrderProductElement("BBQ후라이드치킨",2));
        giveawayRepository.save(new Giveaway(GiveawayType.COFFEE,"컴포즈커피",1200));
        giveawayRepository.save(new Giveaway(GiveawayType.CHICKEN,"BBQ후라이드치킨",20000));

        OrderCreateServiceRequest request = OrderCreateServiceRequest.builder().giveaways(giveaways)
                .requestTime(CORRECTTIME).build();
        OrderResponse saveOrder = registOrderService.createOrder(request);


        PaymentAuthenticationRequest request1 = new PaymentAuthenticationRequest(userId + "_" + CORRECTTIME , IMPUID);

        // then
        assertThatThrownBy(() ->  authenticationPaymentService.authenticatePayment(request1.toServiceRequest()))
                .isInstanceOf(BaseException.class)
                .hasMessage("주문 금액과 결제금액이 다릅니다.");

    }

    @DisplayName("존재하지 않는 IMPUID로 검증요청하는 경우 실패한다.")
    @WithMockUser(username = "ksr4037@naver.com")
    @Test
    void nonImpUid() {
        // given
        String email = "ksr4037@naver.com";
        AccountCreateServiceRequest accountCreateServiceRequest = AccountCreateServiceRequest.builder()
                .email(email)
                .password("1234")
                .gender(AccountCodeType.MAN)
                .age(AccountCodeType.TWENTIES)
                .name("최연재")
                .phoneNumber("010-1234-5678")
                .birthday(LocalDate.of(1997, 6, 24))
                .build();
        accountService.signup(accountCreateServiceRequest);
        String userId = email;

        List<OrderProductElement> giveaways = new ArrayList<>();
        giveaways.add(new OrderProductElement("컴포즈커피",3));
        giveaways.add(new OrderProductElement("BBQ후라이드치킨",2));
        giveawayRepository.save(new Giveaway(GiveawayType.COFFEE,"컴포즈커피",1300));
        giveawayRepository.save(new Giveaway(GiveawayType.CHICKEN,"BBQ후라이드치킨",20000));

        OrderCreateServiceRequest request = OrderCreateServiceRequest.builder().giveaways(giveaways)
                .requestTime(CORRECTTIME).build();
        OrderResponse saveOrder = registOrderService.createOrder(request);

        // when
        PaymentAuthenticationRequest request1 = new PaymentAuthenticationRequest(userId + "_" + CORRECTTIME , "-1");

        // then
        assertThatThrownBy(() ->  authenticationPaymentService.authenticatePayment(request1.toServiceRequest()))
                .isInstanceOf(BaseException.class)
                .hasMessage("iamport 응답 예외입니다.");
    }

    private void setAuthority() {
        Authority userAuthority = Authority.builder()
                .type(AuthorityType.ROLE_USER)
                .build();
        authorityRepository.save(userAuthority);
    }
    private void setAccountCode() {
        List<AccountCodeType> accountCodeTypes = List.of(AccountCodeType.MAN, AccountCodeType.WOMAN, AccountCodeType.UNDER_TEENS,
                AccountCodeType.TEENS, AccountCodeType.TWENTIES, AccountCodeType.THIRTIES,
                AccountCodeType.FORTIES, AccountCodeType.FIFTIES, AccountCodeType.SIXTIES);
        for (AccountCodeType accountCodeType : accountCodeTypes) {
            accountCodeRepository.save(AccountCode.builder()
                    .type(accountCodeType)
                    .build());
        }
    }

    
}