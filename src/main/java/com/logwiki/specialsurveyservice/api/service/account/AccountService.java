package com.logwiki.specialsurveyservice.api.service.account;

import com.logwiki.specialsurveyservice.api.controller.account.request.AccountUpdateRequest;
import com.logwiki.specialsurveyservice.api.controller.account.request.UpdatePasswordRequest;
import com.logwiki.specialsurveyservice.api.service.account.request.AccountCreateServiceRequest;
import com.logwiki.specialsurveyservice.api.service.account.response.AccountResponse;
import com.logwiki.specialsurveyservice.api.service.account.response.DuplicateResponse;
import com.logwiki.specialsurveyservice.api.utils.SecurityUtil;
import com.logwiki.specialsurveyservice.domain.account.Account;
import com.logwiki.specialsurveyservice.domain.account.AccountRepository;
import com.logwiki.specialsurveyservice.domain.authority.Authority;
import com.logwiki.specialsurveyservice.domain.authority.AuthorityRepository;
import com.logwiki.specialsurveyservice.domain.authority.AuthorityType;
import com.logwiki.specialsurveyservice.exception.BaseException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AccountResponse signup(AccountCreateServiceRequest request) {
        if (accountRepository.findOneWithAuthoritiesByEmail(request.getEmail()).orElse(null)
                != null) {
            throw new BaseException("동일한 이메일로 가입되어 있는 계정이 존재합니다.", 2001);
        }
        if (accountRepository.findOneWithAuthoritiesByPhoneNumber(request.getPhoneNumber()).orElse(null)
                != null) {
            throw new BaseException("동일한 휴대폰 번호로 가입되어 있는 계정이 존재합니다.", 2007);
        }

        Authority authority = authorityRepository.findAuthorityByType(AuthorityType.ROLE_USER);
        List<Authority> authorities = List.of(authority);

        Account account = Account.create(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getGender(),
                request.getAge(),
                request.getName(),
                request.getPhoneNumber(),
                request.getBirthday(),
                authorities
        );

        return AccountResponse.from(accountRepository.save(account));
    }

    public Account getCurrentAccountBySecurity() {
        return SecurityUtil.getCurrentUsername()
                .flatMap(accountRepository::findOneWithAuthoritiesByEmail)
                .orElseThrow(() -> new BaseException("존재하지 않는 유저입니다.", 2000));
    }

    public String getUserNameById(Long userId) {
        return accountRepository.findById(userId)
                .orElseThrow(() -> new BaseException("존재하지 않는 유저입니다.", 2000))
                .getName();
    }

    @Transactional
    public AccountResponse updateAccount(AccountUpdateRequest accountUpdateRequest) {
        Account account = getCurrentAccountBySecurity();

        if (accountUpdateRequest.getPassword() != null) {
            accountUpdateRequest.encodePassword(passwordEncoder.encode(accountUpdateRequest.getPassword()));
        }

        if (accountUpdateRequest.getPhoneNumber() != null) {
            if (accountRepository.findOneWithAuthoritiesByPhoneNumber(accountUpdateRequest.getPhoneNumber())
                    .orElse(null)
                    != null) {
                throw new BaseException("동일한 휴대폰 번호로 가입되어 있는 계정이 존재합니다.", 2007);
            }
        }

        Account updatedAccount = account.update(accountUpdateRequest);

        return AccountResponse.from(updatedAccount);
    }

    @Transactional
    public AccountResponse deleteAccount() {
        Account account = getCurrentAccountBySecurity();
        accountRepository.delete(account);

        return AccountResponse.from(account);
    }

    public DuplicateResponse checkDuplicateEmail(String email) {
        Optional<Account> account = accountRepository.findOneWithAuthoritiesByEmail(email);
        boolean duplicate = account.isPresent();

        return DuplicateResponse.builder()
                .duplicate(duplicate)
                .build();
    }

    public DuplicateResponse checkDuplicatePhoneNumber(String phoneNumber) {
        Optional<Account> account = accountRepository.findOneWithAuthoritiesByPhoneNumber(phoneNumber);
        boolean duplicate = account.isPresent();

        return DuplicateResponse.builder()
                .duplicate(duplicate)
                .build();
    }

    public AccountResponse getUserByPhoneNumber(String phoneNumber) {
        return AccountResponse.from(
                accountRepository.findOneWithAuthoritiesByPhoneNumber(phoneNumber)
                        .orElseThrow(() -> new BaseException("존재하지 않는 유저입니다.", 2000)));
    }

    @Transactional
    public AccountResponse updatePassword(UpdatePasswordRequest updatePasswordRequest) {
        Account account = accountRepository.findOneWithAuthoritiesByEmail(updatePasswordRequest.getEmail())
                .orElseThrow(() -> new BaseException("존재하지 않는 유저입니다.", 2000));

        AccountUpdateRequest accountUpdateRequest = AccountUpdateRequest
                .builder()
                .password(passwordEncoder.encode(updatePasswordRequest.getPassword()))
                .build();

        Account updatedAccount = account.update(accountUpdateRequest);

        return AccountResponse.from(updatedAccount);
    }
}
