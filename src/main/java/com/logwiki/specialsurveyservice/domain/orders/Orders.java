package com.logwiki.specialsurveyservice.domain.orders;

import com.logwiki.specialsurveyservice.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Orders extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long orderId;

  private Integer orderAmount;

  private Boolean isSuccess;
  @Builder
  public Orders(Long orderId , Integer orderAmount , Boolean isSuccess) {
     this.orderId = orderId;
     this.orderAmount = orderAmount;
     this.isSuccess = isSuccess;
  }

    public static Orders create(Integer orderAmount) {
        return Orders.builder()
            .orderAmount(orderAmount)
            .build();
    }
}
