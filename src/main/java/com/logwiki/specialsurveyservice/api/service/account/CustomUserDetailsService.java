package com.logwiki.specialsurveyservice.api.service.account;

import com.logwiki.specialsurveyservice.domain.Status;
import com.logwiki.specialsurveyservice.domain.account.Account;
import com.logwiki.specialsurveyservice.domain.account.AccountRepository;
import com.logwiki.specialsurveyservice.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component("userDetailsService")
@Transactional
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(final String email) {

        return accountRepository.findOneWithAuthoritiesByEmail(email)
                .map(account -> createUser(email, account))
                .orElseThrow(() -> new BaseException("존재하지 않는 이메일 입니다.", 2002));
    }

    public org.springframework.security.core.userdetails.User createUser(String email, Account account) {
        if (account.getStatus().equals(Status.INACTIVE)) {
            throw new BaseException("이메일이 활성화되어 있지 않습니다.", 2003);
        }

        List<GrantedAuthority> grantedAuthorities = account.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthority().getType().toString()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(account.getEmail(),
                account.getPassword(),
                grantedAuthorities);
    }
}
