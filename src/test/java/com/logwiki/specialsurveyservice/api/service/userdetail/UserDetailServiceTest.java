package com.logwiki.specialsurveyservice.api.service.userdetail;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.logwiki.specialsurveyservice.IntegrationTestSupport;
import com.logwiki.specialsurveyservice.api.service.account.AccountService;
import com.logwiki.specialsurveyservice.api.service.account.request.AccountCreateServiceRequest;
import com.logwiki.specialsurveyservice.api.service.userdetail.response.UserDetailResponse;
import com.logwiki.specialsurveyservice.domain.authority.Authority;
import com.logwiki.specialsurveyservice.domain.authority.AuthorityRepository;
import com.logwiki.specialsurveyservice.domain.authority.AuthorityType;
import com.logwiki.specialsurveyservice.domain.gender.Gender;
import com.logwiki.specialsurveyservice.exception.security.NotFoundAuthenticationException;
import java.time.LocalDate;
import java.time.Month;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class UserDetailServiceTest extends IntegrationTestSupport {

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserDetailService userDetailService;

    @Autowired
    private AuthorityRepository authorityRepository;

    @BeforeEach
    void setUp() {
        setAuthority();
    }

    @DisplayName("로그인 되어 있는 이메일을 이용하여 자신의 정보를 가져온다.")
    @Test
    @WithMockUser(username = "duswo0624@naver.com", roles = "USER")
    void getMyUserWhileLoggedId() {
        // given
        String email = "duswo0624@naver.com";
        String password = "1234";
        Gender gender = Gender.MALE;
        String name = "최연재";
        String phoneNumber = "010-1234-5678";
        LocalDate birthday = LocalDate.of(1997, Month.JUNE, 24);
        AccountCreateServiceRequest accountCreateServiceRequest = AccountCreateServiceRequest.builder()
                .email(email)
                .password(password)
                .gender(gender)
                .name(name)
                .phoneNumber(phoneNumber)
                .birthday(birthday)
                .build();

        accountService.signup(accountCreateServiceRequest);

        // when
        UserDetailResponse userDetailResponse = userDetailService.getMyUserWithAuthorities();

        // then
        assertThat(userDetailResponse).isNotNull();
        assertThat(userDetailResponse)
                .extracting("email", "gender", "name", "phoneNumber", "birthday")
                .contains(email, gender, name, phoneNumber, birthday);
    }

    @DisplayName("로그인 되어 있지 않은 상태에서는 자신의 정보를 가져올 수 없다.")
    @Test
    void getMyUserWhileNotLoggedIn() {
        // given
        String email = "duswo0624@naver.com";
        String password = "1234";
        Gender gender = Gender.MALE;
        String name = "최연재";
        String phoneNumber = "010-1234-5678";
        LocalDate birthday = LocalDate.of(1997, Month.JUNE, 24);
        AccountCreateServiceRequest accountCreateServiceRequest = AccountCreateServiceRequest.builder()
                .email(email)
                .password(password)
                .gender(gender)
                .name(name)
                .phoneNumber(phoneNumber)
                .birthday(birthday)
                .build();

        accountService.signup(accountCreateServiceRequest);

        // when // then
        assertThatThrownBy(() -> userDetailService.getMyUserWithAuthorities())
                .isInstanceOf(NotFoundAuthenticationException.class)
                .hasMessage("인증 정보가 등록되어 있지 않습니다.");
    }

    @DisplayName("계정 이름을 이용하여 계정 정보를 가져온다.")
    @Test
    void getUser() {
        // given
        String email = "duswo0624@naver.com";
        String password = "1234";
        Gender gender = Gender.MALE;
        String name = "최연재";
        String phoneNumber = "010-1234-5678";
        LocalDate birthday = LocalDate.of(1997, Month.JUNE, 24);
        AccountCreateServiceRequest accountCreateServiceRequest = AccountCreateServiceRequest.builder()
                .email(email)
                .password(password)
                .gender(gender)
                .name(name)
                .phoneNumber(phoneNumber)
                .birthday(birthday)
                .build();
        accountService.signup(accountCreateServiceRequest);

        // when
        UserDetailResponse userDetailResponse = userDetailService.getUserWithAuthorities(email);

        // then
        assertThat(userDetailResponse).isNotNull();
        assertThat(userDetailResponse)
                .extracting("email", "gender", "name", "phoneNumber", "birthday")
                .contains(email, gender, name, phoneNumber, birthday);
    }

    private void setAuthority() {
        Authority userAuthority = Authority.builder()
                .type(AuthorityType.ROLE_USER)
                .build();

        authorityRepository.save(userAuthority);
    }
}