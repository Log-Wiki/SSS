package com.logwiki.specialsurveyservice.api.service.question.request;

import com.logwiki.specialsurveyservice.domain.questioncategory.QuestionCategoryType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class QuestionModifyServiceRequest {

    private Long id;

    private String content;

    private String imgAddress;

    private QuestionCategoryType type;

    @Builder
    public QuestionModifyServiceRequest(Long id, String content, String imgAddress,
            QuestionCategoryType type) {
        this.id = id;
        this.content = content;
        this.imgAddress = imgAddress;
        this.type = type;
    }

}
