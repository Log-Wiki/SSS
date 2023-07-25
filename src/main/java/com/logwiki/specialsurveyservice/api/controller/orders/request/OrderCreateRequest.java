package com.logwiki.specialsurveyservice.api.controller.orders.request;

import com.logwiki.specialsurveyservice.api.service.order.request.OrderCreateServiceRequest;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderCreateRequest {

    private String giveawayName;
    private Integer giveawayNumber;

    private List<OrderCreateRequest> giveaways;

    @Builder
    private OrderCreateRequest(String giveawayName , Integer giveawayNumber) {
        this.giveawayName = giveawayName;
        this.giveawayNumber = giveawayNumber;
    }
    public OrderCreateServiceRequest toServiceRequest() {
        return OrderCreateServiceRequest.builder()
                .giveaways(giveaways)
                .build();
    }
}
