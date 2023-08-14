package com.logwiki.specialsurveyservice.api.service.account;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.logwiki.specialsurveyservice.IntegrationTestSupport;
import com.logwiki.specialsurveyservice.api.controller.account.request.AccountUpdateRequest;
import com.logwiki.specialsurveyservice.api.controller.account.request.UpdatePasswordRequest;
import com.logwiki.specialsurveyservice.api.controller.auth.request.LoginRequest;
import com.logwiki.specialsurveyservice.api.service.account.request.AccountCreateServiceRequest;
import com.logwiki.specialsurveyservice.api.service.account.response.AccountResponse;
import com.logwiki.specialsurveyservice.api.service.account.response.DuplicateResponse;
import com.logwiki.specialsurveyservice.domain.account.Account;
import com.logwiki.specialsurveyservice.domain.account.AccountRepository;
import com.logwiki.specialsurveyservice.domain.accountcode.AccountCodeType;
import com.logwiki.specialsurveyservice.domain.authority.Authority;
import com.logwiki.specialsurveyservice.domain.authority.AuthorityRepository;
import com.logwiki.specialsurveyservice.domain.authority.AuthorityType;
import com.logwiki.specialsurveyservice.exception.BaseException;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class AccountServiceTest extends IntegrationTestSupport {

    @Autowired
    private AccountService accountService;
    @Autowired
    private AuthorityRepository authorityRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    @BeforeEach
    void setUp() {
        setAuthority();
    }

    @DisplayName("계정이름, 비밀번호, 이메일을 이용하여 계정을 생성한다.")
    @Test
    void signup() {
        // given
        String email = "duswo0624@naver.com";
        String password = "1234";
        AccountCodeType gender = AccountCodeType.MAN;
        AccountCodeType age = AccountCodeType.TWENTIES;
        String name = "최연재";
        String phoneNumber = "010-1234-5678";
        LocalDate birthday = LocalDate.of(1997, Month.JUNE, 24);
        AccountCreateServiceRequest accountCreateServiceRequest = AccountCreateServiceRequest.builder()
                .email(email)
                .password(password)
                .gender(gender)
                .age(age)
                .name(name)
                .phoneNumber(phoneNumber)
                .birthday(birthday)
                .build();

        // when
        AccountResponse accountResponse = accountService.signup(accountCreateServiceRequest);

        // then
        assertThat(accountResponse).isNotNull();
        assertThat(accountResponse)
                .extracting("email", "gender", "name", "phoneNumber", "birthday")
                .contains(email, gender, name, phoneNumber, birthday);
    }

    @DisplayName("중복된 이메일로 회원가입을 진행할 수 없다.")
    @TestFactory
    Collection<DynamicTest> duplicateSignupWithDuplicatedEmail() {
        // given
        String email = "duswo0624@naver.com";

        return List.of(
                DynamicTest.dynamicTest("특정 이메일로 이미 가입된 계정이 없는 경우 회원 가입을 할 수 있다.", () -> {
                    // given
                    String password1 = "1234";
                    AccountCodeType gender1 = AccountCodeType.MAN;
                    AccountCodeType age1 = AccountCodeType.TWENTIES;
                    String name1 = "최연재";
                    String phoneNumber1 = "010-1234-5678";
                    LocalDate birthday1 = LocalDate.of(1997, Month.JUNE, 24);

                    AccountCreateServiceRequest accountCreateServiceRequest1 = AccountCreateServiceRequest.builder()
                            .email(email)
                            .password(password1)
                            .gender(gender1)
                            .age(age1)
                            .name(name1)
                            .phoneNumber(phoneNumber1)
                            .birthday(birthday1)
                            .build();

                    // when
                    AccountResponse accountResponse = accountService.signup(
                            accountCreateServiceRequest1);

                    // then
                    assertThat(accountResponse).isNotNull();
                    assertThat(accountResponse)
                            .extracting("email", "gender", "name", "phoneNumber", "birthday")
                            .contains(email, gender1, name1, phoneNumber1, birthday1);
                }),

                DynamicTest.dynamicTest("가입된 계정들 중 중복 이메일이 있는 경우 회원 가입을 할 수 없다.", () -> {
                    // given
                    String sameEmail = email;
                    String password2 = "5678";
                    AccountCodeType gender2 = AccountCodeType.WOMAN;
                    AccountCodeType age2 = AccountCodeType.THIRTIES;
                    String name2 = "홍길동";
                    String phoneNumber2 = "010-5678-1234";
                    LocalDate birthday2 = LocalDate.of(1990, Month.JANUARY, 1);

                    AccountCreateServiceRequest accountCreateServiceRequest2 = AccountCreateServiceRequest.builder()
                            .email(sameEmail)
                            .password(password2)
                            .gender(gender2)
                            .age(age2)
                            .name(name2)
                            .phoneNumber(phoneNumber2)
                            .birthday(birthday2)
                            .build();

                    // when // then
                    assertThatThrownBy(() -> accountService.signup(accountCreateServiceRequest2))
                            .isInstanceOf(BaseException.class)
                            .hasMessage("동일한 이메일로 가입되어 있는 계정이 존재합니다.");
                })
        );
    }

    @DisplayName("중복된 이메일로 회원가입을 진행할 수 없다.")
    @TestFactory
    Collection<DynamicTest> duplicateSignupWithDuplicatedPhoneNumber() {
        // given
        String phoneNumber = "010-1234-5678";

        return List.of(
                DynamicTest.dynamicTest("특정 이메일로 이미 가입된 계정이 없는 경우 회원 가입을 할 수 있다.", () -> {
                    // given
                    String email1 = "duswo0624@naver.com";
                    String password1 = "1234";
                    AccountCodeType gender1 = AccountCodeType.MAN;
                    AccountCodeType age1 = AccountCodeType.TWENTIES;
                    String name1 = "최연재";

                    LocalDate birthday1 = LocalDate.of(1997, Month.JUNE, 24);

                    AccountCreateServiceRequest accountCreateServiceRequest1 = AccountCreateServiceRequest.builder()
                            .email(email1)
                            .password(password1)
                            .gender(gender1)
                            .age(age1)
                            .name(name1)
                            .phoneNumber(phoneNumber)
                            .birthday(birthday1)
                            .build();

                    // when
                    AccountResponse accountResponse = accountService.signup(
                            accountCreateServiceRequest1);

                    // then
                    assertThat(accountResponse).isNotNull();
                    assertThat(accountResponse)
                            .extracting("email", "gender", "name", "phoneNumber", "birthday")
                            .contains(email1, gender1, name1, phoneNumber, birthday1);
                }),

                DynamicTest.dynamicTest("가입된 계정들 중 중복 이메일이 있는 경우 회원 가입을 할 수 없다.", () -> {
                    // given
                    String email2 = "amenable@naver.com";
                    String password2 = "5678";
                    AccountCodeType gender2 = AccountCodeType.WOMAN;
                    AccountCodeType age2 = AccountCodeType.THIRTIES;
                    String name2 = "홍길동";
                    String samePhoneNumber = phoneNumber;
                    LocalDate birthday2 = LocalDate.of(1990, Month.JANUARY, 1);

                    AccountCreateServiceRequest accountCreateServiceRequest2 = AccountCreateServiceRequest.builder()
                            .email(email2)
                            .password(password2)
                            .gender(gender2)
                            .age(age2)
                            .name(name2)
                            .phoneNumber(samePhoneNumber)
                            .birthday(birthday2)
                            .build();

                    // when // then
                    assertThatThrownBy(() -> accountService.signup(accountCreateServiceRequest2))
                            .isInstanceOf(BaseException.class)
                            .hasMessage("동일한 휴대폰 번호로 가입되어 있는 계정이 존재합니다.");
                })
        );
    }

    @DisplayName("이메일 중복 확인 기능에서 이미 사용하고 있는 이메일이 있는 경우 중복 여부는 참이다.")
    @Test
    void checkDuplicateEmailIfTrue() {
        // given
        String email = "duswo0624@naver.com";
        AccountCreateServiceRequest accountCreateServiceRequest = AccountCreateServiceRequest.builder()
                .email(email)
                .password("1234")
                .gender(AccountCodeType.MAN)
                .age(AccountCodeType.TWENTIES)
                .name("최연재")
                .phoneNumber("010-1111-2222")
                .birthday(LocalDate.of(1997, 6, 24))
                .build();

        accountService.signup(accountCreateServiceRequest);

        // when
        DuplicateResponse duplicateResponse = accountService.checkDuplicateEmail(email);

        // then
        assertThat(duplicateResponse.isDuplicate()).isTrue();
    }

    @DisplayName("이메일 중복 확인 기능에서 이미 사용하고 있는 이메일이 없는 경우 중복 여부는 거짓이다.")
    @Test
    void checkDuplicateEmailIfFalse() {
        // given
        String email = "duswo0624@naver.com";

        // when
        DuplicateResponse duplicateResponse = accountService.checkDuplicateEmail(email);

        // then
        assertThat(duplicateResponse.isDuplicate()).isFalse();
    }

    @DisplayName("핸드폰 번호 중복 확인 기능에서 이미 사용하고 있는 핸드폰 번호가 있는 경우 중복 여부는 참이다.")
    @Test
    void checkDuplicatePhoneNumberIfTrue() {
        // given
        String phoneNumber = "010-1111-2222";
        AccountCreateServiceRequest accountCreateServiceRequest = AccountCreateServiceRequest.builder()
                .email("duswo0624@naver.com")
                .password("1234")
                .gender(AccountCodeType.MAN)
                .age(AccountCodeType.TWENTIES)
                .name("최연재")
                .phoneNumber(phoneNumber)
                .birthday(LocalDate.of(1997, 6, 24))
                .build();

        accountService.signup(accountCreateServiceRequest);

        // when
        DuplicateResponse duplicateResponse = accountService.checkDuplicatePhoneNumber(phoneNumber);

        // then
        assertThat(duplicateResponse.isDuplicate()).isTrue();
    }

    @DisplayName("핸드폰 번호 중복 확인 기능에서 이미 사용하고 있는 핸드폰 번호가 없는 경우 중복 여부는 거짓이다.")
    @Test
    void checkDuplicatePhoneNumberIfFalse() {
        // given
        String phoneNumber = "010-1111-2222";

        // when
        DuplicateResponse duplicateResponse = accountService.checkDuplicatePhoneNumber(phoneNumber);

        // then
        assertThat(duplicateResponse.isDuplicate()).isFalse();
    }

    @DisplayName("로그인이 되어 있는 상태에서 회원 탈퇴를 한다.")
    @WithMockUser(username = "duswo0624@naver.com")
    @Test
    void deleteAccount() {
        // given
        String email = "duswo0624@naver.com";
        AccountCreateServiceRequest accountCreateServiceRequest = AccountCreateServiceRequest.builder()
                .email("duswo0624@naver.com")
                .password("1234")
                .gender(AccountCodeType.MAN)
                .age(AccountCodeType.TWENTIES)
                .name("최연재")
                .phoneNumber("010-1111-2222")
                .birthday(LocalDate.of(1997, 6, 24))
                .build();

        accountService.signup(accountCreateServiceRequest);

        // when
        accountService.deleteAccount();

        // then
        assertThat(accountRepository.findOneWithAuthoritiesByEmail(email).isPresent()).isFalse();
    }

    @DisplayName("회원 정보 수정은 로그인이 되어 있는 사용자의 정보를 수정한다.")
    @WithMockUser(username = "duswo0624@naver.com")
    @Test
    void updateAccount() {
        // given
        AccountCreateServiceRequest accountCreateServiceRequest = AccountCreateServiceRequest.builder()
                .email("duswo0624@naver.com")
                .password("1234")
                .gender(AccountCodeType.MAN)
                .age(AccountCodeType.TWENTIES)
                .name("최연재")
                .phoneNumber("010-1111-2222")
                .birthday(LocalDate.of(1997, 6, 24))
                .build();

        accountService.signup(accountCreateServiceRequest);

        String updatePassword = "5678";
        String updateName = "최연습";
        String updatePhoneNumber = "010-9999-8888";
        AccountUpdateRequest accountUpdateRequest = AccountUpdateRequest.builder()
                .password(updatePassword)
                .name(updateName)
                .phoneNumber(updatePhoneNumber)
                .build();

        // when
        accountService.updateAccount(accountUpdateRequest);

        // then
        Account updatedAccount = accountService.getCurrentAccountBySecurity();
        assertThat(updatedAccount.getName()).isEqualTo(updateName);
        assertThat(updatedAccount.getPhoneNumber()).isEqualTo(updatePhoneNumber);
    }

    @DisplayName("회원 정보를 수정할 때 비밀번호를 꼭 바꿔야 하는 것은 아니다.")
    @WithMockUser(username = "duswo0624@naver.com")
    @Test
    void updateAccountWithoutUpdatePassword() {
        // given
        AccountCreateServiceRequest accountCreateServiceRequest = AccountCreateServiceRequest.builder()
                .email("duswo0624@naver.com")
                .password("1234")
                .gender(AccountCodeType.MAN)
                .age(AccountCodeType.TWENTIES)
                .name("최연재")
                .phoneNumber("010-1111-2222")
                .birthday(LocalDate.of(1997, 6, 24))
                .build();

        accountService.signup(accountCreateServiceRequest);

        String updateName = "최연습";
        String updatePhoneNumber = "010-9999-8888";
        AccountUpdateRequest accountUpdateRequest = AccountUpdateRequest.builder()
                .password(null)
                .name(updateName)
                .phoneNumber(updatePhoneNumber)
                .build();

        // when
        accountService.updateAccount(accountUpdateRequest);

        // then
        Account updatedAccount = accountService.getCurrentAccountBySecurity();
        assertThat(updatedAccount.getName()).isEqualTo(updateName);
        assertThat(updatedAccount.getPhoneNumber()).isEqualTo(updatePhoneNumber);
    }

    @DisplayName("회원 정보를 수정할 때 이름을 꼭 바꿔야 하는 것은 아니다.")
    @WithMockUser(username = "duswo0624@naver.com")
    @Test
    void updateAccountWithoutName() {
        // given
        AccountCreateServiceRequest accountCreateServiceRequest = AccountCreateServiceRequest.builder()
                .email("duswo0624@naver.com")
                .password("1234")
                .gender(AccountCodeType.MAN)
                .age(AccountCodeType.TWENTIES)
                .name("최연재")
                .phoneNumber("010-1111-2222")
                .birthday(LocalDate.of(1997, 6, 24))
                .build();

        accountService.signup(accountCreateServiceRequest);

        String updatePassword = "5678";
        String updatePhoneNumber = "010-9999-8888";
        AccountUpdateRequest accountUpdateRequest = AccountUpdateRequest.builder()
                .password(updatePassword)
                .name(null)
                .phoneNumber(updatePhoneNumber)
                .build();

        // when
        accountService.updateAccount(accountUpdateRequest);

        // then
        Account updatedAccount = accountService.getCurrentAccountBySecurity();
        assertThat(updatedAccount.getPhoneNumber()).isEqualTo(updatePhoneNumber);
    }

    @DisplayName("회원 정보를 수정할 때 핸드폰 번호를 꼭 바꿔야 하는 것은 아니다.")
    @WithMockUser(username = "duswo0624@naver.com")
    @Test
    void updateAccountWithoutEmail() {
        // given
        AccountCreateServiceRequest accountCreateServiceRequest = AccountCreateServiceRequest.builder()
                .email("duswo0624@naver.com")
                .password("1234")
                .gender(AccountCodeType.MAN)
                .age(AccountCodeType.TWENTIES)
                .name("최연재")
                .phoneNumber("010-1111-2222")
                .birthday(LocalDate.of(1997, 6, 24))
                .build();

        accountService.signup(accountCreateServiceRequest);

        String updatePassword = "5678";
        String updateName = "최연습";
        AccountUpdateRequest accountUpdateRequest = AccountUpdateRequest.builder()
                .password(updatePassword)
                .name(updateName)
                .phoneNumber(null)
                .build();

        // when
        accountService.updateAccount(accountUpdateRequest);

        // then
        Account updatedAccount = accountService.getCurrentAccountBySecurity();
        assertThat(updatedAccount.getName()).isEqualTo(updateName);
    }

    @DisplayName("회원 정보를 수정할 때 이미 사용하고 있는 핸드폰 번호로 핸드폰 번호를 바꿀 수 없다.")
    @WithMockUser(username = "duswo0624@naver.com")
    @Test
    void updateAccountWithDuplicatedPhoneNumber() {
        // given
        String phoneNumber = "010-1111-2222";
        AccountCreateServiceRequest accountCreateServiceRequest1 = AccountCreateServiceRequest.builder()
                .email("anotherUser@naver.com")
                .password("1234")
                .gender(AccountCodeType.MAN)
                .age(AccountCodeType.TWENTIES)
                .name("박슬빈")
                .phoneNumber(phoneNumber)
                .birthday(LocalDate.of(1997, 1, 1))
                .build();

        accountService.signup(accountCreateServiceRequest1);


        AccountCreateServiceRequest accountCreateServiceRequest2 = AccountCreateServiceRequest.builder()
                .email("duswo0624@naver.com")
                .password("1234")
                .gender(AccountCodeType.MAN)
                .age(AccountCodeType.TWENTIES)
                .name("최연재")
                .phoneNumber("010-2222-3333")
                .birthday(LocalDate.of(1997, 6, 24))
                .build();

        accountService.signup(accountCreateServiceRequest2);

        String updatePassword = "5678";
        String updateName = "최연습";
        String duplicatedUpdatePhoneNumber = phoneNumber;
        AccountUpdateRequest accountUpdateRequest = AccountUpdateRequest.builder()
                .password(updatePassword)
                .name(updateName)
                .phoneNumber(duplicatedUpdatePhoneNumber)
                .build();

        // when // then
        assertThatThrownBy(() -> accountService.updateAccount(accountUpdateRequest))
                .isInstanceOf(BaseException.class)
                .hasMessage("동일한 휴대폰 번호로 가입되어 있는 계정이 존재합니다.");
    }

    @DisplayName("로그인 후 SecurityContext에 정보가 있지만 DB에서 회원을 조회할 수 없는 경우 존재하지 않는 유저라고 인식한다.")
    @WithMockUser(username = "duswo0624@naver.com")
    @Test
    void getCurrentAccountBySecurityWithoutCurrentAccount() {
        assertThatThrownBy(() -> accountService.getCurrentAccountBySecurity())
                .isInstanceOf(BaseException.class)
                .hasMessage("존재하지 않는 유저입니다.");
    }

    @DisplayName("회원ID를 이용하여 회원을 조회할 수 없는 경우 예외를 던진다.")
    @WithMockUser(username = "duswo0624@naver.com")
    @Test
    void getUserNameByInvalidUserId() {
        Long invalidUserId = 1L;
        assertThatThrownBy(() -> accountService.getUserNameById(invalidUserId))
                .isInstanceOf(BaseException.class)
                .hasMessage("존재하지 않는 유저입니다.");
    }

    @DisplayName("핸드폰 번호를 이용하여 유저를 조회한다.")
    @Test
    void getUserByPhoneNumber() {
        // given
        String phoneNumber = "010-1111-2222";
        String email = "duswo0624@naver.com";
        String name = "최연재";
        AccountCreateServiceRequest accountCreateServiceRequest = AccountCreateServiceRequest.builder()
                .email(email)
                .password("1234")
                .gender(AccountCodeType.MAN)
                .age(AccountCodeType.TWENTIES)
                .name(name)
                .phoneNumber(phoneNumber)
                .birthday(LocalDate.of(1997, 6, 24))
                .build();

        accountService.signup(accountCreateServiceRequest);

        // when
        AccountResponse accountResponse = accountService.getUserByPhoneNumber(phoneNumber);

        // then
        assertThat(accountResponse.getEmail()).isEqualTo(email);
        assertThat(accountResponse.getName()).isEqualTo(name);
    }

    @DisplayName("조회하려고 하는 핸드폰 번호를 가진 유저가 없는 경우, 핸드폰 번호로 유저를 조회할 수 없다.")
    @Test
    void cannotGetUserByPhoneNumberWithInvalidPhoneNumber() {
        // given
        String invalidPhoneNumber = "010-1111-2222";

        // when // then
        assertThatThrownBy(() -> accountService.getUserByPhoneNumber(invalidPhoneNumber))
                .isInstanceOf(BaseException.class)
                .hasMessage("존재하지 않는 유저입니다.");
    }

    @DisplayName("이메일과 재설정할 비밀번호를 이용하여 회원의 비밀번호를 재설정한다.")
    @Test
    void updatePassword() {
        // given
        String email = "duswo0624@naver.com";
        String name = "최연재";
        AccountCreateServiceRequest accountCreateServiceRequest = AccountCreateServiceRequest.builder()
                .email(email)
                .password("1234")
                .gender(AccountCodeType.MAN)
                .age(AccountCodeType.TWENTIES)
                .name(name)
                .phoneNumber("010-1111-2222")
                .birthday(LocalDate.of(1997, 6, 24))
                .build();

        accountService.signup(accountCreateServiceRequest);

        String updatePassword = "4321";
        UpdatePasswordRequest updatePasswordRequest = UpdatePasswordRequest
                .builder()
                .email(email)
                .password(updatePassword)
                .build();

        // when
        accountService.updatePassword(updatePasswordRequest);

        // then
        LoginRequest loginRequest = LoginRequest
                .builder()
                .email(email)
                .password(updatePassword)
                .build();

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

        Authentication authenticate = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);
        assertThat(authenticate.getName()).isEqualTo(email);
    }

    @DisplayName("존재하지 않는 유저 이메일을 이용해서 유저의 비밀번호를 재설정할 수 없다.")
    @Test
    void cannotUpdatePasswordWithInvalidEmail() {
        // given
        String InvalidEmail = "duswo0624@naver.com";
        String updatePassword = "4321";
        UpdatePasswordRequest updatePasswordRequest = UpdatePasswordRequest
                .builder()
                .email(InvalidEmail)
                .password(updatePassword)
                .build();

        // when
        assertThatThrownBy(() -> accountService.updatePassword(updatePasswordRequest))
                .isInstanceOf(BaseException.class)
                .hasMessage("존재하지 않는 유저입니다.");
    }

    private void setAuthority() {
        Authority userAuthority = Authority.builder()
                .type(AuthorityType.ROLE_USER)
                .build();

        authorityRepository.save(userAuthority);
    }
}