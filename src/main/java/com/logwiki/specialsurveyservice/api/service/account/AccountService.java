package com.logwiki.specialsurveyservice.api.service.account;

import com.logwiki.specialsurveyservice.api.service.account.request.AccountCreateServiceRequest;
import com.logwiki.specialsurveyservice.api.service.account.response.AccountResponse;
import com.logwiki.specialsurveyservice.domain.account.Account;
import com.logwiki.specialsurveyservice.domain.account.AccountRepository;
import com.logwiki.specialsurveyservice.domain.authority.Authority;
import com.logwiki.specialsurveyservice.domain.authority.AuthorityRepository;
import com.logwiki.specialsurveyservice.domain.authority.AuthorityType;
import com.logwiki.specialsurveyservice.exception.account.DuplicatedAccountException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountResponse signup(AccountCreateServiceRequest request) {
        if (accountRepository.findOneWithAuthoritiesByEmail(request.getEmail()).orElse(null)
                != null) {
            throw new DuplicatedAccountException("이미 가입되어 있는 유저입니다.");
        }

        Authority authority = authorityRepository.findAuthorityByType(AuthorityType.ROLE_USER);
        List<Authority> authorities = List.of(authority);

        Account account = Account.create(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getGender(),
                request.getName(),
                request.getPhoneNumber(),
                request.getBirthday(),
                authorities
        );

        return AccountResponse.from(accountRepository.save(account));
    }
}
