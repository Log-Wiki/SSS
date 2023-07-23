package com.logwiki.specialsurveyservice.api.controller.userdetail;

import com.logwiki.specialsurveyservice.api.service.userdetail.UserDetailService;
import com.logwiki.specialsurveyservice.api.service.userdetail.response.UserDetailResponse;
import com.logwiki.specialsurveyservice.api.utils.ApiResponse;
import com.logwiki.specialsurveyservice.api.utils.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class UserDetailController {

    private final UserDetailService userDetailService;

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ApiResponse<UserDetailResponse> getMyUserInfo() {
        return ApiUtils.success(userDetailService.getMyUserWithAuthorities());
    }

    @GetMapping("/user/{email}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<UserDetailResponse> getUserInfo(@PathVariable String email) {
        return ApiUtils.success(userDetailService.getUserWithAuthorities(email));
    }
}
