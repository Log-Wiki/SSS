package com.logwiki.specialsurveyservice.domain.account;

import com.logwiki.specialsurveyservice.domain.survey.Survey;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @EntityGraph(attributePaths = "authorities")
    Optional<Account> findOneWithAuthoritiesByEmail(String Email);

    @EntityGraph(attributePaths = "authorities")
    Optional<Account> findOneWithAuthoritiesByPhoneNumber(String phoneNumber);

    @Query(value = "SELECT * FROM account acc "
            + "WHERE (SELECT ac.ID FROM ACCOUNT_CODE ac WHERE ac.TYPE = acc.gender) IN (SELECT st.ACCOUNT_CODE_ID "
            + "FROM SURVEY_TARGET st "
            + "WHERE st.SURVEY_ID = :surveyId)"
            + "AND (SELECT ac.ID FROM ACCOUNT_CODE ac WHERE ac.TYPE = acc.age) IN (SELECT st.ACCOUNT_CODE_ID "
            + "FROM SURVEY_TARGET st "
            + "WHERE st.SURVEY_ID = :surveyId)"
           ,
            nativeQuery = true)
    List<Account> findRecommendAccounts(@Param("surveyId") Long surveyId);

}
