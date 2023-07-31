package com.logwiki.specialsurveyservice.api.controller.sse.response;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SurveyResponseResult {
    @NotNull(message = "응답 시간은 필수입니다.")
    private LocalDateTime answerTime;
    @NotNull(message = "응답자 이름은 필수입니다.")
    private String name;
    @NotNull(message = "응답 상품은 필수입니다.")
    private String giveAwayName;
    @NotNull(message = "상품 당청 여부는 필수입니다.")
    private Boolean isWin;

    @Builder
    public SurveyResponseResult(
            LocalDateTime answerTime , String name , String giveAwayName , Boolean isWin) {
        this.answerTime = answerTime;
        this.name = name;
        this.giveAwayName = giveAwayName;
        this.isWin = isWin;
    }


}
