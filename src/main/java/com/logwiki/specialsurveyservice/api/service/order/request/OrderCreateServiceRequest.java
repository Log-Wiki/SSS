package com.logwiki.specialsurveyservice.api.service.order.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderCreateServiceRequest {

    private Integer orderAmount;

    @Builder
    private OrderCreateServiceRequest(Integer orderAmount) {
        this.orderAmount = orderAmount;
    }
}
