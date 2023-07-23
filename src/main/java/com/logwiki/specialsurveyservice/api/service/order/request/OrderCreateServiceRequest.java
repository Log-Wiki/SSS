package com.logwiki.specialsurveyservice.api.service.order.request;

import com.logwiki.specialsurveyservice.api.controller.orders.request.OrderCreateRequest;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderCreateServiceRequest {

    private List<OrderCreateRequest> orderCreateRequestList;

    @Builder
    private OrderCreateServiceRequest(List<OrderCreateRequest> orderCreateRequestList) {
        this.orderCreateRequestList = orderCreateRequestList;
    }
}
