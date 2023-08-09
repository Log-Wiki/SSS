package com.logwiki.specialsurveyservice.domain.survey;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Long> {

    @Query(value = "SELECT * FROM survey sur "
            + "WHERE :surveyCategoryType IN (SELECT sc.TYPE "
                                + "FROM SURVEY_CATEGORY sc "
                                + "WHERE sur.SURVEY_CATEGORY_ID = sc.ID)"
            + "AND :genderId IN (SELECT st.ACCOUNT_CODE_ID "
                                + "FROM SURVEY_TARGET st "
                                + "WHERE st.SURVEY_ID = sur.ID) "
            + "AND :ageId IN (SELECT st.ACCOUNT_CODE_ID "
                                + "FROM SURVEY_TARGET st "
                                + "WHERE st.SURVEY_ID = sur.ID) "
            + "AND sur.closed = false ",
            nativeQuery = true)
    List<Survey> findRecommendSurvey(@Param("surveyCategoryType") String surveyCategoryType, @Param("genderId") Long genderId, @Param("ageId") Long ageId);

    @Query(value = "SELECT * FROM survey sur "
            + "WHERE :genderId IN (SELECT st.ACCOUNT_CODE_ID "
                                + "FROM SURVEY_TARGET st "
                                + "WHERE st.SURVEY_ID = sur.ID) "
            + "AND :ageId IN (SELECT st.ACCOUNT_CODE_ID "
                                + "FROM SURVEY_TARGET st "
                                + "WHERE st.SURVEY_ID = sur.ID) "
            + "AND sur.closed = false ",
            nativeQuery = true)
    List<Survey> findRecommendSurvey(@Param("genderId") Long genderId, @Param("ageId") Long ageId);

    @Query(value = "SELECT * FROM survey sur "
            + "WHERE :surveyCategoryType IN (SELECT sc.TYPE "
                                + "FROM SURVEY_CATEGORY sc "
                                + "WHERE sur.SURVEY_CATEGORY_ID = sc.ID)"
            + "AND sur.closed = false ",
            nativeQuery = true)
    List<Survey> findRecommendSurveyForAnonymous(@Param("surveyCategoryType") String surveyCategoryType);

    @Query(value = "SELECT * FROM survey sur "
            + "WHERE sur.closed = false ",
            nativeQuery = true)
    List<Survey> findRecommendSurveyForAnonymous();

    @Query(value = "SELECT COUNT(*) FROM survey sur "
            + "WHERE sur.ID = :surveyId "
            + "AND :genderId IN (SELECT st.ACCOUNT_CODE_ID "
                                    + "FROM SURVEY_TARGET st "
                                    + "WHERE st.SURVEY_ID = sur.ID) "
            + "AND :ageId IN (SELECT st.ACCOUNT_CODE_ID "
                                    + "FROM SURVEY_TARGET st "
                                    + "WHERE st.SURVEY_ID = sur.ID) ",
            nativeQuery = true)
    int checkSurveyPossible(@Param("surveyId") Long surveyId, @Param("genderId") Long genderId, @Param("ageId") Long ageId);

    List<Survey> findAllByWriter(Long writerId);
}
