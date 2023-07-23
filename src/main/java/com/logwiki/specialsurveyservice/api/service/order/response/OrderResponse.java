package com.logwiki.specialsurveyservice.api.service.order.response;

import com.logwiki.specialsurveyservice.domain.order.Order;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderResponse {

    private final Long orderId;
    private final Integer orderAmount;
    private final Boolean isSucess;

    @Builder
    private OrderResponse(Long orderId, Integer orderAmount, Boolean isSucess) {
        this.orderId = orderId;
        this.orderAmount = orderAmount;
        this.isSucess = isSucess;
    }

    public static OrderResponse from(Order order) {
        if (order == null) {
            return null;
        }

        return OrderResponse.builder().orderId(order.getOrderId())
                .orderAmount(order.getOrderAmount()).isSucess(order.getIsSuccess()).build();
    }
}
