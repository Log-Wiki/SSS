package com.logwiki.specialsurveyservice.api.service.surveyresult;

import com.logwiki.specialsurveyservice.IntegrationTestSupport;
import com.logwiki.specialsurveyservice.api.service.account.AccountService;
import com.logwiki.specialsurveyservice.api.service.account.request.AccountCreateServiceRequest;
import com.logwiki.specialsurveyservice.api.service.surveyresult.response.MyGiveawayResponse;
import com.logwiki.specialsurveyservice.domain.accountcode.AccountCodeType;
import com.logwiki.specialsurveyservice.domain.authority.Authority;
import com.logwiki.specialsurveyservice.domain.authority.AuthorityRepository;
import com.logwiki.specialsurveyservice.domain.authority.AuthorityType;
import com.logwiki.specialsurveyservice.domain.giveaway.Giveaway;
import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayRepository;
import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayType;
import com.logwiki.specialsurveyservice.domain.survey.Survey;
import com.logwiki.specialsurveyservice.domain.survey.SurveyRepository;
import com.logwiki.specialsurveyservice.domain.surveycategory.SurveyCategory;
import com.logwiki.specialsurveyservice.domain.surveycategory.SurveyCategoryType;
import com.logwiki.specialsurveyservice.domain.surveygiveaway.SurveyGiveaway;
import com.logwiki.specialsurveyservice.domain.surveyresult.SurveyResult;
import com.logwiki.specialsurveyservice.domain.surveyresult.SurveyResultRepository;
import com.logwiki.specialsurveyservice.domain.targetnumber.TargetNumber;
import com.logwiki.specialsurveyservice.domain.targetnumber.TargetNumberRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    @Autowired
    private GiveawayRepository giveawayRepository;
    @Autowired
    private SurveyResultRepository surveyResultRepository;
    @Autowired
    private TargetNumberRepository targetNumberRepository;


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
                .writer(accountService.getCurrentAccountBySecurity().getId())
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
                .writer(accountService.getCurrentAccountBySecurity().getId())
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

    @DisplayName("당첨이 되지 않은 상품을 조회할시 확률은 0 프로다.")
    @WithMockUser(username = "duswo0624@naver.com")
    @Test
    void notWinProbabilityZero() {
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

        // 상품 생성
        Giveaway giveaway = Giveaway.builder()
                .giveawayType(GiveawayType.COFFEE)
                .name("커피")
                .price(10000)
                .build();


        Survey survey = Survey.builder()
                .title("당신은 어떤 과일을 좋아하십니까?")
                .startTime(LocalDateTime.now().minusDays(1))
                .endTime(LocalDateTime.now().plusDays(3))
                .headCount(1)
                .closedHeadCount(1)
                .writer(accountService.getCurrentAccountBySecurity().getId())
                .type(surveyCategory)
                .build();
        survey.toClose();

        SurveyGiveaway surveyGiveaway = SurveyGiveaway.builder()
                .giveaway(giveaway)
                .count(1)
                .survey(survey)
                .build();
        List<SurveyGiveaway> surveyGiveaways = new ArrayList<>();
        surveyGiveaways.add(surveyGiveaway);

        survey.addSurveyGiveaways(surveyGiveaways);

        TargetNumber targetNumber = TargetNumber.builder()
                .number(1)
                .survey(survey)
                .giveaway(giveaway)
                .build();
        survey.addTargetNumbers(List.of(targetNumber));

        giveawayRepository.save(giveaway);

        //설문 당첨 상품

        surveyRepository.save(survey);
        survey.getSurveyGiveaways().add(surveyGiveaway);

        // 상품 생성
        SurveyResult surveyResult = SurveyResult.builder()
                .win(false)
                .answerDateTime(LocalDateTime.now())
                .survey(survey)
                .userCheck(false)
                .account(accountService.getCurrentAccountBySecurity())
                .submitOrder(2)
                .build();
        surveyResultRepository.save(surveyResult);

        System.out.println(accountService.getCurrentAccountBySecurity().getId());
        List<MyGiveawayResponse> myGiveawayResponses = surveyResultService.getMyGiveaways();

        double ZERO_PROBABILITY = 0;
        assertThat(myGiveawayResponses.get(0).getProbabilty()).isEqualTo(ZERO_PROBABILITY);
    }


    @DisplayName("당첨이 상품이1개 참여인원 1명 일시에 확률은 100프로다.")
    @WithMockUser(username = "duswo0624@naver.com")
    @Test
    void winProbability100Percent() {
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

        // 상품 생성
        Giveaway giveaway = Giveaway.builder()
                .giveawayType(GiveawayType.COFFEE)
                .name("커피")
                .price(10000)
                .build();


        Survey survey = Survey.builder()
                .title("당신은 어떤 과일을 좋아하십니까?")
                .startTime(LocalDateTime.now().minusDays(1))
                .endTime(LocalDateTime.now().plusDays(3))
                .headCount(1)
                .closedHeadCount(1)
                .writer(accountService.getCurrentAccountBySecurity().getId())
                .type(surveyCategory)
                .build();
        survey.toClose();

        SurveyGiveaway surveyGiveaway = SurveyGiveaway.builder()
                .giveaway(giveaway)
                .count(1)
                .survey(survey)
                .build();
        List<SurveyGiveaway> surveyGiveaways = new ArrayList<>();
        surveyGiveaways.add(surveyGiveaway);

        survey.addSurveyGiveaways(surveyGiveaways);

        TargetNumber targetNumber = TargetNumber.builder()
                .number(1)
                .survey(survey)
                .giveaway(giveaway)
                .build();
        survey.addTargetNumbers(List.of(targetNumber));

        giveawayRepository.save(giveaway);

        //설문 당첨 상품

        surveyRepository.save(survey);
        survey.getSurveyGiveaways().add(surveyGiveaway);

        // 상품 생성
        SurveyResult surveyResult = SurveyResult.builder()
                .win(true)
                .answerDateTime(LocalDateTime.now())
                .survey(survey)
                .userCheck(false)
                .account(accountService.getCurrentAccountBySecurity())
                .submitOrder(1)
                .build();
        surveyResultRepository.save(surveyResult);


        List<MyGiveawayResponse> myGiveawayResponses = surveyResultService.getMyGiveaways();

        double PROBABILITY_100 = 100;
        double a = surveyResultRepository.findByGiveawaySurvey(surveyResult.getSurvey().getId(), surveyResult.getSubmitOrder())
                .orElseGet(() -> 0);

        assertThat(myGiveawayResponses.get(0).getProbabilty()).isEqualTo(PROBABILITY_100);
    }

    private void setAuthority() {
        Authority userAuthority = Authority.builder()
                .type(AuthorityType.ROLE_USER)
                .build();

        authorityRepository.save(userAuthority);
    }
}