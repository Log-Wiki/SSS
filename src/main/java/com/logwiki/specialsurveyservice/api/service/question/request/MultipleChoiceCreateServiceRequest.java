package com.logwiki.specialsurveyservice.api.service.question.request;

import com.logwiki.specialsurveyservice.domain.multiplechoice.MultipleChoice;
import com.logwiki.specialsurveyservice.domain.question.Question;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MultipleChoiceCreateServiceRequest {

    private String content;

    private Long linkNumber;

    @Builder
    public MultipleChoiceCreateServiceRequest(String content, Long linkNumber) {
        this.content = content;
        this.linkNumber = linkNumber;
    }

    public MultipleChoice toEntity(Question question) {
        MultipleChoice multipleChoice = MultipleChoice.builder()
                .content(content)
                .linkNumber(linkNumber)
                .build();
        multipleChoice.addQuestion(question);
        return multipleChoice;
    }

}
