package com.logwiki.specialsurveyservice.api.controller.orders.request;

import com.logwiki.specialsurveyservice.api.service.order.request.OrderCreateServiceRequest;
import com.logwiki.specialsurveyservice.domain.orders.OrderProductElement;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderCreateRequest {

    @NotNull
    private List<OrderProductElement> giveaways;

    @Builder
    public OrderCreateServiceRequest toServiceRequest(String userId , Long requestTime) {
        return OrderCreateServiceRequest.builder()
                .userId(userId)
                .giveaways(giveaways)
                .requestTime(requestTime)
                .build();
    }
}
