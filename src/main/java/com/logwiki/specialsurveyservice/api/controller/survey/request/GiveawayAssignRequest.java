package com.logwiki.specialsurveyservice.api.controller.survey.request;

import com.logwiki.specialsurveyservice.api.service.survey.request.GiveawayAssignServiceRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GiveawayAssignRequest {

    @NotNull(message = "당첨 상품 PK는 필수입니다.")
    private Long id;

    @Positive(message = "당첨 상품 수량은 필수(양수)입니다.")
    private int count;

    public GiveawayAssignServiceRequest toServiceRequest() {
        return GiveawayAssignServiceRequest.builder()
                .id(id)
                .count(count)
                .build();
    }
}
