package com.logwiki.specialsurveyservice.api.controller.payment.request;

import com.logwiki.specialsurveyservice.api.service.payment.request.PaymentAuthenticationServiceRequest;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentAuthenticationRequest {
    @NotNull(message = "설문 ID는 필수입니다.")
    private Long surveyId;

    @NotEmpty(message = "주문 ID는 필수입니다.")
    private String orderId;

    @NotEmpty(message = "결제 ID는 필수입니다.")
    private String impUid;


    @Builder
    public PaymentAuthenticationRequest(Long surveyId, String orderId, String impUid) {
        this.surveyId = surveyId;
        this.orderId = orderId;
        this.impUid = impUid;
    }

    public PaymentAuthenticationServiceRequest toServiceRequest() {
        return PaymentAuthenticationServiceRequest.builder()
                .surveyId(surveyId)
                .orderId(orderId)
                .impUid(impUid)
                .build();
    }
}
