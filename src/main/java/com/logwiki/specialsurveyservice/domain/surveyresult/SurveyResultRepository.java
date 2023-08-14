package com.logwiki.specialsurveyservice.domain.surveyresult;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SurveyResultRepository extends JpaRepository<SurveyResult, Long> {

    @Query(value = "select COUNT(*) from survey_result sr where sr.survey_id = :surveyId", nativeQuery = true)
    int findSubmitCountBy(@Param("surveyId") Long surveyId);

    SurveyResult findSurveyResultBySurvey_IdAndAccount_Id(Long surveyId, Long accountId);

    List<SurveyResult> findSurveyResultsByAccount_Id(Long accountId);

    @Query(value = "SELECT sg.COUNT "
            + "FROM TARGET_NUMBER tn "
            + "INNER JOIN GIVEAWAY g "
            + "ON tn.GIVEAWAY_ID = g.ID "
            + "INNER JOIN SURVEY_GIVEAWAY sg "
            + "ON g.id = sg.GIVEAWAY_ID \n"
            + "WHERE tn.NUMBER = :targetNumber "
            + "AND tn.SURVEY_ID = :surveyId "
            + "AND sg.SURVEY_ID = :surveyId ;",
            nativeQuery = true
    )
    Optional<Integer> findByGiveawaySurvey(@Param("surveyId") Long surveyId, @Param("targetNumber") int targetNumber);

    List<SurveyResult> findAllBySurvey_IdAndWin(Long surveyId, boolean win);
}
