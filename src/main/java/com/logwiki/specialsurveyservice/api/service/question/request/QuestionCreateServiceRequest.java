package com.logwiki.specialsurveyservice.api.service.question.request;

import com.logwiki.specialsurveyservice.domain.question.Question;
import com.logwiki.specialsurveyservice.domain.questioncategory.QuestionCategoryType;
import com.logwiki.specialsurveyservice.domain.survey.Survey;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class QuestionCreateServiceRequest {

    private Long questionNumber;

    private String content;

    private String imgAddress;

    private QuestionCategoryType type;

    private List<MultipleChoiceCreateServiceRequest> multipleChoices;

    @Builder
    public QuestionCreateServiceRequest(Long questionNumber, String content,
            String imgAddress,
            QuestionCategoryType type, List<MultipleChoiceCreateServiceRequest> multipleChoices) {
        this.questionNumber = questionNumber;
        this.content = content;
        this.imgAddress = imgAddress;
        this.type = type;
        this.multipleChoices = multipleChoices;
    }

    public Question toEntity(Survey survey) {
        if (multipleChoices != null) {
            Question question = Question.builder()
                    .questionNumber(questionNumber)
                    .content(content)
                    .survey(survey)
                    .imgAddress(imgAddress)
                    .type(type)
                    .build();

            question.addMultipleChoices(multipleChoices.stream()
                    .map(multipleChoiceCreateServiceRequest -> {
                        return multipleChoiceCreateServiceRequest.toEntity(question);
                    }).collect(Collectors.toList()));

            return question;
        }
        return Question.builder()
                .questionNumber(questionNumber)
                .content(content)
                .imgAddress(imgAddress)
                .survey(survey)
                .type(type)
                .build();
    }

    public Question toEntity() {
        if (multipleChoices != null) {
            Question question = Question.builder()
                    .questionNumber(questionNumber)
                    .content(content)
                    .imgAddress(imgAddress)
                    .type(type)
                    .build();

            question.addMultipleChoices(multipleChoices.stream()
                    .map(multipleChoiceCreateServiceRequest -> {
                        return multipleChoiceCreateServiceRequest.toEntity(question);
                    }).collect(Collectors.toList()));

            return question;
        }
        return Question.builder()
                .questionNumber(questionNumber)
                .content(content)
                .imgAddress(imgAddress)
                .type(type)
                .build();
    }

}
