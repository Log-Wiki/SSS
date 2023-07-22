package com.logwiki.specialsurveyservice.domain.question;

import com.logwiki.specialsurveyservice.domain.questioncategory.QuestionCategory;
import com.logwiki.specialsurveyservice.domain.survey.Survey;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long questionNumber;

    private String content;

    private String imgAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    private Survey survey;

    @ManyToOne(fetch = FetchType.LAZY)
    private QuestionCategory questionCategory;

    @Builder
    public Question(Long questionNumber, String content, String imgAddress, Survey survey, QuestionCategory questionCategory) {
        this.questionNumber = questionNumber;
        this.content = content;
        this.imgAddress = imgAddress;
        this.survey = survey;
        this.questionCategory = questionCategory;
    }
}
