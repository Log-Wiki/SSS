package com.logwiki.specialsurveyservice.api.service.payment.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentAuthenticationServiceRequest {
    private Long surveyId;

    private String orderId;

    private String impUid;


    @Builder
    private PaymentAuthenticationServiceRequest(Long surveyId, String orderId , String impUid) {
        this.surveyId = surveyId;
        this.orderId = orderId;
        this.impUid = impUid;
    }
}
