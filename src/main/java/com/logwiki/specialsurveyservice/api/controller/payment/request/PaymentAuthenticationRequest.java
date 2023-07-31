package com.logwiki.specialsurveyservice.api.controller.payment.request;

import com.logwiki.specialsurveyservice.api.service.payment.request.PaymentAuthenticationServiceRequest;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentAuthenticationRequest {

    @NotEmpty(message = "주문 ID는 필수입니다.")
    private String orderId;

    @NotEmpty(message = "결제 ID는 필수입니다.")
    private String impUid;


    @Builder
    public PaymentAuthenticationRequest(String orderId, String impUid) {
        this.orderId = orderId;
        this.impUid = impUid;
    }

    public PaymentAuthenticationServiceRequest toServiceRequest() {
        return PaymentAuthenticationServiceRequest.builder()
                .orderId(orderId)
                .impUid(impUid)
                .build();
    }
}
