package com.logwiki.specialsurveyservice.api.service.order.request;

import com.logwiki.specialsurveyservice.api.controller.orders.request.OrderCreateRequest;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderCreateServiceRequest {
    private String userId;
    private List<OrderCreateRequest> giveaways;
    private Long requestTime;
    @Builder
    private OrderCreateServiceRequest(String userId, List<OrderCreateRequest> giveaways , Long requestTime) {
        this.userId = userId;
        this.giveaways = giveaways;
        this.requestTime = requestTime;
    }
}
