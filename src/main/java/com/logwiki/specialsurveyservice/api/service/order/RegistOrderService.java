package com.logwiki.specialsurveyservice.api.service.order;

import com.logwiki.specialsurveyservice.api.controller.orders.request.OrderCreateRequest;
import com.logwiki.specialsurveyservice.api.service.order.request.OrderCreateServiceRequest;
import com.logwiki.specialsurveyservice.api.service.order.response.OrderResponse;
import com.logwiki.specialsurveyservice.domain.giveaway.Giveaway;
import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayRepository;
import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayType;
import com.logwiki.specialsurveyservice.domain.giveawayPrice.GiveawayPrice;
import com.logwiki.specialsurveyservice.domain.orders.Orders;
import com.logwiki.specialsurveyservice.domain.orders.OrdersRepository;
import java.util.Optional;
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
  @Transactional
  public OrderResponse regist(OrderCreateServiceRequest request) {
    int orderAmount = 0;
    log.info(String.valueOf(orderAmount));
    for(OrderCreateRequest orderCreateRequest : request.getGiveaways()){
      Optional<Giveaway> giveaway = giveawayRepository.findGiveawayByName(orderCreateRequest.getGiveawayName());
      log.info(String.valueOf(giveaway.isPresent()));
      orderAmount += giveaway.get().getPrice() * orderCreateRequest.getGiveawayNumber();
    }

    log.info(String.valueOf(orderAmount));
    Orders order = Orders.create(
            request.getUserId() + "_" + request.getRequestTime(),
            orderAmount,
            false
    );

    return OrderResponse.from(orderRepository.save(order));
  }
}
