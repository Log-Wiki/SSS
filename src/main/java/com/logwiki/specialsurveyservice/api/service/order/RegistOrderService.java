package com.logwiki.specialsurveyservice.api.service.order;

import com.logwiki.specialsurveyservice.domain.orders.OrderProductElement;
import com.logwiki.specialsurveyservice.api.service.order.request.OrderCreateServiceRequest;
import com.logwiki.specialsurveyservice.api.service.order.response.OrderResponse;
import com.logwiki.specialsurveyservice.domain.giveaway.Giveaway;
import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayRepository;
import com.logwiki.specialsurveyservice.domain.orders.Orders;
import com.logwiki.specialsurveyservice.domain.orders.OrdersRepository;
import java.util.Optional;

import com.logwiki.specialsurveyservice.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Slf4j
@RequiredArgsConstructor
@Service
public class RegistOrderService {

  private final OrdersRepository orderRepository;
  private final GiveawayRepository giveawayRepository;
  private static final boolean NotPaid = false;
  @Transactional
  public OrderResponse createOrder(OrderCreateServiceRequest request) {
    int orderAmount = 0;
    for(OrderProductElement orderCreateRequest : request.getGiveaways()){
      Optional<Giveaway> giveaway = giveawayRepository.findGiveawayByName(orderCreateRequest.getGiveawayName());
      if(giveaway.isEmpty()) {
        throw new BaseException("주문 상품이 존재하지 않습니다." , 4000);
      }
      orderAmount += giveaway.get().getPrice() * orderCreateRequest.getGiveawayNumber();
    }

    if(orderAmount == 0) {
      throw new BaseException("주문 금액이 0원입니다.", 4001);
    }

    Orders order = Orders.create(
            request.getUserId() + "_" + request.getRequestTime(),
            orderAmount,
            NotPaid
    );

    return OrderResponse.from(orderRepository.save(order));
  }
}
