package com.logwiki.specialsurveyservice.api.service.order.response;

import com.logwiki.specialsurveyservice.domain.orders.Orders;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class OrderResponse {

    private final String orderId;
    private final Integer orderAmount;
    private final Boolean isSuccess;

    @Builder
    private OrderResponse(String orderId, Integer orderAmount, Boolean isSuccess) {
        this.orderId = orderId;
        this.orderAmount = orderAmount;
        this.isSuccess = isSuccess;
    }

    public static OrderResponse from(Orders order) {
        log.info(order.getOrderId() + " " + order.getOrderAmount());
        if (order == null) {
            return null;
        }

        return OrderResponse.builder().orderId(order.getOrderId())
                .orderAmount(order.getOrderAmount()).isSuccess(order.getIsSuccess()).build();
    }
}
