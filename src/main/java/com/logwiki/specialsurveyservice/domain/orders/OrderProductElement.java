package com.logwiki.specialsurveyservice.domain.orders;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderProductElement {
    @NotNull(message = "경품 이름은 필수입니다.")
    private String giveawayName;
    @NotNull(message = "경품 숫자는 필수입니다.")
    private int giveawayNumber;

    @Builder
    public OrderProductElement(String giveawayName, int giveawayNumber) {
        this.giveawayName = giveawayName;
        this.giveawayNumber = giveawayNumber;
    }
}
