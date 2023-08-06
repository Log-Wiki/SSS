package com.logwiki.specialsurveyservice.api.controller.account;

import com.logwiki.specialsurveyservice.api.controller.account.request.AccountCreateRequest;
import com.logwiki.specialsurveyservice.api.controller.account.request.AccountUpdateRequest;
import com.logwiki.specialsurveyservice.api.controller.account.request.CheckDuplicateEmailRequest;
import com.logwiki.specialsurveyservice.api.controller.account.request.CheckDuplicatePhoneNumberRequest;
import com.logwiki.specialsurveyservice.api.service.account.AccountService;
import com.logwiki.specialsurveyservice.api.service.account.response.AccountResponse;
import com.logwiki.specialsurveyservice.api.service.account.response.DuplicateResponse;
import com.logwiki.specialsurveyservice.api.utils.ApiResponse;
import com.logwiki.specialsurveyservice.api.utils.ApiUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/signup")
    public ApiResponse<AccountResponse> signup(
            @Valid @RequestBody AccountCreateRequest accountCreateRequest
    ) {
        return ApiUtils.success(
                accountService.signup(accountCreateRequest.toServiceRequest()));
    }

    @PatchMapping("/user")
    public ApiResponse<AccountResponse> update(@Valid @RequestBody AccountUpdateRequest accountUpdateRequest) {
        return ApiUtils.success(accountService.updateAccount(accountUpdateRequest));
    }

    @DeleteMapping("/user")
    public ApiResponse<AccountResponse> delete() {
        return ApiUtils.success(accountService.deleteAccount());
    }

    @PostMapping("/duplicate/email")
    public ApiResponse<DuplicateResponse> checkDuplicateEmail(@Valid @RequestBody CheckDuplicateEmailRequest checkDuplicateEmailRequest) {
        return ApiUtils.success(accountService.checkDuplicateEmail(checkDuplicateEmailRequest.getEmail()));
    }

    @PostMapping("/duplicate/phone-number")
    public ApiResponse<DuplicateResponse> checkDuplicatePhoneNumber(@Valid @RequestBody CheckDuplicatePhoneNumberRequest checkDuplicatePhoneNumberRequest) {
        return ApiUtils.success(accountService.checkDuplicatePhoneNumber(checkDuplicatePhoneNumberRequest.getPhoneNumber()));
    }
}
