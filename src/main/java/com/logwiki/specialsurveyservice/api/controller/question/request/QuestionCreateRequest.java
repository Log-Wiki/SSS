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

    @NotNull(message = "문제번호는 필수입니다.")
    private Long questionNumber;

    @NotEmpty(message = "질문내용은 필수입니다.")
    private String content;

    private String imgAddress;

    @NotNull(message = "문항 카테고리 타입은 필수입니다.")
    private QuestionCategoryType type;

    @Valid
    private List<MultipleChoiceCreateRequest> multipleChoices;

    @Builder
    public QuestionCreateRequest(Long questionNumber, String content, String imgAddress,
                                 QuestionCategoryType type, List<MultipleChoiceCreateRequest> multipleChoices) {
        this.questionNumber = questionNumber;
        this.content = content;
        this.imgAddress = imgAddress;
        this.type = type;
        this.multipleChoices = multipleChoices;
    }


    public QuestionCreateServiceRequest toServiceRequest() {
        if (type == QuestionCategoryType.SHORT_FORM && multipleChoices != null) {
            throw new BaseException("주관식은 보기를 가질수 없습니다.", 3008);
        }
        if (type == QuestionCategoryType.MULTIPLE_CHOICE && multipleChoices == null) {
            throw new BaseException("객관식은 보기를 가져야합니다.", 3009);
        }
        if (multipleChoices != null) {
            return QuestionCreateServiceRequest.builder()
                    .questionNumber(questionNumber)
                    .content(content)
                    .imgAddress(imgAddress)
                    .type(type)
                    .multipleChoices(multipleChoices.stream()
                            .map(MultipleChoiceCreateRequest::toServiceRequest)
                            .collect(Collectors.toList()))
                    .build();
        }
        return QuestionCreateServiceRequest.builder()
                .questionNumber(questionNumber)
                .content(content)
                .imgAddress(imgAddress)
                .type(type)
                .build();
    }

}

