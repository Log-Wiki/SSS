package com.logwiki.specialsurveyservice.api.service.order;

import com.logwiki.specialsurveyservice.api.service.order.request.OrderCreateServiceRequest;
import com.logwiki.specialsurveyservice.api.service.order.response.OrderResponse;
import com.logwiki.specialsurveyservice.domain.order.Order;
import com.logwiki.specialsurveyservice.domain.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class RegistOrderService {

  private final OrderRepository orderRepository;
  @Transactional
  public OrderResponse regist(OrderCreateServiceRequest request) {

    Order order = Order.create(
        request.getOrderAmount()
    );

    return OrderResponse.from(orderRepository.save(order));
  }
}
