package com.logwiki.specialsurveyservice.domain.accountsurvey;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountSurveyRepository extends JpaRepository<AccountSurvey, Long> {

    List<AccountSurvey> findAllByAccountId(Long accountId);
}
