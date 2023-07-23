package com.logwiki.specialsurveyservice.domain.order;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {


  Optional<Order> findOneByOrderId(Long OrderId);
}
