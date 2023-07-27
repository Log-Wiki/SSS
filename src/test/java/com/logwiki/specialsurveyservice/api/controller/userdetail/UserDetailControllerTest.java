package com.logwiki.specialsurveyservice.api.controller.userdetail;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.logwiki.specialsurveyservice.ControllerTestSupport;
import com.logwiki.specialsurveyservice.api.service.userdetail.response.UserDetailResponse;
import com.logwiki.specialsurveyservice.domain.accountcode.AccountCodeType;
import java.time.LocalDate;
import java.time.Month;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

class UserDetailControllerTest extends ControllerTestSupport {

    @DisplayName("로그인 된 자신의 정보를 가져온다.")
    @WithMockUser
    @Test
    void getMyUserInfo() throws Exception {
        // given
        String email = "duswo0624@naver.com";
        AccountCodeType gender = AccountCodeType.MAN;
        AccountCodeType age = AccountCodeType.TWENTIES;
        String name = "최연재";
        String phoneNumber = "010-1234-5678";
        int responseSurveyCount = 3;
        int createSurveyCount = 5;
        int winningGiveawayCount = 1;
        int point = 10;
        LocalDate birthday = LocalDate.of(1997, Month.JUNE, 24);

        UserDetailResponse userDetailResponse = UserDetailResponse.builder()
                .email(email)
                .gender(gender)
                .age(age)
                .name(name)
                .phoneNumber(phoneNumber)
                .responseSurveyCount(responseSurveyCount)
                .createSurveyCount(createSurveyCount)
                .winningGiveawayCount(winningGiveawayCount)
                .point(point)
                .birthday(birthday)
                .build();

        when(userDetailService.getMyUserWithAuthorities()).thenReturn(userDetailResponse);

        // when // then
        mockMvc.perform(
                        get("/api/user")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.response.email").value(email))
                .andExpect(jsonPath("$.response.gender").value(gender.toString()))
                .andExpect(jsonPath("$.response.name").value(name))
                .andExpect(jsonPath("$.response.phoneNumber").value(phoneNumber))
                .andExpect(jsonPath("$.response.responseSurveyCount").value(responseSurveyCount))
                .andExpect(jsonPath("$.response.createSurveyCount").value(createSurveyCount))
                .andExpect(jsonPath("$.response.winningGiveawayCount").value(winningGiveawayCount))
                .andExpect(jsonPath("$.response.point").value(point))
                .andExpect(jsonPath("$.response.birthday").value(birthday.toString()))
        ;
    }

    @DisplayName("관리자가 다른 유저의 이메일을 이용하여 유저 정보를 가져온다.")
    @WithMockUser
    @Test
    void getUserInfo() throws Exception {
        // given
        String email = "duswo0624@naver.com";
        AccountCodeType gender = AccountCodeType.MAN;
        AccountCodeType age = AccountCodeType.TWENTIES;
        String name = "최연재";
        String phoneNumber = "010-1234-5678";
        int responseSurveyCount = 3;
        int createSurveyCount = 5;
        int winningGiveawayCount = 1;
        int point = 10;
        LocalDate birthday = LocalDate.of(1997, Month.JUNE, 24);

        UserDetailResponse userDetailResponse = UserDetailResponse.builder()
                .email(email)
                .gender(gender)
                .age(age)
                .name(name)
                .phoneNumber(phoneNumber)
                .responseSurveyCount(responseSurveyCount)
                .createSurveyCount(createSurveyCount)
                .winningGiveawayCount(winningGiveawayCount)
                .point(point)
                .birthday(birthday)
                .build();

        when(userDetailService.getUserWithAuthorities(email)).thenReturn(userDetailResponse);

        // when // then
        mockMvc.perform(
                        get("/api/user/{email}", email)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.response.email").value(email))
                .andExpect(jsonPath("$.response.gender").value(gender.toString()))
                .andExpect(jsonPath("$.response.name").value(name))
                .andExpect(jsonPath("$.response.phoneNumber").value(phoneNumber))
                .andExpect(jsonPath("$.response.responseSurveyCount").value(responseSurveyCount))
                .andExpect(jsonPath("$.response.createSurveyCount").value(createSurveyCount))
                .andExpect(jsonPath("$.response.winningGiveawayCount").value(winningGiveawayCount))
                .andExpect(jsonPath("$.response.point").value(point))
                .andExpect(jsonPath("$.response.birthday").value(birthday.toString()))
        ;
    }
}