package com.logwiki.specialsurveyservice.api.controller.surveyresult;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.logwiki.specialsurveyservice.ControllerTestSupport;
import com.logwiki.specialsurveyservice.api.controller.surveyresult.response.SurveyResultResponse;
import com.logwiki.specialsurveyservice.api.service.surveyresult.response.ResultPageResponse;
import com.logwiki.specialsurveyservice.api.service.surveyresult.response.WinningAccountResponse;
import com.logwiki.specialsurveyservice.domain.accountcode.AccountCodeType;
import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

class SurveyResultControllerTest extends ControllerTestSupport {

    @DisplayName("응답한 설문의 당첨 여부 정보를 받는다.")
    @WithMockUser
    @Test
    void getAnsweredSurveys() throws Exception {
        boolean isWin = true;
        GiveawayType giveawayType = GiveawayType.COFFEE;
        String giveawayName = "스타벅스 아이스 아메리카노";
        ResultPageResponse resultPageResponse = ResultPageResponse
                .builder()
                .isWin(isWin)
                .giveawayType(giveawayType)
                .giveawayName(giveawayName)
                .probability(10D)
                .build();

        Long surveyId = 1L;
        when(surveyResultService.getSurveyResult(surveyId)).thenReturn(resultPageResponse);

        mockMvc.perform(
                        get("/api/user/survey/{surveyId}", surveyId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.response.win").value(isWin))
                .andExpect(jsonPath("$.response.giveawayType").value(giveawayType.toString()))
                .andExpect(jsonPath("$.response.giveawayName").value(giveawayName));
    }

    @DisplayName("당첨 상품을 처음 확인할 때 당첨 상품의 확인 여부 필드값을 참으로 바꾼다.")
    @WithMockUser
    @Test
    void patchSurveyResult() throws Exception {

        Long surveyResultId = 1L;
        boolean isWin = true;
        LocalDateTime answerDateTime = LocalDateTime.now().minusDays(1);
        int submitOrder = 10;
        boolean userCheck = true;
        SurveyResultResponse surveyResultResponse = SurveyResultResponse
                .builder()
                .id(surveyResultId)
                .win(isWin)
                .answerDateTime(answerDateTime)
                .submitOrder(submitOrder)
                .userCheck(userCheck)
                .build();

        Long surveyId = 1L;
        when(surveyResultService.patchSurveyResult(surveyId)).thenReturn(surveyResultResponse);

        mockMvc.perform(
                        patch("/api/user/survey-result/check/{surveyId}", surveyId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.response.id").value(surveyResultId))
                .andExpect(jsonPath("$.response.win").value(isWin))
                .andExpect(jsonPath("$.response.submitOrder").value(submitOrder))
                .andExpect(jsonPath("$.response.userCheck").value(userCheck));
    }

    @DisplayName("설문 ID를 이용하여 당첨자 목록을 조회한다.")
    @WithMockUser
    @Test
    void getWinningUsers() throws Exception {

        String winningAccountEmail1 = "duswo0624@naver.com";
        WinningAccountResponse winningAccountResponse1 = WinningAccountResponse
                .builder()
                .email(winningAccountEmail1)
                .gender(AccountCodeType.MAN)
                .age(AccountCodeType.TWENTIES)
                .name("최연재")
                .phoneNumber("010-1111-2222")
                .responseSurveyCount(10)
                .createSurveyCount(2)
                .winningGiveawayCount(3)
                .point(0)
                .birthday(LocalDate.of(1997, 6, 24))
                .build();

        String winningAccountEmail2 = "kwon1111@naver.com";
        WinningAccountResponse winningAccountResponse2 = WinningAccountResponse
                .builder()
                .email(winningAccountEmail2)
                .gender(AccountCodeType.MAN)
                .age(AccountCodeType.TWENTIES)
                .name("근짱")
                .phoneNumber("010-2222-3333")
                .responseSurveyCount(10)
                .createSurveyCount(2)
                .winningGiveawayCount(3)
                .point(0)
                .birthday(LocalDate.of(1997, 6, 24))
                .build();

        Long surveyId = 1L;
        when(surveyResultService.getWinningUsers(surveyId)).thenReturn(
                List.of(winningAccountResponse1, winningAccountResponse2));

        mockMvc.perform(
                        get("/api/survey-result/users/{surveyId}", surveyId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.response[0].email").value(winningAccountEmail1))
                .andExpect(jsonPath("$.response[1].email").value(winningAccountEmail2));
    }
}