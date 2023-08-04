package com.logwiki.specialsurveyservice.api.service.sse.response;
import com.logwiki.specialsurveyservice.domain.surveyresult.SurveyResult;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SurveyAnswerResponse {
    private LocalDateTime answerTime;
    private String name;
    private String giveAwayName;
    private Boolean isWin;

    @Builder
    public SurveyAnswerResponse(
            LocalDateTime answerTime , String name , String giveAwayName , Boolean isWin) {
        this.answerTime = answerTime;
        this.name = name;
        this.giveAwayName = giveAwayName;
        this.isWin = isWin;
    }

    public static SurveyAnswerResponse from(SurveyResult surveyResult , String giveAwayName , boolean isWin ) {
        return SurveyAnswerResponse.builder()
                .answerTime(surveyResult.getAnswerDateTime())
                .name(surveyResult.getAccount().getName())
                .giveAwayName(giveAwayName)
                .isWin(isWin)
                .build();
    }


}