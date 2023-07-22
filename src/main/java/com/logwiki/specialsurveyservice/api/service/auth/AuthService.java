package com.logwiki.specialsurveyservice.api.service.auth;

import com.logwiki.specialsurveyservice.domain.account.Account;
import com.logwiki.specialsurveyservice.domain.account.AccountRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class AuthService {

    private final AccountRepository accountRepository;

    public void saveRefreshToken(String email, String refreshToken) {
        Optional<Account> account = accountRepository.findOneWithAuthoritiesByEmail(email);
        account.ifPresent(value -> value.setRefreshToken(refreshToken));
    }
}
