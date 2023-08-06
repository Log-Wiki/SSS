package com.logwiki.specialsurveyservice.api.service.question.response;

import com.logwiki.specialsurveyservice.domain.question.Question;
import com.logwiki.specialsurveyservice.domain.questioncategory.QuestionCategoryType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class QuestionResponse {

    private Long id;
    private Long questionNumber;
    private String title;
    private String content;
    private String imgAddress;
    private QuestionCategoryType type;
    private List<MultipleChoiceResponse> multipleChoices;

    @Builder
    public QuestionResponse(String title, Long id, Long questionNumber, String content,
            String imgAddress, QuestionCategoryType type, List<MultipleChoiceResponse> multipleChoices) {
        this.title = title;
        this.id = id;
        this.questionNumber = questionNumber;
        this.content = content;
        this.imgAddress = imgAddress;
        this.type = type;
        this.multipleChoices = multipleChoices;
    }

    public static QuestionResponse from(Question question) {
        if (question == null) {
            return null;
        }
        if (question.getMultipleChoice() == null) {
            return QuestionResponse.builder()
                    .id(question.getId())
                    .title(question.getTitle())
                    .questionNumber(question.getQuestionNumber())
                    .content(question.getContent())
                    .imgAddress(question.getImgAddress())
                    .type(question.getType())
                    .build();
        }
        return QuestionResponse.builder()
                .id(question.getId())
                .questionNumber(question.getQuestionNumber())
                .title(question.getTitle())
                .content(question.getContent())
                .imgAddress(question.getImgAddress())
                .type(question.getType())
                .multipleChoices(question.getMultipleChoice()
                        .stream().map(MultipleChoiceResponse::from)
                        .collect(Collectors.toList()))
                .build();
    }
}
