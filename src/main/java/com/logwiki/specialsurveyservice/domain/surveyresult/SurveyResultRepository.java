package com.logwiki.specialsurveyservice.domain.surveyresult;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SurveyResultRepository extends JpaRepository<SurveyResult, Long> {

    @Query(value = "select COUNT(*) from survey_result sr where sr.survey_id = :surveyId", nativeQuery = true)
    int findSubmitCountBy(@Param("surveyId") Long surveyId);

    SurveyResult findSurveyResultByAccount_Id(Long accountId);
}
