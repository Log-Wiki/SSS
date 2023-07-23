package com.logwiki.specialsurveyservice.api.service.account;

import com.logwiki.specialsurveyservice.domain.Status;
import com.logwiki.specialsurveyservice.domain.account.Account;
import com.logwiki.specialsurveyservice.domain.account.AccountRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component("userDetailsService")
@Transactional
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(final String email) {

        return accountRepository.findOneWithAuthoritiesByEmail(email)
                .map(account -> createUser(email, account))
                .orElseThrow(() -> new UsernameNotFoundException(email + " -> 데이터베이스에서 찾을 수 없습니다."));
    }

    public org.springframework.security.core.userdetails.User createUser(String email, Account account) {
        if (account.getStatus().equals(Status.INACTIVE)) {
            throw new RuntimeException(email + " -> 활성화되어 있지 않습니다.");
        }

        List<GrantedAuthority> grantedAuthorities = account.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthority().getType().toString()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(account.getEmail(),
                account.getPassword(),
                grantedAuthorities);
    }
}
