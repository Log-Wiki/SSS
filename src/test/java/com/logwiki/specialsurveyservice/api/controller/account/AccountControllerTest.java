package com.logwiki.specialsurveyservice.api.controller.account;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.logwiki.specialsurveyservice.ControllerTestSupport;
import com.logwiki.specialsurveyservice.api.controller.account.request.AccountCreateRequest;
import com.logwiki.specialsurveyservice.api.controller.account.request.AccountUpdateRequest;
import com.logwiki.specialsurveyservice.api.controller.account.request.CheckDuplicateEmailRequest;
import com.logwiki.specialsurveyservice.api.controller.account.request.CheckDuplicatePhoneNumberRequest;
import com.logwiki.specialsurveyservice.api.service.account.response.AccountResponse;
import com.logwiki.specialsurveyservice.api.service.account.response.DuplicateResponse;
import com.logwiki.specialsurveyservice.domain.accountcode.AccountCodeType;
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
        AccountCodeType gender = AccountCodeType.MAN;
        AccountCodeType age = AccountCodeType.TWENTIES;
        String name = "최연재";
        String phoneNumber = "010-3499-4698";
        LocalDate birthday = LocalDate.of(1997, Month.JUNE, 24);

        AccountResponse accountResponse = AccountResponse.builder()
                .email(email)
                .gender(gender)
                .age(age)
                .name(name)
                .phoneNumber(phoneNumber)
                .birthday(birthday)
                .build();

        when(accountService.signup(any())).thenReturn(accountResponse);

        AccountCreateRequest request =
                createAccountCreateRequest(email, password, gender, age, name, phoneNumber,
                        birthday);

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
                .andExpect(jsonPath("$.response.gender").value(gender.toString()))
                .andExpect(jsonPath("$.response.name").value(name))
                .andExpect(jsonPath("$.response.phoneNumber").value(phoneNumber))
                .andExpect(jsonPath("$.response.birthday").value(birthday.toString()))
        ;
    }

    @DisplayName("회원가입을 할 때 이메일은 필수값이다.")
    @WithMockUser
    @Test
    void signupWithoutEmail() throws Exception {
        // given
        AccountCreateRequest request =
                createAccountCreateRequest(null, "1234", AccountCodeType.MAN,
                        AccountCodeType.TWENTIES, "최연재", "010-3499-4698",
                        LocalDate.of(1997, 6, 24));

        // when // then
        mockMvc.perform(
                        post("/api/signup")
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

    @DisplayName("회원가입을 할 때 이메일은 이메일의 형식을 따라야한다.")
    @WithMockUser
    @Test
    void signupWithWrongEmailFormat() throws Exception {
        // given
        AccountCreateRequest request =
                createAccountCreateRequest("emailNotFollowEmailFormat", "1234", AccountCodeType.MAN,
                        AccountCodeType.TWENTIES, "최연재", "010-3499-4698",
                        LocalDate.of(1997, 6, 24));

        // when // then
        mockMvc.perform(
                        post("/api/signup")
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

    @DisplayName("회원가입을 할 때 패스워드는 필수값이다.")
    @WithMockUser
    @Test
    void signupWithoutPassword() throws Exception {
        // given
        AccountCreateRequest request =
                createAccountCreateRequest("duswo0624@naver.com", null, AccountCodeType.MAN,
                        AccountCodeType.TWENTIES, "최연재", "010-3499-4698",
                        LocalDate.of(1997, 6, 24));

        // when // then
        mockMvc.perform(
                        post("/api/signup")
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

    @DisplayName("회원가입을 할 때 패스워드의 길이는 3이상 이다.")
    @WithMockUser
    @Test
    void signupWithPasswordNotInRange1() throws Exception {
        // given
        AccountCreateRequest request =
                createAccountCreateRequest("duswo0624@naver.com", "12", AccountCodeType.MAN,
                        AccountCodeType.TWENTIES, "최연재", "010-3499-4698",
                        LocalDate.of(1997, 6, 24));

        // when // then
        mockMvc.perform(
                        post("/api/signup")
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

    @DisplayName("회원가입을 할 때 패스워드의 길이는 30이하다.")
    @WithMockUser
    @Test
    void signupWithPasswordNotInRange2() throws Exception {
        // given
        AccountCreateRequest request =
                createAccountCreateRequest("duswo0624@naver.com", "0123456789012345678901234567891",
                        AccountCodeType.MAN, AccountCodeType.TWENTIES, "최연재", "010-3499-4698",
                        LocalDate.of(1997, 6, 24));

        // when // then
        mockMvc.perform(
                        post("/api/signup")
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

    @DisplayName("회원가입을 할 때 성별은 필수값이다.")
    @WithMockUser
    @Test
    void signupWithoutgender() throws Exception {
        // given
        AccountCreateRequest request =
                createAccountCreateRequest("duswo0624@naver.com", "1234", null,
                        AccountCodeType.TWENTIES, "최연재", "010-3499-4698",
                        LocalDate.of(1997, 6, 24));

        // when // then
        mockMvc.perform(
                        post("/api/signup")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.apiError.message").value("성별은 필수입니다."))
                .andExpect(jsonPath("$.apiError.status").value(1000))
        ;
    }

    @DisplayName("회원가입을 할 때 이름은 필수값이다.")
    @WithMockUser
    @Test
    void signupWithoutName() throws Exception {
        // given
        AccountCreateRequest request =
                createAccountCreateRequest("duswo0624@naver.com", "1234", AccountCodeType.MAN,
                        AccountCodeType.TWENTIES, null, "010-3499-4698", LocalDate.of(1997, 6, 24));

        // when // then
        mockMvc.perform(
                        post("/api/signup")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.apiError.message").value("이름은 필수입니다."))
                .andExpect(jsonPath("$.apiError.status").value(1000))
        ;
    }

    @DisplayName("회원가입을 할 때 휴대폰 번호는 필수값이다.")
    @WithMockUser
    @Test
    void signupWithoutPhoneNumber() throws Exception {
        // given
        AccountCreateRequest request =
                createAccountCreateRequest("duswo0624@naver.com", "1234", AccountCodeType.MAN,
                        AccountCodeType.TWENTIES, "최연재", null, LocalDate.of(1997, 6, 24));

        // when // then
        mockMvc.perform(
                        post("/api/signup")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.apiError.message").value("휴대폰 번호는 필수입니다."))
                .andExpect(jsonPath("$.apiError.status").value(1000))
        ;
    }

    @DisplayName("회원가입을 할 때 휴대폰 번호는 10~11자리의 숫자만 입력 가능하다.")
    @WithMockUser
    @Test
    void signupWithPhoneNumberNotInRange() throws Exception {
        // given
        AccountCreateRequest request =
                createAccountCreateRequest("duswo0624@naver.com", "1234", AccountCodeType.MAN,
                        AccountCodeType.TWENTIES, "최연재", "010-1234-56789",
                        LocalDate.of(1997, 6, 24));

        // when // then
        mockMvc.perform(
                        post("/api/signup")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.apiError.message").value("휴대폰 번호는 10~11자리의 숫자만 입력가능합니다."))
                .andExpect(jsonPath("$.apiError.status").value(1000))
        ;
    }

    @DisplayName("회원가입을 할 때 출생년도는 필수값이다.")
    @WithMockUser
    @Test
    void signupWithoutBirthday() throws Exception {
        // given
        AccountCreateRequest request =
                createAccountCreateRequest("duswo0624@naver.com", "1234", AccountCodeType.MAN,
                        AccountCodeType.TWENTIES, "최연재", "010-1234-5678", null);

        // when // then
        mockMvc.perform(
                        post("/api/signup")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.apiError.message").value("출생년도는 필수입니다."))
                .andExpect(jsonPath("$.apiError.status").value(1000))
        ;
    }

    @DisplayName("회원 정보 수정 시 패스워드, 핸드폰 번호, 이름을 수정할 수 있다.")
    @WithMockUser()
    @Test
    void updateAccount() throws Exception {
        String updatePhoneNumber = "010-9999-8888";
        String updateName = "최연습";
        AccountUpdateRequest accountUpdateRequest = AccountUpdateRequest
                .builder()
                .password("updatePassword1234")
                .phoneNumber(updatePhoneNumber)
                .name(updateName)
                .build();

        AccountResponse accountResponse = AccountResponse
                .builder()
                .id(1L)
                .email("duswo0624@naver.com")
                .gender(AccountCodeType.MAN)
                .age(AccountCodeType.TWENTIES)
                .name(updateName)
                .phoneNumber(updatePhoneNumber)
                .responseSurveyCount(10)
                .createSurveyCount(10)
                .winningGiveawayCount(2)
                .point(10)
                .birthday(LocalDate.of(1997, 6, 24))
                .refreshToken("refreshToken")
                .build();

        when(accountService.updateAccount(any())).thenReturn(accountResponse);

        // when // then
        mockMvc.perform(
                        patch("/api/user")
                                .content(objectMapper.writeValueAsString(accountUpdateRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.response.name").value(updateName))
                .andExpect(jsonPath("$.response.phoneNumber").value(updatePhoneNumber))
        ;
    }

    @DisplayName("회원을 삭제하면 삭제된 회원의 정보를 받는다.")
    @WithMockUser()
    @Test
    void deleteAccount() throws Exception {
        String name = "최연재";
        String email = "duswo0624@naver.com";
        AccountResponse accountResponse = AccountResponse
                .builder()
                .id(1L)
                .email(email)
                .gender(AccountCodeType.MAN)
                .age(AccountCodeType.TWENTIES)
                .name(name)
                .phoneNumber("010-1111-2222")
                .responseSurveyCount(10)
                .createSurveyCount(10)
                .winningGiveawayCount(2)
                .point(10)
                .birthday(LocalDate.of(1997, 6, 24))
                .refreshToken("refreshToken")
                .build();

        when(accountService.deleteAccount()).thenReturn(accountResponse);

        // when // then
        mockMvc.perform(
                        delete("/api/user")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.response.name").value(name))
                .andExpect(jsonPath("$.response.email").value(email))
        ;
    }

    @DisplayName("이메일 중복 확인 기능을 이용하여 이메일의 중복 확인 여부를 응답 받는다.")
    @WithMockUser
    @Test
    void checkDuplicateEmail() throws Exception {
        CheckDuplicateEmailRequest checkDuplicateEmailRequest = CheckDuplicateEmailRequest.builder()
                .email("duswo0624@naver.com")
                .build();

        boolean isDuplicate = true;
        DuplicateResponse duplicateResponse = DuplicateResponse
                .builder()
                .duplicate(isDuplicate)
                .build();

        when(accountService.checkDuplicateEmail(checkDuplicateEmailRequest.getEmail())).thenReturn(duplicateResponse);

        mockMvc.perform(
                        post("/api/duplicate/email")
                                .content(objectMapper.writeValueAsString(checkDuplicateEmailRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.response.duplicate").value(isDuplicate))
        ;
    }

    @DisplayName("핸드폰 번호 중복 확인 기능을 이용하여 핸드폰 번호의 중복 확인 여부를 응답 받는다.")
    @WithMockUser
    @Test
    void checkDuplicatePhoneNumber() throws Exception {
        CheckDuplicatePhoneNumberRequest checkDuplicatePhoneNumberRequest = CheckDuplicatePhoneNumberRequest.builder()
                .phoneNumber("010-1111-2222")
                .build();

        boolean isDuplicate = true;
        DuplicateResponse duplicateResponse = DuplicateResponse
                .builder()
                .duplicate(isDuplicate)
                .build();

        when(accountService.checkDuplicatePhoneNumber(checkDuplicatePhoneNumberRequest.getPhoneNumber())).thenReturn(duplicateResponse);

        mockMvc.perform(
                        post("/api/duplicate/phone-number")
                                .content(objectMapper.writeValueAsString(checkDuplicatePhoneNumberRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.response.duplicate").value(isDuplicate))
        ;
    }

    private AccountCreateRequest createAccountCreateRequest(String email, String password,
            AccountCodeType gender, AccountCodeType age, String name, String phoneNumber,
            LocalDate birthday) {
        return AccountCreateRequest.builder()
                .email(email)
                .password(password)
                .gender(gender)
                .age(age)
                .name(name)
                .phoneNumber(phoneNumber)
                .birthday(birthday)
                .build();
    }
}