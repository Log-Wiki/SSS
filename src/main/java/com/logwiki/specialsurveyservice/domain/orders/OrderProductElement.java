package com.logwiki.specialsurveyservice.domain.orders;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Data
public class OrderProductElement {
    @NotNull(message = "경품 이름은 필수입니다.")
    private String giveawayName;
    @NotNull(message = "경품 숫자는 필수입니다.")
    private Integer giveawayNumber;
}
