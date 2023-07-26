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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class RegistOrderService {

  private final OrdersRepository orderRepository;
  private final GiveawayRepository giveawayRepository;
  @Transactional
  public OrderResponse regist(OrderCreateServiceRequest request) {
    int orderAmount = 0;
    for(OrderCreateRequest orderCreateRequest : request.getGiveaways()){
      Optional<Giveaway> giveaway = giveawayRepository.findGiveawayByName(orderCreateRequest.getGiveawayName());
      orderAmount += giveaway.get().getPrice() * orderCreateRequest.getGiveawayNumber();
    }


    Orders order = Orders.create(
        orderAmount
    );

    return OrderResponse.from(orderRepository.save(order));
  }
}
