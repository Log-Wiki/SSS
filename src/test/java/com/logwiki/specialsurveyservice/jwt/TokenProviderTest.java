package com.logwiki.specialsurveyservice.jwt;

import static org.assertj.core.api.Assertions.*;

import com.logwiki.specialsurveyservice.IntegrationTestSupport;
import com.logwiki.specialsurveyservice.api.service.account.AccountService;
import com.logwiki.specialsurveyservice.api.service.account.request.AccountCreateServiceRequest;
import com.logwiki.specialsurveyservice.domain.accountcode.AccountCodeType;
import com.logwiki.specialsurveyservice.domain.authority.Authority;
import com.logwiki.specialsurveyservice.domain.authority.AuthorityRepository;
import com.logwiki.specialsurveyservice.domain.authority.AuthorityType;
import java.time.LocalDate;
import java.time.Month;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class TokenProviderTest extends IntegrationTestSupport {

    @Autowired
    AccountService accountService;
    @Autowired
    AuthorityRepository authorityRepository;
    @Autowired
    TokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        setAuthority();
    }

    @DisplayName("유효한 access Token을 검증한다.")
    @WithMockUser(username = "duswo0624@naver.com")
    @Test
    void createAccessToken() {
        // given
        String email = "duswo0624@naver.com";
        signupWith(email);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String accessToken = tokenProvider.createAccessToken(authentication);

        // when
        boolean isValid = tokenProvider.validateAccessToken(accessToken);

        // then
        assertThat(isValid).isTrue();
    }

    @DisplayName("유효한 refresh Token을 검증한다.")
    @WithMockUser(username = "duswo0624@naver.com")
    @Test
    void createRefreshToken() {
        // given
        String email = "duswo0624@naver.com";
        signupWith(email);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String accessToken = tokenProvider.createRefreshToken(authentication);

        // when
        boolean isValid = tokenProvider.validateRefreshToken(accessToken);

        // then
        assertThat(isValid).isTrue();
    }

    @DisplayName("토큰을 이용하여 회원 정보를 가져온다.")
    @WithMockUser(username = "duswo0624@naver.com")
    @Test
    void getAuthenticationByToken() {
        // given
        String email = "duswo0624@naver.com";
        signupWith(email);

        Authentication authenticationOrigin = SecurityContextHolder.getContext().getAuthentication();
        String accessToken = tokenProvider.createAccessToken(authenticationOrigin);

        // when
        Authentication authenticationCompare = tokenProvider.getAuthentication(accessToken);

        // then
        assertThat(authenticationOrigin.getName()).isEqualTo(authenticationCompare.getName());
    }

    private void signupWith(String email) {
        AccountCreateServiceRequest accountCreateServiceRequest = AccountCreateServiceRequest.builder()
                .email(email)
                .password("1234")
                .gender(AccountCodeType.MAN)
                .age(AccountCodeType.TWENTIES)
                .name("최연재")
                .phoneNumber("010-1234-5678")
                .birthday(LocalDate.of(1997, Month.JUNE, 24))
                .build();
        accountService.signup(accountCreateServiceRequest);
    }

    private void setAuthority() {
        Authority userAuthority = Authority.builder()
                .type(AuthorityType.ROLE_USER)
                .build();
        authorityRepository.save(userAuthority);
    }
}