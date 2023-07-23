package com.logwiki.specialsurveyservice.api.controller.payment.request;

import com.logwiki.specialsurveyservice.api.service.order.request.OrderCreateServiceRequest;
import com.logwiki.specialsurveyservice.api.service.payment.request.PaymentAuthenticationServiceRequest;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentAuthenticationRequest {

    @NotEmpty(message = "주문 ID는 필수입니다.")
    private Long orderId;

    @NotEmpty(message = "결제 ID는 필수입니다.")
    private String imp_uid;


    @Builder
    private PaymentAuthenticationRequest(Long orderId , String imp_uid) {
        this.orderId = orderId;
        this.imp_uid = imp_uid;
    }

    public PaymentAuthenticationServiceRequest toServiceRequest() {
        return PaymentAuthenticationServiceRequest.builder()
                .orderId(orderId)
                .imp_uid(imp_uid)
                .build();
    }
}
