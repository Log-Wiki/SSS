package com.logwiki.specialsurveyservice.domain.surveytarget;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveyTargetRepository extends JpaRepository<SurveyTarget, Long> {

    List<SurveyTarget> findSurveyTargetBySurvey_Id(Long surveyId);

}
