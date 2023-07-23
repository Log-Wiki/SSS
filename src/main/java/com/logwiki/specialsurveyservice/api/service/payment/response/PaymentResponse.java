package com.logwiki.specialsurveyservice.api.service.payment.response;

import com.logwiki.specialsurveyservice.domain.order.Order;
import com.siot.IamportRestClient.response.Payment;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PaymentResponse {

    private final String imp_uid;
    private final Long orderId;
    private final Integer orderAmount;
    private final String isSucess;

    @Builder
    private PaymentResponse(String impUid, Long orderId, Integer orderAmount, String isSucess) {
        imp_uid = impUid;
        this.orderId = orderId;
        this.orderAmount = orderAmount;
        this.isSucess = isSucess;
    }

    public static PaymentResponse from(Payment payment) {
        if (payment == null) {
            return null;
        }

        return PaymentResponse.builder().impUid(payment.getImpUid()).orderId(Long.parseLong(payment.getMerchantUid()))
                .orderAmount(payment.getAmount().intValue()).isSucess(payment.getStatus()).build();
    }
}
