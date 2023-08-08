package com.logwiki.specialsurveyservice.api.service.order.request;

import com.logwiki.specialsurveyservice.domain.orders.OrderProductElement;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderCreateServiceRequest {
    private List<OrderProductElement> giveaways;
    private Long requestTime;
    @Builder
    private OrderCreateServiceRequest(List<OrderProductElement> giveaways , Long requestTime) {
        this.giveaways = giveaways;
        this.requestTime = requestTime;
    }
}
