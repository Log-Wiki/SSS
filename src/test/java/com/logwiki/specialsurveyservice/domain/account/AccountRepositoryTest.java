package com.logwiki.specialsurveyservice.domain.account;

import static org.assertj.core.api.Assertions.assertThat;

import com.logwiki.specialsurveyservice.IntegrationTestSupport;
import com.logwiki.specialsurveyservice.domain.authority.Authority;
import com.logwiki.specialsurveyservice.domain.authority.AuthorityRepository;
import com.logwiki.specialsurveyservice.domain.authority.AuthorityType;
import com.logwiki.specialsurveyservice.domain.sex.Sex;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class AccountRepositoryTest extends IntegrationTestSupport {

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private AuthorityRepository authorityRepository;

  @DisplayName("이메일을 이용하여 계정을 찾는다.")
  @Test
  void findOneWithEmail() {
    Authority authority = Authority.builder().
        type(AuthorityType.ROLE_USER)
        .build();
    authorityRepository.save(authority);

    List<Authority> authorities = List.of(authority);
    String email = "duswo0624@naver.com";
    Account account = Account.builder()
        .email(email)
        .password("1234")
        .sex(Sex.MALE)
        .name("최연재")
        .phoneNumber("010-1234-5678")
        .birthday(LocalDateTime.of(1997, 06, 24, 00, 00))
        .authorities(authorities)
        .build();

    accountRepository.save(account);

    // when
    Optional<Account> findAccount = accountRepository.findOneWithAuthoritiesByEmail(email);

    // then
    assertThat(findAccount.isEmpty()).isFalse();
    assertThat(findAccount.get()).isEqualTo(account);
  }
}