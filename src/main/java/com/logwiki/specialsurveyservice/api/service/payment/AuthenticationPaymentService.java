package com.logwiki.specialsurveyservice.api.service.payment;

import com.logwiki.specialsurveyservice.api.service.payment.request.PaymentAuthenticationServiceRequest;
import com.logwiki.specialsurveyservice.api.service.payment.response.PaymentResponse;
import com.logwiki.specialsurveyservice.domain.orders.Orders;
import com.logwiki.specialsurveyservice.domain.orders.OrdersRepository;
import com.logwiki.specialsurveyservice.exception.BaseException;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Service
public class AuthenticationPaymentService {
    private final OrdersRepository orderRepository;

    @Transactional
    public PaymentResponse authentication(PaymentAuthenticationServiceRequest request,
            IamportClient iamportClientApi) {

        Orders order = orderRepository.findOneByOrderId(request.getOrderId()).orElse(null);

        if (order == null) {
            throw new BaseException("주문 정보가 없는 결제인증 요청입니다." , 2000);
        }

        IamportResponse<Payment> iamportResponse;
        try {
            iamportResponse = iamportClientApi.paymentByImpUid(request.getImp_uid());
        } catch (IamportResponseException e) {
            throw new BaseException("iamport 응답 예외입니다." , 2001);
        } catch (IOException e) {
            throw new BaseException("iamport IO 예외입니다." , 2002);
        }

        try {
            if (iamportResponse.getResponse().getAmount().intValue() != order.getOrderAmount()) {
                throw new BaseException("주문 금액과 결제금액이 다릅니다.", 2003);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return PaymentResponse.from(iamportResponse.getResponse());
    }

}
