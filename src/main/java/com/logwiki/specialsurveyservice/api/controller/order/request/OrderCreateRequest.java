package com.logwiki.specialsurveyservice.api.controller.order.request;

import com.logwiki.specialsurveyservice.api.service.order.request.OrderCreateServiceRequest;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderCreateRequest {

    @NotEmpty(message = "주문 가격은 필수입니다.")
    private Integer orderAmount;

    @Builder
    private OrderCreateRequest(Integer orderAmount) {
        this.orderAmount = orderAmount;
    }

    public OrderCreateServiceRequest toServiceRequest() {
        return OrderCreateServiceRequest.builder()
                .orderAmount(orderAmount)
                .build();
    }
}
