package com.logwiki.specialsurveyservice.domain.accountcode;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountCodeRepository extends JpaRepository<AccountCode, Long> {

    Optional<AccountCode> findAccountCodeByType(AccountCodeType accountCodeType);
}
