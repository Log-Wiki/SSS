package com.logwiki.specialsurveyservice.domain.targetnumber;

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
public class TargetNumber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long number;

    @ManyToOne(fetch = FetchType.LAZY)
    private Survey survey;


    @ManyToOne(fetch = FetchType.LAZY)
    private Giveaway giveaway;

    @Builder
    public TargetNumber(Long number, Survey survey, Giveaway giveaway) {
        this.number = number;
        this.survey = survey;
        this.giveaway = giveaway;
    }

}
