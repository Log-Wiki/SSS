package com.logwiki.specialsurveyservice.domain.orders;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, String> {


  Optional<Orders> findOneByOrderId(String OrderId);
}
