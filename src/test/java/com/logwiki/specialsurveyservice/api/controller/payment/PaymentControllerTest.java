package com.logwiki.specialsurveyservice.api.controller.payment;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.logwiki.specialsurveyservice.ControllerTestSupport;
import com.logwiki.specialsurveyservice.api.controller.orders.request.OrderCreateRequest;
import com.logwiki.specialsurveyservice.api.controller.payment.request.PaymentAuthenticationRequest;
import com.logwiki.specialsurveyservice.api.service.order.response.OrderResponse;
import com.logwiki.specialsurveyservice.api.service.payment.response.PaymentResponse;
import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayRepository;
import com.logwiki.specialsurveyservice.domain.orders.OrderProductElement;
import com.logwiki.specialsurveyservice.domain.orders.Orders;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

class PaymentControllerTest extends ControllerTestSupport {
    @DisplayName("결제 검증 테스트")
    @WithMockUser
    @Test
    void createOrder() throws Exception {
        // given
        String orderId = "orderId";
        String impUid = "impUid";
        int orderAmount = 5000;
        String isSuccess = "paid";
        PaymentAuthenticationRequest request = PaymentAuthenticationRequest.builder()
                .orderId(orderId)
                .impUid(impUid)
                .build();
        PaymentResponse response = PaymentResponse.builder()
                .impUid(impUid)
                .orderId(orderId)
                .orderAmount(orderAmount)
                .isSucess(isSuccess).build();
        when(authenticationPaymentService.authenticatePayment(any())).thenReturn(response);
        mockMvc.perform(
                //when
                        post("/api/payment/authentication")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                //then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.response.orderId").value(orderId))
                .andExpect(jsonPath("$.response.orderAmount").value(orderAmount))
                .andExpect(jsonPath("$.response.imp_uid").value(impUid))
                .andExpect(jsonPath("$.response.isSucess").value(isSuccess))
        ;

    }
}
