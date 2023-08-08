package com.logwiki.specialsurveyservice.api.controller.orders.request;

import com.logwiki.specialsurveyservice.api.service.order.request.OrderCreateServiceRequest;
import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayType;
import com.logwiki.specialsurveyservice.domain.orders.OrderProductElement;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderCreateRequest {

    @NotNull(message = "주문 상품은 필수 입니다.")
    private List<OrderProductElement> giveaways;

    @Builder
    public OrderCreateRequest(List<OrderProductElement> giveaways) {
        this.giveaways = giveaways;
    }
    @Builder
    public OrderCreateServiceRequest toServiceRequest(Long requestTime) {
        return OrderCreateServiceRequest.builder()
                .giveaways(giveaways)
                .requestTime(requestTime)
                .build();
    }
}
