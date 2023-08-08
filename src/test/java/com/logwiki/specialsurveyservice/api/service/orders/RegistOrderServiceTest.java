package com.logwiki.specialsurveyservice.api.service.orders;

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
import com.logwiki.specialsurveyservice.api.service.giveaway.GiveawayService;
import com.logwiki.specialsurveyservice.api.service.order.RegistOrderService;
import com.logwiki.specialsurveyservice.api.service.order.request.OrderCreateServiceRequest;
import com.logwiki.specialsurveyservice.api.service.order.response.OrderResponse;
import com.logwiki.specialsurveyservice.domain.giveaway.Giveaway;
import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayRepository;
import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayType;
import com.logwiki.specialsurveyservice.exception.BaseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class RegistOrderServiceTest extends IntegrationTestSupport {

    @Autowired
    RegistOrderService registOrderService;

    @Autowired
    GiveawayRepository giveawayRepository;
    @Autowired
    GiveawayService giveawayService;
    @Autowired
    AccountService accountService;
    @Autowired
    AuthorityRepository authorityRepository;
    @Autowired
    AccountCodeRepository accountCodeRepository;

    @BeforeEach
    void setUp() {
        setAuthority();
        setAccountCode();
    }
    @DisplayName("사용자ID , {상품명 , 상품금액}리스트로 주문을 등록한다.")
    @WithMockUser(username = "duswo0624@naver.com")
    @Test
    void registOrder() {
        // given
        String email = "duswo0624@naver.com";
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

        List<OrderProductElement> giveaways = new ArrayList<>();
        giveaways.add(new OrderProductElement("컴포즈커피",3));
        giveaways.add(new OrderProductElement("BBQ후라이드치킨",2));
        giveawayRepository.save(new Giveaway(GiveawayType.COFFEE,"컴포즈커피",1300));
        giveawayRepository.save(new Giveaway(GiveawayType.CHICKEN,"BBQ후라이드치킨",20000));
        
        OrderCreateServiceRequest request = OrderCreateServiceRequest.builder().giveaways(giveaways).build();
        // when
        OrderResponse saveOrder = registOrderService.createOrder(request);

        // then
        assertThat(saveOrder).isNotNull();
        assertThat(saveOrder)
                .extracting( "orderAmount", "isVerificated")
                .contains( 43900 , false);

    }

    @DisplayName("주문 금액이 0원이어서는 안된다.")
    @WithMockUser(username = "duswo0624@naver.com")
    @Test
    void registOrderWithNoMoney() {
        // given
        String email = "duswo0624@naver.com";
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

        List<OrderProductElement> giveaways = new ArrayList<>();
        giveaways.add(new OrderProductElement("컴포즈커피",0));
        giveaways.add(new OrderProductElement("BBQ후라이드치킨",0));
        giveawayRepository.save(new Giveaway(GiveawayType.COFFEE,"컴포즈커피",1300));
        giveawayRepository.save(new Giveaway(GiveawayType.CHICKEN,"BBQ후라이드치킨",20000));

        OrderCreateServiceRequest request = OrderCreateServiceRequest.builder().giveaways(giveaways).build();
        // when // then
        assertThatThrownBy(() ->registOrderService.createOrder(request))
                .isInstanceOf(BaseException.class)
                .hasMessage("주문 금액이 0원입니다.");

    }

    @DisplayName("입력받은 주문상품이 존재하지 않습니다.")
    @WithMockUser(username = "duswo0624@naver.com")
    @Test
    void registOrderWithNoProduct() {
        // given
        String email = "duswo0624@naver.com";
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

        List<OrderProductElement> giveaways = new ArrayList<>();
        giveaways.add(new OrderProductElement("컴포즈커피",3));
        giveaways.add(new OrderProductElement("BBQ후라이드치킨",2));
        giveawayRepository.save(new Giveaway(GiveawayType.CHICKEN,"BBQ후라이드치킨",20000));

        OrderCreateServiceRequest request = OrderCreateServiceRequest.builder().giveaways(giveaways).build();
        // when // then
        assertThatThrownBy(() ->registOrderService.createOrder(request))
                .isInstanceOf(BaseException.class)
                .hasMessage("주문 상품이 존재하지 않습니다.");

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