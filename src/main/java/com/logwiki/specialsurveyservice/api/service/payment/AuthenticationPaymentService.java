package com.logwiki.specialsurveyservice.api.service.payment;

import com.logwiki.specialsurveyservice.api.service.payment.request.PaymentAuthenticationServiceRequest;
import com.logwiki.specialsurveyservice.api.service.payment.response.PaymentResponse;
import com.logwiki.specialsurveyservice.domain.order.Order;
import com.logwiki.specialsurveyservice.domain.order.OrderRepository;
import com.logwiki.specialsurveyservice.exception.DuplicatedAccountException;
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

    // iamport api 사용해서 결제정보 가져오기
    // order 테이블에서 주문정보 가져오기
    // 두 정보의 가격이 같으면 정상
    private final OrderRepository orderRepository;

    @Transactional
    public PaymentResponse authentication(PaymentAuthenticationServiceRequest request,
            IamportClient iamportClientApi) {

        Order order = orderRepository.findOneByOrderId(request.getOrderId()).orElse(null);

        if (order == null) {
            throw new DuplicatedAccountException("주문 정보가 없는 결제인증 요청입니다.");
        }

        IamportResponse<Payment> iamportResponse = null;
        try {
            iamportResponse = iamportClientApi.paymentByImpUid(request.getImp_uid());
        } catch (IamportResponseException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            if (iamportResponse.getResponse().getAmount().intValue() != order.getOrderAmount()) {
                throw new DuplicatedAccountException("주문 금액과 결제금액이 다릅니다.");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return PaymentResponse.from();
    }

}
