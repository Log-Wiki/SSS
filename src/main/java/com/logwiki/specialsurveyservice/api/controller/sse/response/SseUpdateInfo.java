package com.logwiki.specialsurveyservice.api.controller.sse.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SseUpdateInfo {
    private LocalDateTime answerTime;
    private String name;
    private String giveAwayName;
    private Boolean isWin;

    @Builder
    public SseUpdateInfo(
            LocalDateTime answerTime , String name , String giveAwayName , Boolean isWin) {
        this.answerTime = answerTime;
        this.name = name;
        this.giveAwayName = giveAwayName;
        this.isWin = isWin;
    }


}