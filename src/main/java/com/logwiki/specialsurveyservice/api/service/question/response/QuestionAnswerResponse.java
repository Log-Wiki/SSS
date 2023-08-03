package com.logwiki.specialsurveyservice.api.service.question.response;

import com.logwiki.specialsurveyservice.domain.questionanswer.QuestionAnswer;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class QuestionAnswerResponse {

    private Long id;
    private Long multipleChoiceAnswer;
    private String shortFormAnswer;
    private Long writer;
    private LocalDateTime writeDate;

    @Builder
    public QuestionAnswerResponse(Long id, Long multipleChoiceAnswer, String shortFormAnswer,
            Long writer, LocalDateTime writeDate) {
        this.id = id;
        this.multipleChoiceAnswer = multipleChoiceAnswer;
        this.shortFormAnswer = shortFormAnswer;
        this.writer = writer;
        this.writeDate = writeDate;
    }

    public static QuestionAnswerResponse from(QuestionAnswer questionAnswer) {
        return QuestionAnswerResponse.builder()
                .id(questionAnswer.getId())
                .multipleChoiceAnswer(questionAnswer.getAnswerNumber())
                .shortFormAnswer(questionAnswer.getShortFormAnswer())
                .writer(questionAnswer.getAccount().getId())
                .writeDate(questionAnswer.getAnswerDateTime())
                .build();
    }


}
