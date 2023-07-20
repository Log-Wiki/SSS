package com.logwiki.specialsurveyservice.api.controller.account;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.logwiki.specialsurveyservice.ControllerTestSupport;
import com.logwiki.specialsurveyservice.api.controller.account.request.AccountCreateRequest;
import com.logwiki.specialsurveyservice.api.service.account.response.AccountResponse;
import com.logwiki.specialsurveyservice.domain.sex.Sex;
import java.time.LocalDate;
import java.time.Month;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

class AccountControllerTest extends ControllerTestSupport {

  @DisplayName("이메일, 비밀번호, 성별, 이름, 핸드폰 번호, 출생년도를 이용하여 회원 가입을 한다.")
  @WithMockUser
  @Test
  void signup() throws Exception {
    // given
    String email = "duswo0624@naver.com";
    String password = "1234";
    Sex sex = Sex.MALE;
    String name = "최연재";
    String phoneNumber = "010-3499-4698";
    LocalDate birthday = LocalDate.of(1997, Month.JUNE, 24);

    AccountResponse accountResponse = AccountResponse.builder()
        .email(email)
        .sex(sex)
        .name(name)
        .phoneNumber(phoneNumber)
        .birthday(birthday)
        .build();


    when(signupAccountService.signup(any())).thenReturn(accountResponse);

    AccountCreateRequest request =
        createAccountCreateRequest(email, password, sex, name, phoneNumber, birthday);

    // when // then
    mockMvc.perform(
            post("/api/signup")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value("true"))
        .andExpect(jsonPath("$.response.email").value(email))
        .andExpect(jsonPath("$.response.sex").value(sex.toString()))
        .andExpect(jsonPath("$.response.name").value(name))
        .andExpect(jsonPath("$.response.phoneNumber").value(phoneNumber))
        .andExpect(jsonPath("$.response.birthday").value(birthday.toString()))
    ;
  }

  private AccountCreateRequest createAccountCreateRequest(String email, String password, Sex sex, String name, String phoneNumber, LocalDate birthday) {
    return AccountCreateRequest.builder()
        .email(email)
        .password(password)
        .sex(sex)
        .name(name)
        .phoneNumber(phoneNumber)
        .birthday(birthday)
        .build();
  }
}