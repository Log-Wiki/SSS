package com.logwiki.specialsurveyservice.domain.surveygiveaway;

import com.logwiki.specialsurveyservice.domain.giveaway.Giveaway;
import com.logwiki.specialsurveyservice.domain.survey.Survey;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@lombok.Generated
public class SurveyGiveaway {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int count;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id")
    private Survey survey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "giveaway_id")
    private Giveaway giveaway;

    @Builder
    public SurveyGiveaway(int count, Survey survey, Giveaway giveaway) {
        this.count = count;
        this.survey = survey;
        this.giveaway = giveaway;
    }

    public static SurveyGiveaway create(int count, Survey survey, Giveaway giveaway) {
        return SurveyGiveaway.builder()
                .count(count)
                .survey(survey)
                .giveaway(giveaway)
                .build();
    }
}
