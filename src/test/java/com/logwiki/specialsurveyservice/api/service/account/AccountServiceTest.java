package com.logwiki.specialsurveyservice.api.service.account;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.logwiki.specialsurveyservice.IntegrationTestSupport;
import com.logwiki.specialsurveyservice.api.service.account.request.AccountCreateServiceRequest;
import com.logwiki.specialsurveyservice.api.service.account.response.AccountResponse;
import com.logwiki.specialsurveyservice.domain.accountcode.AccountCodeType;
import com.logwiki.specialsurveyservice.domain.authority.Authority;
import com.logwiki.specialsurveyservice.domain.authority.AuthorityRepository;
import com.logwiki.specialsurveyservice.domain.authority.AuthorityType;
import com.logwiki.specialsurveyservice.exception.account.DuplicatedAccountException;
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
import org.springframework.transaction.annotation.Transactional;

@Transactional
class AccountServiceTest extends IntegrationTestSupport {

  @Autowired
  private AccountService accountService;
  @Autowired
  private AuthorityRepository authorityRepository;

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

  @DisplayName("중복 회원 가입 시나리오")
  @TestFactory
  Collection<DynamicTest> duplicateSignup() {
    // given
    String email = "duswo0624@naver.com";

    return List.of(
        DynamicTest.dynamicTest("이미 가입된 계정이 없는 경우 회원 가입을 할 수 있다.", () -> {
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
          AccountResponse accountResponse = accountService.signup(accountCreateServiceRequest1);

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
              .isInstanceOf(DuplicatedAccountException.class)
              .hasMessage("이미 가입되어 있는 유저입니다.");
        })
    );
  }

  private void setAuthority() {
    Authority userAuthority = Authority.builder()
        .type(AuthorityType.ROLE_USER)
        .build();

    authorityRepository.save(userAuthority);
  }
}