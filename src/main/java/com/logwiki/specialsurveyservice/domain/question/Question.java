package com.logwiki.specialsurveyservice.domain.question;

import com.logwiki.specialsurveyservice.api.service.question.request.QuestionModifyServiceRequest;
import com.logwiki.specialsurveyservice.domain.multiplechoice.MultipleChoice;
import com.logwiki.specialsurveyservice.domain.questioncategory.QuestionCategoryType;
import com.logwiki.specialsurveyservice.domain.survey.Survey;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long questionNumber;

    private String title;

    private String content;

    private String imgAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id")
    private Survey survey;

    private QuestionCategoryType type;

    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<MultipleChoice> multipleChoice;

    @Builder
    public Question(String title, Long questionNumber, String content, String imgAddress, Survey survey,
            QuestionCategoryType type, List<MultipleChoice> multipleChoice) {
        this.questionNumber = questionNumber;
        this.title = title;
        this.content = content;
        this.imgAddress = imgAddress;
        this.survey = survey;
        this.type = type;
        this.multipleChoice = multipleChoice;
    }

    public void updateQuestion(QuestionModifyServiceRequest questionModifyServiceRequest) {
        this.content = questionModifyServiceRequest.getContent();
        this.imgAddress = questionModifyServiceRequest.getImgAddress();
        this.type = questionModifyServiceRequest.getType();
    }

    public void addMultipleChoices(List<MultipleChoice> multipleChoices) {
        this.multipleChoice = multipleChoices;
    }

}
