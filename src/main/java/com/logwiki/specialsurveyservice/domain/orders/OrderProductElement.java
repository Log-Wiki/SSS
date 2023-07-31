package com.logwiki.specialsurveyservice.domain.orders;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Data
public class OrderProductElement {
    @NotNull
    private String giveawayName;
    @NotNull
    private Integer giveawayNumber;
}
