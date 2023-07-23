package com.logwiki.specialsurveyservice.api.service.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.logwiki.specialsurveyservice.IntegrationTestSupport;
import com.logwiki.specialsurveyservice.api.controller.orders.request.OrderCreateRequest;
import com.logwiki.specialsurveyservice.api.service.account.SignupAccountService;
import com.logwiki.specialsurveyservice.api.service.account.request.AccountCreateServiceRequest;
import com.logwiki.specialsurveyservice.api.service.account.response.AccountResponse;
import com.logwiki.specialsurveyservice.api.service.order.request.OrderCreateServiceRequest;
import com.logwiki.specialsurveyservice.api.service.order.response.OrderResponse;
import com.logwiki.specialsurveyservice.domain.authority.Authority;
import com.logwiki.specialsurveyservice.domain.authority.AuthorityRepository;
import com.logwiki.specialsurveyservice.domain.authority.AuthorityType;
import com.logwiki.specialsurveyservice.domain.gender.Gender;
import com.logwiki.specialsurveyservice.exception.DuplicatedAccountException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class RegistOrderServiceTest extends IntegrationTestSupport {

  @Autowired
  private RegistOrderService registOrderService;

  @DisplayName("주문 금액을 사용해서 주문 번호를 자동생성하여 주문을 생성한다.")
  @Test
  void regist() {
    List<OrderCreateRequest> orderCreateRequestList = new ArrayList<>();

    orderCreateRequestList.add(OrderCreateRequest.builder().giveawayName("COFFEE").giveawayNumber(3).build());
    orderCreateRequestList.add(OrderCreateRequest.builder().giveawayName("CHICKEN").giveawayNumber(2).build());
    OrderCreateServiceRequest orderCreateServiceRequest = OrderCreateServiceRequest
            .builder()
            .orderCreateRequestList(orderCreateRequestList)
            .build();
    OrderResponse orderResponse = registOrderService.regist(orderCreateServiceRequest);
    assertThat(orderResponse).isNotNull();
    assertThat(orderResponse.getOrderAmount()).isEqualTo(43900);
    assertThat(orderResponse.getOrderId()).isEqualTo(1L);
  }

}