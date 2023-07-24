package com.logwiki.specialsurveyservice.api.controller.question.request;

import com.logwiki.specialsurveyservice.api.service.question.request.QuestionModifyServiceRequest;
import com.logwiki.specialsurveyservice.domain.questioncategory.QuestionCategoryType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class QuestionModifyRequest {

    @NotEmpty(message = "질문내용은 필수입니다.")
    private String content;

    @NotEmpty(message = "이미지 주소는 필수입니다.")
    private String imgAddress;

    @NotNull(message = "문항 카테고리 타입은 필수입니다.")
    private QuestionCategoryType type;

    @Builder
    public QuestionModifyRequest(String content, String imgAddress,
            QuestionCategoryType type) {
        this.content = content;
        this.imgAddress = imgAddress;
        this.type = type;
    }

    public QuestionModifyServiceRequest toServiceRequest(Long id) {
        return QuestionModifyServiceRequest.builder()
                .id(id)
                .content(content)
                .imgAddress(imgAddress)
                .type(type)
                .build();
    }
}
