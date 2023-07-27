package com.logwiki.specialsurveyservice.api.service.auth;

import static org.assertj.core.api.Assertions.*;

import com.logwiki.specialsurveyservice.IntegrationTestSupport;
import com.logwiki.specialsurveyservice.domain.account.Account;
import com.logwiki.specialsurveyservice.domain.account.AccountRepository;
import com.logwiki.specialsurveyservice.domain.accountcode.AccountCodeType;
import com.logwiki.specialsurveyservice.domain.authority.Authority;
import com.logwiki.specialsurveyservice.domain.authority.AuthorityRepository;
import com.logwiki.specialsurveyservice.domain.authority.AuthorityType;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class AuthServiceTest extends IntegrationTestSupport {

    @Autowired
    private AuthService authService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AuthorityRepository authorityRepository;

    @DisplayName("이메일을 이용하여 해당 이메일을 사용하는 회원의 refresh token을 저장한다.")
    @Test
    void saveRefreshToken() {
        // given
        Authority userAuthority = Authority.builder()
                .type(AuthorityType.ROLE_USER)
                .build();

        Authority authority = authorityRepository.save(userAuthority);
        List<Authority> authorities = List.of(authority);

        String email = "duswo0624@naver.com";
        Account account = Account.create(
                email,
                "1234",
                AccountCodeType.MAN,
                AccountCodeType.TWENTIES,
                "최연재",
                "010-1234-5678",
                LocalDate.of(1997, 6, 24),
                authorities
        );

        accountRepository.save(account);

        String refreshToken = "temp-refresh-token";

        // when
        authService.saveRefreshToken(email, refreshToken);

        // then
        String findRefreshToken = accountRepository.findOneWithAuthoritiesByEmail(email).get().getRefreshToken();
        assertThat(findRefreshToken).isNotNull();
        assertThat(findRefreshToken).isEqualTo(refreshToken);
    }
}