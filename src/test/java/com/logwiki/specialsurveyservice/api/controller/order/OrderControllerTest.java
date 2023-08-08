package com.logwiki.specialsurveyservice.api.controller.order;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.logwiki.specialsurveyservice.ControllerTestSupport;
import com.logwiki.specialsurveyservice.api.controller.orders.request.OrderCreateRequest;
import com.logwiki.specialsurveyservice.api.controller.question.request.QuestionAnswerCreateRequest;
import com.logwiki.specialsurveyservice.api.controller.question.request.QuestionAnswersCreateRequest;
import com.logwiki.specialsurveyservice.api.service.account.request.AccountCreateServiceRequest;
import com.logwiki.specialsurveyservice.api.service.giveaway.response.GiveawayResponse;
import com.logwiki.specialsurveyservice.api.service.order.response.OrderResponse;
import com.logwiki.specialsurveyservice.api.service.question.response.QuestionAnswerResponse;
import com.logwiki.specialsurveyservice.domain.accountcode.AccountCode;
import com.logwiki.specialsurveyservice.domain.accountcode.AccountCodeRepository;
import com.logwiki.specialsurveyservice.domain.accountcode.AccountCodeType;
import com.logwiki.specialsurveyservice.domain.authority.Authority;
import com.logwiki.specialsurveyservice.domain.authority.AuthorityRepository;
import com.logwiki.specialsurveyservice.domain.authority.AuthorityType;
import com.logwiki.specialsurveyservice.domain.giveaway.Giveaway;
import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayRepository;
import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayType;
import com.logwiki.specialsurveyservice.domain.orders.OrderProductElement;
import com.logwiki.specialsurveyservice.domain.orders.Orders;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;

class OrderControllerTest extends ControllerTestSupport {
    @DisplayName("주문 등록 테스트")
    @WithMockUser
    @Test
    void createOrder() throws Exception {
        // given
        List<OrderProductElement> orderProductElements = new ArrayList<>();
        orderProductElements.add(new OrderProductElement("컴포즈커피",3));
        String id = "testid";
        int orderAmount = 5000;
        boolean isVerificated = false;
        Orders Orders = new Orders(id,orderAmount,isVerificated);
        OrderResponse orderResponse = OrderResponse.from(Orders);
        OrderCreateRequest orderCreateRequest = new OrderCreateRequest(orderProductElements);
        when(registOrderService.createOrder(any())).thenReturn(orderResponse);
        mockMvc.perform(
                //when
                        post("/api/order/regist")
                                .content(objectMapper.writeValueAsString(orderCreateRequest))
                                        .contentType(MediaType.APPLICATION_JSON)
                                                .with(csrf())
                )
                //then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.response.orderId").value(id))
                .andExpect(jsonPath("$.response.orderAmount").value(5000))
                .andExpect(jsonPath("$.response.isVerificated").value(isVerificated));
    }

    @DisplayName("주문 상품이 없으면 주문등록이 불가능하다.")
    @WithMockUser
    @Test
    void createOrderWithNoProduct() throws Exception {
        // given
        String id = "testid";
        int orderAmount = 5000;
        boolean isVerificated = false;
        Orders Orders = new Orders(id,orderAmount,isVerificated);
        OrderResponse orderResponse = OrderResponse.from(Orders);
        OrderCreateRequest orderCreateRequest = new OrderCreateRequest();
        when(registOrderService.createOrder(any())).thenReturn(orderResponse);
        mockMvc.perform(
                //when
                        post("/api/order/regist")
                                .content(objectMapper.writeValueAsString(orderCreateRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                //then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.apiError.message").value("주문 상품은 필수 입니다."));
    }

}
