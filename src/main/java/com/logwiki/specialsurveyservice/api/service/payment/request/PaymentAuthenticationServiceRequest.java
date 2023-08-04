package com.logwiki.specialsurveyservice.api.service.payment.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentAuthenticationServiceRequest {

    private String orderId;

    private String impUid;


    @Builder
    private PaymentAuthenticationServiceRequest(String orderId , String impUid) {
        this.orderId = orderId;
        this.impUid = impUid;
    }
}
