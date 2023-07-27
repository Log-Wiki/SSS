package com.logwiki.specialsurveyservice.api.controller.auth;

import com.logwiki.specialsurveyservice.api.controller.auth.request.LoginRequest;
import com.logwiki.specialsurveyservice.api.controller.auth.request.RefreshRequest;
import com.logwiki.specialsurveyservice.api.controller.auth.response.LoginResponse;
import com.logwiki.specialsurveyservice.api.controller.auth.response.RefreshResponse;
import com.logwiki.specialsurveyservice.api.service.auth.AuthService;
import com.logwiki.specialsurveyservice.api.utils.ApiResponse;
import com.logwiki.specialsurveyservice.api.utils.ApiUtils;
import com.logwiki.specialsurveyservice.exception.BaseException;
import com.logwiki.specialsurveyservice.jwt.JwtFilter;
import com.logwiki.specialsurveyservice.jwt.TokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class AuthController {

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final AuthService authService;

    @PostMapping("/authenticate")
    public ResponseEntity<ApiResponse<LoginResponse>> authorize(@Valid @RequestBody LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = tokenProvider.createAccessToken(authentication);
        String refreshToken = tokenProvider.createRefreshToken(authentication);
        authService.saveRefreshToken(loginRequest.getEmail(), refreshToken);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + accessToken);

        ApiResponse<LoginResponse> apiResponse = ApiUtils.success(new LoginResponse(accessToken, refreshToken));
        return new ResponseEntity<>(apiResponse, httpHeaders, HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<RefreshResponse>> refresh(@Valid @RequestBody RefreshRequest refreshRequest) {

        tokenProvider.validateRefreshToken(refreshRequest.getRefreshToken());

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(refreshRequest.getEmail(), refreshRequest.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = tokenProvider.createAccessToken(authentication);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + accessToken);

        ApiResponse<RefreshResponse> apiResponse = ApiUtils.success(new RefreshResponse(accessToken));
        return new ResponseEntity<>(apiResponse, httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/hello")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("hello");
    }
}
