package com.logwiki.specialsurveyservice.api.controller.survey.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.logwiki.specialsurveyservice.api.controller.question.request.QuestionCreateRequest;
import com.logwiki.specialsurveyservice.api.service.survey.request.SurveyCreateServiceRequest;
import com.logwiki.specialsurveyservice.domain.surveycategory.SurveyCategoryType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class SurveyCreateRequest {

    @NotEmpty(message = "질문 제목이 필요합니다.")
    private String title;

    @NotNull(message = "시작 시간은 필수입니다.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startTime;

    @NotNull(message = "마감 시간은 필수입니다.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endTime;

    @NotNull(message = "인원수는 필수입니다.")
    private int headCount;

    @NotNull(message = "마감 인원수는 필수입니다.")
    private int closedHeadCount;

    @NotNull(message = "설문 타입은 필수입니다.")
    private SurveyCategoryType type;

    @NotNull(message = "설문에 문항들은 필수입니다.")
    @Valid
    private List<QuestionCreateRequest> questions;

    // 상품코드 , 개수 필요할듯
    public SurveyCreateServiceRequest from() {
        return SurveyCreateServiceRequest.builder()
                .title(title)
                .startTime(startTime)
                .endTime(endTime)
                .headCount(headCount)
                .closedHeadCount(closedHeadCount)
                .type(type)
                .questions(
                        questions.stream().map(QuestionCreateRequest::toServiceRequest)
                                .collect(Collectors.toList())
                )
                .build();
    }
}