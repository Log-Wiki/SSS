package com.logwiki.specialsurveyservice.api.controller.account;

import com.logwiki.specialsurveyservice.api.controller.account.request.AccountCreateRequest;
import com.logwiki.specialsurveyservice.api.service.account.AccountService;
import com.logwiki.specialsurveyservice.api.service.account.response.AccountResponse;
import com.logwiki.specialsurveyservice.api.utils.ApiResponse;
import com.logwiki.specialsurveyservice.api.utils.ApiUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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


}
