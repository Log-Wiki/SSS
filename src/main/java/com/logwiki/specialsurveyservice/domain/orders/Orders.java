package com.logwiki.specialsurveyservice.domain.orders;

import com.logwiki.specialsurveyservice.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@lombok.Generated
public class Orders extends BaseEntity {

  @Id
  private String orderId;

  private Integer orderAmount;

  private Boolean isVerificated;

  @Builder
  public Orders(String orderId , Integer orderAmount , Boolean isVerificated) {
     this.orderId = orderId;
     this.orderAmount = orderAmount;
     this.isVerificated = isVerificated;
  }

    public static Orders create(String orderId, Integer orderAmount , Boolean isVerificated) {
        return Orders.builder()
                .orderId(orderId)
                .orderAmount(orderAmount)
                .isVerificated(isVerificated)
                .build();
    }
}
