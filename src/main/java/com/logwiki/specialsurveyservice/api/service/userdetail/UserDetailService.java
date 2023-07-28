package com.logwiki.specialsurveyservice.api.service.userdetail;

import com.logwiki.specialsurveyservice.api.service.userdetail.response.UserDetailResponse;
import com.logwiki.specialsurveyservice.api.utils.SecurityUtil;
import com.logwiki.specialsurveyservice.domain.account.AccountRepository;
import com.logwiki.specialsurveyservice.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class UserDetailService {

    private final AccountRepository accountRepository;

    @Transactional(readOnly = true)
    public UserDetailResponse getMyUserWithAuthorities() {
        return UserDetailResponse.from(
                SecurityUtil.getCurrentUsername()
                        .flatMap(accountRepository::findOneWithAuthoritiesByEmail)
                        .orElseThrow(() -> new BaseException("존재하지 않는 유저입니다.", 2000))
        );
    }

    @Transactional(readOnly = true)
    public UserDetailResponse getUserWithAuthorities(String email) {
        return UserDetailResponse.from(accountRepository.findOneWithAuthoritiesByEmail(email).orElse(null));
    }
}
