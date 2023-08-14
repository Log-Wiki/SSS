package com.logwiki.specialsurveyservice.api.service.payment;

import com.logwiki.specialsurveyservice.api.service.payment.request.PaymentAuthenticationServiceRequest;
import com.logwiki.specialsurveyservice.api.service.payment.response.PaymentResponse;
import com.logwiki.specialsurveyservice.api.service.survey.SurveyService;
import com.logwiki.specialsurveyservice.api.service.survey.response.SurveyResponse;
import com.logwiki.specialsurveyservice.domain.orders.Orders;
import com.logwiki.specialsurveyservice.domain.orders.OrdersRepository;
import com.logwiki.specialsurveyservice.exception.BaseException;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Service
public class AuthenticationPaymentService {
    private final OrdersRepository orderRepository;
    private final SurveyService surveyService;

    private IamportClient iamportClientApi;

    @Value("${apikey.iamport-apikey}")
    private  String iamportAccessKey;

    @Value("${apikey.iamport-secretkey}")
    private String iamportSecretKey;

    private final static Boolean orderSuccess = true;


    @Transactional
    public PaymentResponse authenticatePayment(PaymentAuthenticationServiceRequest request) {
        iamportClientApi = new IamportClient(iamportAccessKey , iamportSecretKey);

        Orders order = orderRepository.findOneByOrderId(request.getOrderId()).orElse(null);

        if (order == null) {
            throw new BaseException("주문 정보가 없는 결제인증 요청입니다." , 4002);
        }

        IamportResponse<Payment> iamportResponse;
        try {
            iamportResponse = iamportClientApi.paymentByImpUid(request.getImpUid());
        } catch (IamportResponseException | IOException e) {
            throw new BaseException("iamport 응답 예외입니다." , 4002);
        }


        if (iamportResponse.getResponse().getAmount().intValue() != order.getOrderAmount()) {
            throw new BaseException("주문 금액과 결제금액이 다릅니다.", 4004);
        }
        Orders successOrder = Orders.builder().orderId(order.getOrderId())
                .orderAmount(order.getOrderAmount())
                .impUid(iamportResponse.getResponse().getImpUid())
                .surveyId(request.getSurveyId())
                .isVerificated(orderSuccess)
                .build();

        orderRepository.save(successOrder);
        return PaymentResponse.from(iamportResponse.getResponse());
    }

    public Payment getPaymentInfo(Long surveyId) {
        Optional<Orders> ordersOptional = orderRepository.findOneBySurveyId(surveyId);
        if(ordersOptional.isEmpty()) {
            throw  new BaseException("설문아이디에 해당하는 주문내역이 존재하지 않습니다." , 4006);
        }
        Orders orders = ordersOptional.get();
        IamportResponse<Payment> iamportResponse;
        try {
            iamportResponse = iamportClientApi.paymentByImpUid(orders.getImpUid());
        } catch (IamportResponseException | IOException e) {
            throw new BaseException("iamport 응답 예외입니다." , 4002);
        }
        return  iamportResponse.getResponse();
    }
}
