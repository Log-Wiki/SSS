package com.logwiki.specialsurveyservice.domain.targetnumber;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TargetNumberRepository extends JpaRepository<TargetNumber, Long> {

    TargetNumber findTargetNumberByNumberAndSurvey_Id(int targetNumber, Long surveyId);
}
