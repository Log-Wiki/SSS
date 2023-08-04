package com.logwiki.specialsurveyservice.api.service.surveyresult;

import com.logwiki.specialsurveyservice.IntegrationTestSupport;
import com.logwiki.specialsurveyservice.api.service.account.AccountService;
import com.logwiki.specialsurveyservice.api.service.account.request.AccountCreateServiceRequest;
import com.logwiki.specialsurveyservice.domain.accountcode.AccountCodeType;
import com.logwiki.specialsurveyservice.domain.authority.Authority;
import com.logwiki.specialsurveyservice.domain.authority.AuthorityRepository;
import com.logwiki.specialsurveyservice.domain.authority.AuthorityType;
import com.logwiki.specialsurveyservice.domain.survey.Survey;
import com.logwiki.specialsurveyservice.domain.survey.SurveyRepository;
import com.logwiki.specialsurveyservice.domain.surveycategory.SurveyCategory;
import com.logwiki.specialsurveyservice.domain.surveycategory.SurveyCategoryType;
import com.logwiki.specialsurveyservice.domain.targetnumber.TargetNumber;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class SurveyResultServiceTest extends IntegrationTestSupport {

    @Autowired
    private AccountService accountService;
    @Autowired
    private SurveyResultService surveyResultService;
    @Autowired
    private SurveyRepository surveyRepository;
    @Autowired
    private AuthorityRepository authorityRepository;

    @Disabled
    @DisplayName("'설문 번호, 회원 이메일, 작성 시간'을 이용하여 설문 응답 결과를 제출한다.")
    @WithMockUser(username = "duswo0624@naver.com")
    @Test
    void addSubmitResult() {
        // given
        setAuthority();
        String email = "duswo0624@naver.com";
        AccountCreateServiceRequest accountCreateServiceRequest = AccountCreateServiceRequest.builder()
                .email(email)
                .password("1234")
                .gender(AccountCodeType.MAN)
                .age(AccountCodeType.TWENTIES)
                .name("최연재")
                .phoneNumber("010-1234-5678")
                .birthday(LocalDate.of(1997, 6, 24))
                .build();
        accountService.signup(accountCreateServiceRequest);


        SurveyCategory surveyCategory = SurveyCategory.builder()
                .type(SurveyCategoryType.NORMAL)
                .build();
        Survey survey = Survey.builder()
                .title("당신은 어떤 과일을 좋아하십니까?")
                .startTime(LocalDateTime.now().minusDays(1))
                .endTime(LocalDateTime.now().plusDays(3))
                .headCount(50)
                .closedHeadCount(100)
                .writer(1L)
                .type(surveyCategory)
                .build();
        survey.toOpen();

        TargetNumber targetNumber = TargetNumber.builder()
                .number(3)
                .survey(survey)
                .build();
        survey.addTargetNumbers(List.of(targetNumber));
        surveyRepository.save(survey);

        int submitOrder = surveyResultService.createSubmitOrderIn(survey.getId());
        LocalDateTime writeDateTime = LocalDateTime.now();

        // when
//        SurveyResultResponse surveyResultResponse = surveyResultService.addSubmitResult(
//                survey.getId(), email, writeDateTime);
//
//        // then
//        assertThat(surveyResultResponse)
//                .extracting("endTime", "submitOrder")
//                .contains(writeDateTime, submitOrder);
//        assertThat(surveyResultResponse.getSurvey()).isEqualTo(survey);
//        assertThat(surveyResultResponse.getAccount().getEmail()).isEqualTo(email);
    }

    @DisplayName("특정 설문에 부여해야할 설문 응답 번호를 받는다.")
    @WithMockUser(username = "duswo0624@naver.com")
    @TestFactory
    Collection<DynamicTest> createSubmitOrderIn() {
        // given
        setAuthority();
        String email = "duswo0624@naver.com";
        AccountCreateServiceRequest accountCreateServiceRequest = AccountCreateServiceRequest.builder()
                .email(email)
                .password("1234")
                .gender(AccountCodeType.MAN)
                .age(AccountCodeType.TWENTIES)
                .name("최연재")
                .phoneNumber("010-1234-5678")
                .birthday(LocalDate.of(1997, 6, 24))
                .build();
        accountService.signup(accountCreateServiceRequest);

        SurveyCategory surveyCategory = SurveyCategory.builder()
                .type(SurveyCategoryType.NORMAL)
                .build();

        Survey survey = Survey.builder()
                .title("당신은 어떤 과일을 좋아하십니까?")
                .startTime(LocalDateTime.now().minusDays(1))
                .endTime(LocalDateTime.now().plusDays(3))
                .headCount(50)
                .closedHeadCount(100)
                .writer(1L)
                .type(surveyCategory)
                .build();
        survey.toOpen();

        TargetNumber targetNumber = TargetNumber.builder()
                .number(3)
                .survey(survey)
                .build();
        survey.addTargetNumbers(List.of(targetNumber));
        surveyRepository.save(survey);

        return List.of(
                // when // then
                DynamicTest.dynamicTest("첫 응답자일 경우 응답 번호는 1번이다.", () -> {
                    assertThat(surveyResultService.createSubmitOrderIn(survey.getId())).isEqualTo(1);
                    surveyResultService.addSubmitResult(survey.getId(), LocalDateTime.now());
                }),
                // when // then
                DynamicTest.dynamicTest("두 번째 응답자일 경우 응답 번호는 2번이다.", () -> {
                    assertThat(surveyResultService.createSubmitOrderIn(survey.getId())).isEqualTo(2);
                })
        );
    }

    private void setAuthority() {
        Authority userAuthority = Authority.builder()
                .type(AuthorityType.ROLE_USER)
                .build();

        authorityRepository.save(userAuthority);
    }
}