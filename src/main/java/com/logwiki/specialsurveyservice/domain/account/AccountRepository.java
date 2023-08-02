package com.logwiki.specialsurveyservice.domain.account;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @EntityGraph(attributePaths = "authorities")
    Optional<Account> findOneWithAuthoritiesByEmail(String Email);

    @EntityGraph(attributePaths = "authorities")
    Optional<Account> findOneWithAuthoritiesByPhoneNumber(String phoneNumber);
}
