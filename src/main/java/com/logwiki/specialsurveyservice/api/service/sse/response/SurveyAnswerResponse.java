package com.logwiki.specialsurveyservice.api.service.sse.response;
import com.logwiki.specialsurveyservice.domain.surveycategory.SurveyCategoryType;
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
    private int submitOrder;
    private SurveyCategoryType surveyCategoryType;
    @Builder
    public SurveyAnswerResponse(
            LocalDateTime answerTime , String name , String giveAwayName , Boolean isWin , int submitOrder , SurveyCategoryType surveyCategoryType) {
        this.answerTime = answerTime;
        this.name = name;
        this.giveAwayName = giveAwayName;
        this.isWin = isWin;
        this.submitOrder = submitOrder;
        this.surveyCategoryType = surveyCategoryType;
    }

    public static SurveyAnswerResponse from(SurveyResult surveyResult , String giveAwayName , boolean isWin ) {
        return SurveyAnswerResponse.builder()
                .answerTime(surveyResult.getAnswerDateTime())
                .name(surveyResult.getAccount().getName())
                .giveAwayName(giveAwayName)
                .isWin(isWin)
                .submitOrder(surveyResult.getSubmitOrder())
                .surveyCategoryType(surveyResult.getSurvey().getSurveyCategory().getType())
                .build();
    }


}