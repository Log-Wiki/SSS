package com.logwiki.specialsurveyservice.api.service.payment.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentAuthenticationServiceRequest {

    @NotEmpty(message = "주문 ID는 필수입니다.")
    private String orderId;

    @NotEmpty(message = "결제 ID는 필수입니다.")
    private String imp_uid;


    @Builder
    private PaymentAuthenticationServiceRequest(String orderId , String imp_uid) {
        this.orderId = orderId;
        this.imp_uid = imp_uid;
    }
}
