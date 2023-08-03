package com.logwiki.specialsurveyservice.domain.targetnumber;

서import com.logwiki.specialsurveyservice.domain.survey.Survey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TargetNumberRepository extends JpaRepository<TargetNumber, Long> {

    TargetNumber findTargetNumberByNumberAndSurvey_Id(int targetNumber, Long surveyId);

    Optional<TargetNumber> findFirstBySurveyAndNumber(Survey survey, int number);
}
