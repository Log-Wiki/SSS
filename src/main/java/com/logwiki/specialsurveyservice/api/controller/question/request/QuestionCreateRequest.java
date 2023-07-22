package com.logwiki.specialsurveyservice.api.controller.question.request;

import com.logwiki.specialsurveyservice.api.service.question.request.QuestionCreateServiceRequest;
import com.logwiki.specialsurveyservice.domain.questioncategory.QuestionCategoryType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

}

