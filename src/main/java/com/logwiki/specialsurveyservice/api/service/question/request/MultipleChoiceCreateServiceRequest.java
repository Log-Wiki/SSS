package com.logwiki.specialsurveyservice.api.service.question.request;

import com.logwiki.specialsurveyservice.domain.multiplechoice.MultipleChoice;
import lombok.Builder;

public class MultipleChoiceCreateServiceRequest {
    
    private String content;

    private Long linkNumber;

    @Builder
    public MultipleChoiceCreateServiceRequest(String content, Long linkNumber) {
        this.content = content;
        this.linkNumber = linkNumber;
    }

    public MultipleChoice toEntity() {
        return MultipleChoice.builder()
                .content(content)
                .linkNumber(linkNumber)
                .build();
    }

}
