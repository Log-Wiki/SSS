package com.logwiki.specialsurveyservice.domain.orders;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@Data
public class OrderProductElement {
    @NotNull
    private String giveawayName;
    @NotNull
    private Integer giveawayNumber;
}
