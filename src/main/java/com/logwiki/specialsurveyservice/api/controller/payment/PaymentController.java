package com.logwiki.specialsurveyservice.api.controller.payment;


import com.logwiki.specialsurveyservice.api.controller.payment.request.PaymentAuthenticationRequest;
import com.logwiki.specialsurveyservice.api.service.payment.AuthenticationPaymentService;
import com.logwiki.specialsurveyservice.api.service.payment.response.PaymentResponse;
import com.logwiki.specialsurveyservice.api.utils.ApiResponse;
import com.logwiki.specialsurveyservice.api.utils.ApiUtils;
import com.siot.IamportRestClient.IamportClient;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class PaymentController {

    private final AuthenticationPaymentService authenticationPaymentService;
    private final IamportClient iamportClientApi;

    @Value("${apikey.iamport-apikey}")
    private String iamportAccessKey;

    @Value("${apikey.iamport-secretkey}")
    private String iamportSecretKey;

    public PaymentController(AuthenticationPaymentService authenticationPaymentService) {
        this.authenticationPaymentService = authenticationPaymentService;
        this.iamportClientApi = new IamportClient(iamportAccessKey
                , iamportSecretKey);
    }

    @PostMapping("/payment/authentication")
    public ApiResponse<PaymentResponse> authenticationPayment(
            @Valid @RequestBody PaymentAuthenticationRequest paymentAuthenticationRequest
    ) {
        return ApiUtils.success(authenticationPaymentService.authenticatePayment(
                paymentAuthenticationRequest.toServiceRequest() , iamportClientApi));
    }
}
