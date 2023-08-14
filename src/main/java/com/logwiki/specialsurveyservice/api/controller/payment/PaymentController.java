package com.logwiki.specialsurveyservice.api.controller.payment;


import com.logwiki.specialsurveyservice.api.controller.payment.request.PaymentAuthenticationRequest;
import com.logwiki.specialsurveyservice.api.service.payment.AuthenticationPaymentService;
import com.logwiki.specialsurveyservice.api.service.payment.response.PaymentResponse;
import com.logwiki.specialsurveyservice.api.utils.ApiResponse;
import com.logwiki.specialsurveyservice.api.utils.ApiUtils;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class PaymentController {

    private final AuthenticationPaymentService authenticationPaymentService;


    public PaymentController(AuthenticationPaymentService authenticationPaymentService) {
        this.authenticationPaymentService = authenticationPaymentService;
    }

    @PostMapping("/payment/authentication")
    public ApiResponse<PaymentResponse> authenticationPayment(
            @Valid @RequestBody PaymentAuthenticationRequest paymentAuthenticationRequest
    ) {
        return ApiUtils.success(authenticationPaymentService.authenticatePayment(
                paymentAuthenticationRequest.toServiceRequest()));
    }

    @GetMapping("/payment/{surveyId}")
    public ApiResponse<Payment> authenticationPayment(
            @PathVariable Long surveyId
    ) {
        return ApiUtils.success(authenticationPaymentService.getPaymentInfo(surveyId));
    }
}
