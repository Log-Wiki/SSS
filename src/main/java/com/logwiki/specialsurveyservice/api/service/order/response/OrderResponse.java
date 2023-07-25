package com.logwiki.specialsurveyservice.api.service.order.response;

import com.logwiki.specialsurveyservice.domain.orders.Orders;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderResponse {

    private final Long orderId;
    private final Integer orderAmount;
    private final Boolean isSuccess;

    @Builder
    private OrderResponse(Long orderId, Integer orderAmount, Boolean isSuccess) {
        this.orderId = orderId;
        this.orderAmount = orderAmount;
        this.isSuccess = isSuccess;
    }

    public static OrderResponse from(Orders order) {
        if (order == null) {
            return null;
        }

        return OrderResponse.builder().orderId(order.getOrderId())
                .orderAmount(order.getOrderAmount()).isSuccess(order.getIsSuccess()).build();
    }
}
