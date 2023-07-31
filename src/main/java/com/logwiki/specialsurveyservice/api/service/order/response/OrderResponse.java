package com.logwiki.specialsurveyservice.api.service.order.response;

import com.logwiki.specialsurveyservice.domain.orders.Orders;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class OrderResponse {

    @NotNull(message = "주문 ID는 필수 입니다")
    private final String orderId;
    @NotNull(message = "주문 금액은 필수 입니다")
    private final Integer orderAmount;
    @NotNull(message = "주문 결제여부는 필수 입니다")
    private final Boolean isVerificated;

    @Builder
    private OrderResponse(String orderId, Integer orderAmount, Boolean isVerificated) {
        this.orderId = orderId;
        this.orderAmount = orderAmount;
        this.isVerificated = isVerificated;
    }

    public static OrderResponse from(Orders order) {
        return OrderResponse.builder().orderId(order.getOrderId())
                .orderAmount(order.getOrderAmount()).isVerificated(order.getIsSuccess()).build();
    }
}
