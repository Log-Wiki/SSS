package com.logwiki.specialsurveyservice.domain.targetnumber;

import com.logwiki.specialsurveyservice.domain.survey.Survey;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TargetNumberRepository extends JpaRepository<TargetNumber, Long> {
    Optional<TargetNumber> findFirstBySurveyAndNumber(Survey survey, int number);

    TargetNumber findTargetNumberByNumberAndSurvey_Id(int targetNumber, Long surveyId);

}
