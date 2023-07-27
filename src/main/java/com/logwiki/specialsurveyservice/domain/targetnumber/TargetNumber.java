package com.logwiki.specialsurveyservice.domain.targetnumber;

import com.logwiki.specialsurveyservice.domain.BaseEntity;
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
public class TargetNumber extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int number;

    @ManyToOne(fetch = FetchType.LAZY)
    private Survey survey;


    @ManyToOne(fetch = FetchType.LAZY)
    private Giveaway giveaway;

    @Builder
    private TargetNumber(int number, Survey survey, Giveaway giveaway) {
        this.number = number;
        this.survey = survey;
        this.giveaway = giveaway;
    }

    public static TargetNumber create(int number, Survey survey, Giveaway giveaway) {
        return TargetNumber.builder()
                .number(number)
                .survey(survey)
                .giveaway(giveaway)
                .build();
    }
}
