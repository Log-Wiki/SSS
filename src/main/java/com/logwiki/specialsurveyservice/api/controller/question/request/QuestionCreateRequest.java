package com.logwiki.specialsurveyservice.api.controller.question.request;

import com.logwiki.specialsurveyservice.api.service.question.request.QuestionCreateServiceRequest;
import com.logwiki.specialsurveyservice.domain.questioncategory.QuestionCategoryType;
import com.logwiki.specialsurveyservice.exception.BaseException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class QuestionCreateRequest {

    @NotNull(message = "문제 번호는 필수입니다.")
    private Long questionNumber;

    @NotEmpty(message = "질문 제목은 필수입니다.")
    private String title;

    private String content;

    private String imgAddress;

    @NotNull(message = "문항 카테고리 타입은 필수입니다.")
    private QuestionCategoryType type;

    @Valid
    private List<MultipleChoiceCreateRequest> multipleChoices;

    @NotNull(message = "필수 답변 여부는 필수입니다.")
    private Boolean essential;

    @Builder
    public QuestionCreateRequest(String title, Long questionNumber, String content, String imgAddress,
            QuestionCategoryType type, List<MultipleChoiceCreateRequest> multipleChoices, boolean essential) {
        this.questionNumber = questionNumber;
        this.content = content;
        this.imgAddress = imgAddress;
        this.type = type;
        this.multipleChoices = multipleChoices;
        this.title = title;
        this.essential = essential;
    }


    public QuestionCreateServiceRequest toServiceRequest() {
        if (type == QuestionCategoryType.SHORT_FORM && multipleChoices != null) {
            throw new BaseException("주관식은 보기를 가질수 없습니다.", 3008);
        }
        if ((type == QuestionCategoryType.MULTIPLE_CHOICE ||
                type == QuestionCategoryType.DROP_DOWN ||
                type == QuestionCategoryType.CHECK_BOX) &&
                multipleChoices == null) {
            throw new BaseException("객관식은 보기를 가져야합니다.", 3009);
        }
        if (multipleChoices != null) {
            return QuestionCreateServiceRequest.builder()
                    .questionNumber(questionNumber)
                    .title(title)
                    .content(content)
                    .imgAddress(imgAddress)
                    .essential(essential)
                    .type(type)
                    .multipleChoices(multipleChoices.stream()
                            .map(MultipleChoiceCreateRequest::toServiceRequest)
                            .collect(Collectors.toList()))
                    .build();
        }
        return QuestionCreateServiceRequest.builder()
                .questionNumber(questionNumber)
                .title(title)
                .content(content)
                .imgAddress(imgAddress)
                .essential(essential)
                .type(type)
                .build();
    }

}

