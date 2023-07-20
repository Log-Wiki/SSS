package com.logwiki.specialsurveyservice.domain.authority;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    Authority findAuthorityByType(AuthorityType authorityType);
}
