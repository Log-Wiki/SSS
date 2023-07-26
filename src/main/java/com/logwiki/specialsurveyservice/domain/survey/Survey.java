package com.logwiki.specialsurveyservice.domain.survey;

import com.logwiki.specialsurveyservice.domain.BaseEntity;
import com.logwiki.specialsurveyservice.domain.question.Question;
import com.logwiki.specialsurveyservice.domain.surveycategory.SurveyCategory;
import com.logwiki.specialsurveyservice.domain.surveygiveaway.SurveyGiveaway;
import com.logwiki.specialsurveyservice.domain.surveyresult.SurveyResult;
import com.logwiki.specialsurveyservice.domain.targetnumber.TargetNumber;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Survey extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private int headCount;

    private int closedHeadCount;

    private Long writer;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private SurveyCategory surveyCategory;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Question> questions;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<SurveyGiveaway> surveyGiveaways;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<TargetNumber> targetNumbers;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<SurveyResult> surveyResults;

    @Builder
    public Survey(String title, LocalDateTime startTime, LocalDateTime endTime, int headCount,
                  int closedHeadCount,
                  Long writer, SurveyCategory type, List<Question> questions) {
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.headCount = headCount;
        this.closedHeadCount = closedHeadCount;
        this.surveyCategory = type;
        this.writer = writer;
        this.questions = questions;
    }

    public void addCategory(SurveyCategory surveyCategory) {
        this.surveyCategory = surveyCategory;
    }

    public void addSurveyGiveaways(List<SurveyGiveaway> surveyGiveaways) {
        this.surveyGiveaways = surveyGiveaways;
    }

    public void addTargetNumbers(List<TargetNumber> targetNumbers) {
        this.targetNumbers = targetNumbers;
    }

    public void addQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public void addSurveyResults(List<SurveyResult> surveyResults) {
        this.surveyResults = surveyResults;
    }
}
