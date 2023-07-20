package com.logwiki.specialsurveyservice.domain.authority;

import static org.assertj.core.api.Assertions.*;

import com.logwiki.specialsurveyservice.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class AuthorityRepositoryTest extends IntegrationTestSupport {

    @Autowired
    AuthorityRepository authorityRepository;

    @DisplayName("권한 타입을 이용하여 권한을 조회한다.")
    @Test
    void findAuthorityByType() {
        // given
        AuthorityType authorityType = AuthorityType.ROLE_USER;
        Authority authority = Authority.builder()
                .type(authorityType)
                .build();

        authorityRepository.save(authority);

        // when
        Authority findAuthority = authorityRepository.findAuthorityByType(authorityType);

        // then
        assertThat(findAuthority).isNotNull()
                .isEqualTo(authority);
    }
}