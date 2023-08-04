package com.logwiki.specialsurveyservice.api.service.account;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.logwiki.specialsurveyservice.IntegrationTestSupport;
import com.logwiki.specialsurveyservice.api.service.account.request.AccountCreateServiceRequest;
import com.logwiki.specialsurveyservice.domain.Status;
import com.logwiki.specialsurveyservice.domain.account.Account;
import com.logwiki.specialsurveyservice.domain.accountcode.AccountCodeType;
import com.logwiki.specialsurveyservice.domain.authority.Authority;
import com.logwiki.specialsurveyservice.domain.authority.AuthorityRepository;
import com.logwiki.specialsurveyservice.domain.authority.AuthorityType;
import com.logwiki.specialsurveyservice.exception.BaseException;
import java.time.LocalDate;
import java.time.Month;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class CustomUserDetailsServiceTest extends IntegrationTestSupport {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private AuthorityRepository authorityRepository;
    @Autowired
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        setAuthority();
    }

    @DisplayName("유저이메일을 이용하여 유저 정보를 가져온다.")
    @Test
    void loadUserByUsername() {
        String email = "duswo0624@naver.com";
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

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
        assertThat(userDetails.getUsername()).isEqualTo(email);
    }

    @DisplayName("등록된 유저 이메일이 없으면 유저 정보를 가져올 수 없다.")
    @Test
    void cannotLoadUserByInvalidEmail() {
        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername("invalidEmail@naver.com"))
                .isInstanceOf(BaseException.class)
                .hasMessage("존재하지 않는 이메일 입니다.");
    }

    @DisplayName("활성 상태가 비활성화인 유저의 경우, 유저를 가져올 수 없다.")
    @WithMockUser(username = "duswo0624@naver.com")
    @Test
    void check() {
        String email = "duswo0624@naver.com";
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

        Account account = accountService.getCurrentAccountBySecurity();
        account.setStatus(Status.INACTIVE);
        assertThatThrownBy(() -> customUserDetailsService.createUser(email, account))
                .isInstanceOf(BaseException.class)
                .hasMessage("이메일이 활성화되어 있지 않습니다.");
    }

    private void setAuthority() {
        Authority userAuthority = Authority.builder()
                .type(AuthorityType.ROLE_USER)
                .build();

        authorityRepository.save(userAuthority);
    }

}