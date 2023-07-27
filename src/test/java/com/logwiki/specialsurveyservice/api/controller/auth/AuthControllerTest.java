package com.logwiki.specialsurveyservice.api.controller.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.logwiki.specialsurveyservice.ControllerTestSupport;
import com.logwiki.specialsurveyservice.api.controller.auth.request.LoginRequest;
import com.logwiki.specialsurveyservice.api.controller.auth.request.RefreshRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;

class AuthControllerTest extends ControllerTestSupport {

    @DisplayName("이메일과 비밀번호를 이용하여 로그인을 한다.")
    @WithMockUser
    @Test
    void authorize() throws Exception {
        // given
        AuthenticationManager mockAuthenticationManager = mock(AuthenticationManager.class);
        Authentication authentication = mock(Authentication.class);

        given(authenticationManagerBuilder.getObject()).willReturn(mockAuthenticationManager);
        given(mockAuthenticationManager.authenticate(ArgumentMatchers.any(UsernamePasswordAuthenticationToken.class))).willReturn(authentication);

        String accessToken = "access-token";
        when(tokenProvider.createAccessToken(authentication)).thenReturn(accessToken);

        LoginRequest loginRequest = LoginRequest.builder()
                .email("duswo0624@naver.com")
                .password("1234")
                .build();

        // when // then
        mockMvc.perform(
                        post("/api/authenticate")
                                .content(objectMapper.writeValueAsString(loginRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
        ;
    }

    @DisplayName("로그인을 하면 access token과 refresh token을 받게 된다.")
    @WithMockUser
    @Test
    void authorizeThenReturnAccessTokenAndRefreshToken() throws Exception {
        // given
        AuthenticationManager mockAuthenticationManager = mock(AuthenticationManager.class);
        Authentication authentication = mock(Authentication.class);

        given(authenticationManagerBuilder.getObject()).willReturn(mockAuthenticationManager);
        given(mockAuthenticationManager.authenticate(ArgumentMatchers.any(UsernamePasswordAuthenticationToken.class))).willReturn(authentication);

        String accessToken = "access-token";
        when(tokenProvider.createAccessToken(authentication)).thenReturn(accessToken);
        String refreshToken = "refresh-token";
        when(tokenProvider.createRefreshToken(authentication)).thenReturn(refreshToken);

        LoginRequest loginRequest = LoginRequest.builder()
                .email("duswo0624@naver.com")
                .password("1234")
                .build();

        // when // then
        mockMvc.perform(
                        post("/api/authenticate")
                                .content(objectMapper.writeValueAsString(loginRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.response.accessToken").value(accessToken))
                .andExpect(jsonPath("$.response.refreshToken").value(refreshToken))
        ;
    }

    @DisplayName("로그인을 할 때 이메일은 필수값이다.")
    @WithMockUser
    @Test
    void authorizeWithoutAccountName() throws Exception {
        // given
        LoginRequest request =
                createLoginRequest(null, "1234");

        // when // then
        mockMvc.perform(
                        post("/api/authenticate")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.apiError.message").value("이메일은 필수입니다."))
                .andExpect(jsonPath("$.apiError.status").value(1000))
        ;
    }

    @DisplayName("로그인을 할 때 이메일은 이메일의 형식을 따라야 한다.")
    @WithMockUser
    @Test
    void authorizeWithoutWrongEmailFormat() throws Exception {
        // given
        LoginRequest request =
                createLoginRequest("emailNotFollowEmailFormat", "1234");

        // when // then
        mockMvc.perform(
                        post("/api/authenticate")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.apiError.message").value("이메일 형식에 맞지 않습니다."))
                .andExpect(jsonPath("$.apiError.status").value(1000))
        ;
    }

    @DisplayName("로그인을 할 때 비밀번호는 필수값이다.")
    @WithMockUser
    @Test
    void authorizeWithoutPassword() throws Exception {
        // given
        LoginRequest request =
                createLoginRequest("duswo0624@naver.com", null);

        // when // then
        mockMvc.perform(
                        post("/api/authenticate")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.apiError.message").value("패스워드는 필수입니다."))
                .andExpect(jsonPath("$.apiError.status").value(1000))
        ;
    }

    @DisplayName("로그인을 할 때 비밀번호의 길이는 3이상이다.")
    @WithMockUser
    @Test
    void authorizeWithPasswordNotInRange1() throws Exception {
        // given
        LoginRequest request =
                createLoginRequest("duswo0624@naver.com", "12");

        // when // then
        mockMvc.perform(
                        post("/api/authenticate")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.apiError.message").value("패스워드의 길이는 3이상 30이하 입니다."))
                .andExpect(jsonPath("$.apiError.status").value(1000))
        ;
    }

    @DisplayName("로그인을 할 때 비밀번호의 길이는 30이하다.")
    @WithMockUser
    @Test
    void authorizeWithPasswordNotInRange2() throws Exception {
        // given
        LoginRequest request =
                createLoginRequest("duswo0624@naver.com", "0123456789012345678901234567891");

        // when // then
        mockMvc.perform(
                        post("/api/authenticate")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.apiError.message").value("패스워드의 길이는 3이상 30이하 입니다."))
                .andExpect(jsonPath("$.apiError.status").value(1000))
        ;
    }

    @DisplayName("refresh token이 유효하다면 새로운 access token을 받는다.")
    @WithMockUser
    @Test
    void refreshWithValidRefreshToken() throws Exception {
        // given
        given(tokenProvider.validateRefreshToken(any())).willReturn(true);

        AuthenticationManager mockAuthenticationManager = mock(AuthenticationManager.class);
        Authentication authentication = mock(Authentication.class);

        given(authenticationManagerBuilder.getObject()).willReturn(mockAuthenticationManager);
        given(mockAuthenticationManager.authenticate(ArgumentMatchers.any(UsernamePasswordAuthenticationToken.class))).willReturn(authentication);

        String accessToken = "new-access-token";
        when(tokenProvider.createAccessToken(authentication)).thenReturn(accessToken);

        RefreshRequest refreshRequest = createRefreshRequest("duswo0624@naver.com", "1234", "valid-refresh-token");

        // when // then
        mockMvc.perform(
                        post("/api/refresh")
                                .content(objectMapper.writeValueAsString(refreshRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.response.accessToken").value(accessToken))
        ;
    }

    @DisplayName("refresh token이 유효하지 않다면 에러 메시지를 클라이언트에게 제공한다.")
    @WithMockUser
    @Test
    void refreshWithInvalidRefreshToken() throws Exception {
        // given
        given(tokenProvider.validateRefreshToken(any())).willThrow(IllegalArgumentException.class);

        RefreshRequest refreshRequest = createRefreshRequest("duswo0624@naver.com", "1234", "invalid-refresh-token");

        // when // then
        mockMvc.perform(
                        post("/api/refresh")
                                .content(objectMapper.writeValueAsString(refreshRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.apiError.message").value("Refresh Token이 유효하지 않습니다."))
                .andExpect(jsonPath("$.apiError.status").value(1000))
        ;
    }

    @DisplayName("refresh token의 유효성을 검증할 때 이메일은 필수값이다.")
    @WithMockUser
    @Test
    void checkRefreshTokenWithoutEamil() throws Exception {
        // given
        RefreshRequest request = createRefreshRequest(null, "1234", "valid-refresh-token");

        // when // then
        mockMvc.perform(
                        post("/api/refresh")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.apiError.message").value("이메일은 필수입니다."))
                .andExpect(jsonPath("$.apiError.status").value(1000))
        ;
    }

    @DisplayName("refresh token의 유효성을 검증할 때 이메일은 이메일의 형식에 따라야 한다.")
    @WithMockUser
    @Test
    void checkRefreshTokenWithWrongEmailFormat() throws Exception {
        // given
        RefreshRequest request = createRefreshRequest("emailNotFollowEmailFormat", "1234", "valid-refresh-token");

        // when // then
        mockMvc.perform(
                        post("/api/refresh")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.apiError.message").value("이메일 형식에 맞지 않습니다."))
                .andExpect(jsonPath("$.apiError.status").value(1000))
        ;
    }

    @DisplayName("refresh token의 유효성을 검증할 때 비밀번호는 필수값이다.")
    @WithMockUser
    @Test
    void checkRefreshTokenWithoutPassword() throws Exception {
        // given
        RefreshRequest request = createRefreshRequest("duswo0624@naver.com", null, "valid-refresh-token");

        // when // then
        mockMvc.perform(
                        post("/api/refresh")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.apiError.message").value("패스워드는 필수입니다."))
                .andExpect(jsonPath("$.apiError.status").value(1000))
        ;
    }

    @DisplayName("refresh token의 유효성을 검증할 때 비밀번호의 길이는 3이상이다.")
    @WithMockUser
    @Test
    void checkRefreshTokenWithPasswordNotInRange1() throws Exception {
        // given
        RefreshRequest request = createRefreshRequest("duswo0624@naver.com", "12", "valid-refresh-token");

        // when // then
        mockMvc.perform(
                        post("/api/refresh")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.apiError.message").value("패스워드의 길이는 3이상 30이하 입니다."))
                .andExpect(jsonPath("$.apiError.status").value(1000))
        ;
    }

    @DisplayName("refresh token의 유효성을 검증할 때 비밀번호의 길이는 30이하다.")
    @WithMockUser
    @Test
    void checkRefreshTokenWithPasswordNotInRange2() throws Exception {
        // given
        RefreshRequest request = createRefreshRequest("duswo0624@naver.com", "0123456789012345678901234567891", "valid-refresh-token");

        // when // then
        mockMvc.perform(
                        post("/api/refresh")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.apiError.message").value("패스워드의 길이는 3이상 30이하 입니다."))
                .andExpect(jsonPath("$.apiError.status").value(1000))
        ;
    }

    @DisplayName("refresh token의 유효성을 검증할 때 refreshToken은 필수값이다.")
    @WithMockUser
    @Test
    void checkRefreshTokenWithoutRefreshToken() throws Exception {
        // given
        RefreshRequest request = createRefreshRequest("duswo0624@naver.com", "1234", null);

        // when // then
        mockMvc.perform(
                        post("/api/refresh")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.apiError.message").value("refresh token은 필수입니다."))
                .andExpect(jsonPath("$.apiError.status").value(1000))
        ;
    }

    private LoginRequest createLoginRequest(String email, String password) {
        return LoginRequest.builder()
                .email(email)
                .password(password)
                .build();
    }

    private RefreshRequest createRefreshRequest(String email, String password, String refreshToken) {
        return RefreshRequest.builder()
                .email(email)
                .password(password)
                .refreshToken(refreshToken)
                .build();
    }
}