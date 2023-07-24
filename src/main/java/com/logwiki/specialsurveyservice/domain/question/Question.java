package com.logwiki.specialsurveyservice.domain.question;

import com.logwiki.specialsurveyservice.api.service.question.request.QuestionModifyServiceRequest;
import com.logwiki.specialsurveyservice.domain.multiplechoice.MultipleChoice;
import com.logwiki.specialsurveyservice.domain.questioncategory.QuestionCategoryType;
import com.logwiki.specialsurveyservice.domain.survey.Survey;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.List;
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

    private QuestionCategoryType type;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<MultipleChoice> multipleChoice;

    @Builder
    public Question(Long questionNumber, String content, String imgAddress, Survey survey,
            QuestionCategoryType type, List<MultipleChoice> multipleChoice) {
        this.questionNumber = questionNumber;
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

}
