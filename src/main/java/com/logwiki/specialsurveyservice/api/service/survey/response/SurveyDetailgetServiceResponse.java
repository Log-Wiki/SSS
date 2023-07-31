package com.logwiki.specialsurveyservice.api.service.survey.response;

import com.logwiki.specialsurveyservice.api.controller.sse.response.SurveyAnswerResponse;
import com.logwiki.specialsurveyservice.domain.giveaway.Giveaway;
import com.logwiki.specialsurveyservice.domain.surveycategory.SurveyCategoryType;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SurveyDetailgetServiceResponse {

    @NotNull(message = "설문 상세정보의 제목은 필수입니다.")
    private final String title;
    @NotNull(message = "설문 상세정보의 시작시간은 필수입니다.")
    private final LocalDateTime startTime;
    @NotNull(message = "설문 상세정보의 끝나는시간은 필수입니다.")
    private final LocalDateTime endTime;
    @NotNull(message = "설문 상세정보의 현재 진행인원 필수입니다.")
    private final int headCount;
    @NotNull(message = "설문 상세정보의 마감인원 필수입니다.")
    private final int closedHeadCount;
    @NotNull(message = "설문 상세정보의 작성자ID은 필수입니다.")
    private final Long writer;
    @NotNull(message = "설문 상세정보의 작성자이름은 필수입니다.")
    private final String writerName;
    @NotNull(message = "설문 상세정보의 설문카테고리타입은 필수입니다.")
    private final SurveyCategoryType surveyCategoryType;
    @NotNull(message = "설문 상세정보의 상품리스트는 필수입니다.")
    private final List<String> giveawayNames;
    @NotNull(message = "설문 상세정보의 당청확률 필수입니다.")
    private final double winRate;
    @NotNull(message = "설문 상세정보의 질문 문항수 필수입니다.")
    private final int questionCount;
    @NotNull(message = "설문 상세정보의 예상시간 필수입니다.")
    private final double estimateTime;
    @NotNull(message = "설문 상세정보의 응답결과내역은 필수입니다.")
    private final List<SurveyAnswerResponse> surveyResponseResults;

    @Builder
    public SurveyDetailgetServiceResponse(String title, LocalDateTime startTime , LocalDateTime endTime,
            int headCount , int closedHeadCount , Long writer, String writerName , SurveyCategoryType surveyCategoryType,
            List<String> giveawayNames , double winRate , int questionCount , double estimateTime , List<SurveyAnswerResponse>
            surveyResponseResults) {
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.headCount = headCount;
        this.closedHeadCount = closedHeadCount;
        this.writer = writer;
        this.writerName = writerName;
        this.surveyCategoryType = surveyCategoryType;
        this.giveawayNames = giveawayNames;
        this.winRate = winRate;
        this.questionCount = questionCount;
        this.estimateTime = estimateTime;
        this.surveyResponseResults = surveyResponseResults;
    }

}
