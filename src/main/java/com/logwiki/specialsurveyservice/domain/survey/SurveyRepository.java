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
            + "AND sur.closed = false "
            + "ORDER BY sur.END_TIME",
            nativeQuery = true)
    List<Survey> findRecommendNormalSurvey(@Param("surveyCategoryType") String surveyCategoryType, @Param("genderId") Long genderId, @Param("ageId") Long ageId);

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
    List<Survey> findRecommendInstantSurvey(@Param("surveyCategoryType") String surveyCategoryType, @Param("genderId") Long genderId, @Param("ageId") Long ageId);
}
