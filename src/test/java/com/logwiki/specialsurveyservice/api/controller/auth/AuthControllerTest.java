package com.logwiki.specialsurveyservice.api.controller.auth;

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
                createLoginDto(null, "1234");

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
                createLoginDto("emailNotFollowEmailFormat", "1234");

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
                createLoginDto("duswo0624@naver.com", null);

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
                createLoginDto("duswo0624@naver.com", "12");

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
                createLoginDto("duswo0624@naver.com", "0123456789012345678901234567891");

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

    private LoginRequest createLoginDto(String email, String password) {
        return LoginRequest.builder()
                .email(email)
                .password(password)
                .build();
    }
}