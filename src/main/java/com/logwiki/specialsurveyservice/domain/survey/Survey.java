package com.logwiki.specialsurveyservice.domain.survey;

import com.logwiki.specialsurveyservice.domain.BaseEntity;
import com.logwiki.specialsurveyservice.domain.question.Question;
import com.logwiki.specialsurveyservice.domain.surveycategory.SurveyCategory;
import com.logwiki.specialsurveyservice.domain.surveygiveaway.SurveyGiveaway;
import com.logwiki.specialsurveyservice.domain.surveyresult.SurveyResult;
import com.logwiki.specialsurveyservice.domain.surveytarget.SurveyTarget;
import com.logwiki.specialsurveyservice.domain.targetnumber.TargetNumber;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    private int totalGiveawayCount;

    private int requiredTimeInSeconds;

    private boolean closed;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "survey_category_id")
    private SurveyCategory surveyCategory;

    @OneToMany(mappedBy = "survey", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Question> questions;

    @OneToMany(mappedBy = "survey", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<SurveyGiveaway> surveyGiveaways;

    @OneToMany(mappedBy = "survey", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<TargetNumber> targetNumbers;

    @OneToMany(mappedBy = "survey", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<SurveyResult> surveyResults;


    @OneToMany(mappedBy = "survey", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<SurveyTarget> surveyTargets;

    @Builder
    public Survey(String title, LocalDateTime startTime, LocalDateTime endTime, int headCount,
            int closedHeadCount, List<SurveyTarget> surveyTargets,
            Long writer, int totalGiveawayCount, int requiredTimeInSeconds, SurveyCategory type, List<Question> questions) {
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.headCount = headCount;
        this.closedHeadCount = closedHeadCount;
        this.surveyCategory = type;
        this.writer = writer;
        this.totalGiveawayCount = totalGiveawayCount;
        this.requiredTimeInSeconds = requiredTimeInSeconds;
        this.closed = false;
        this.questions = questions;
        this.surveyTargets = surveyTargets;
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
  
    public void addSurveyTarget(SurveyTarget surveyTarget) {
        if (this.surveyTargets == null) this.surveyTargets = new ArrayList<>();
        this.surveyTargets.add(surveyTarget);
    }
  
    public void addHeadCount() {
        this.headCount += 1;
        if(this.headCount == closedHeadCount)
            closed = true;
    }

    public void addSurveyCategory(SurveyCategory surveyCategory) {
        this.surveyCategory = surveyCategory;
    }

    public void toOpen() {
        this.closed = false;
    }

    public void toClose() {
        this.closed = true;
    }
}
