package com.logwiki.specialsurveyservice.api.controller.orders.request;

import com.logwiki.specialsurveyservice.api.service.order.request.OrderCreateServiceRequest;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderCreateRequest {

    private String giveawayName;
    private Integer giveawayNumber;

    private List<OrderCreateRequest> giveawayList;

    @Builder
    private OrderCreateRequest(String giveawayName , Integer giveawayNumber) {
        this.giveawayName = giveawayName;
        this.giveawayNumber = giveawayNumber;
    }
    public OrderCreateServiceRequest toServiceRequest() {
        return OrderCreateServiceRequest.builder()
                .orderCreateRequestList(giveawayList)
                .build();
    }
//    @NotEmpty(message = "주문 가격은 필수입니다.")
//    private Integer orderAmount;
//
//    @Builder
//    private OrderCreateRequest(Integer orderAmount) {
//        this.orderAmount = orderAmount;
//    }
//
//    public OrderCreateServiceRequest toServiceRequest() {
//        return OrderCreateServiceRequest.builder()
//                .orderAmount(orderAmount)
//                .build();
//    }
}
