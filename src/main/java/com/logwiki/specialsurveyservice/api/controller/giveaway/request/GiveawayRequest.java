package com.logwiki.specialsurveyservice.api.controller.giveaway.request;

import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GiveawayRequest {

    @NotNull(message = "상품 타입은 필수입니다.")
    private GiveawayType giveawayType;

    @NotEmpty(message = "상품 이름은 필수입니다.")
    private String name;

    @Positive(message = "상품 가격은 필수(양수)입니다.")
    private int price;

    @Builder
    private GiveawayRequest(GiveawayType giveawayType, String name, int price) {
        this.giveawayType = giveawayType;
        this.name = name;
        this.price = price;
    }
}
