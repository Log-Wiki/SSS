package com.logwiki.specialsurveyservice.api.controller.order;

import com.logwiki.specialsurveyservice.ControllerTestSupport;
import com.logwiki.specialsurveyservice.api.controller.orders.request.OrderCreateRequest;
import com.logwiki.specialsurveyservice.api.service.order.response.OrderResponse;
import com.logwiki.specialsurveyservice.domain.orders.OrderProductElement;
import com.logwiki.specialsurveyservice.domain.orders.Orders;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderControllerTest extends ControllerTestSupport {
    @DisplayName("주문 등록 테스트")
    @WithMockUser
    @Test
    @Disabled
    void createOrder() throws Exception {
        // given
        List<OrderProductElement> orderProductElements = new ArrayList<>();
        orderProductElements.add(new OrderProductElement("컴포즈커피", 3));
        String id = "testid";
        int orderAmount = 5000;
        boolean isVerificated = false;
        Orders Orders = new Orders(id, orderAmount, isVerificated);
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
        Orders Orders = new Orders(id, orderAmount, isVerificated);
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
