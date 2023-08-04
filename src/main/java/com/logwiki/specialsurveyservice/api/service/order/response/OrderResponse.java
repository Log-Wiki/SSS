package com.logwiki.specialsurveyservice.api.service.order.response;

import com.logwiki.specialsurveyservice.domain.orders.Orders;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@Getter
public class OrderResponse {

    private final String orderId;
    private final Integer orderAmount;
    private final Boolean isVerificated;
    @Value("${apikey.iamport-storekey}")
    private final String storeId;

    @Builder
    private OrderResponse(String orderId, Integer orderAmount, Boolean isVerificated,
            String storeId) {
        this.orderId = orderId;
        this.orderAmount = orderAmount;
        this.isVerificated = isVerificated;
        this.storeId = storeId;
    }

    public static OrderResponse from(Orders order) {
        return OrderResponse.builder().orderId(order.getOrderId())
                .orderAmount(order.getOrderAmount()).isVerificated(order.getIsVerificated()).build();
    }
}
