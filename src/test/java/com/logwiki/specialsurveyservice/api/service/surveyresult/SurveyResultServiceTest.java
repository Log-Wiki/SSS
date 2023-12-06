package com.logwiki.specialsurveyservice.api.service.surveyresult;

import com.logwiki.specialsurveyservice.IntegrationTestSupport;
import com.logwiki.specialsurveyservice.api.controller.giveaway.request.GiveawayRequest;
import com.logwiki.specialsurveyservice.api.service.account.AccountService;
import com.logwiki.specialsurveyservice.api.service.account.request.AccountCreateServiceRequest;
import com.logwiki.specialsurveyservice.api.service.giveaway.GiveawayService;
import com.logwiki.specialsurveyservice.api.service.giveaway.response.MyGiveawayResponse;
import com.logwiki.specialsurveyservice.api.service.question.QuestionAnswerService;
import com.logwiki.specialsurveyservice.api.service.question.request.QuestionAnswerCreateServiceRequest;
import com.logwiki.specialsurveyservice.api.service.question.request.QuestionCreateServiceRequest;
import com.logwiki.specialsurveyservice.api.service.survey.SurveyService;
import com.logwiki.specialsurveyservice.api.service.survey.request.GiveawayAssignServiceRequest;
import com.logwiki.specialsurveyservice.api.service.survey.request.SurveyCreateServiceRequest;
import com.logwiki.specialsurveyservice.api.service.survey.response.SurveyResponse;
import com.logwiki.specialsurveyservice.api.service.surveyresult.response.WinningAccountResponse;
import com.logwiki.specialsurveyservice.domain.account.Account;
import com.logwiki.specialsurveyservice.domain.account.AccountRepository;
import com.logwiki.specialsurveyservice.domain.accountcode.AccountCode;
import com.logwiki.specialsurveyservice.domain.accountcode.AccountCodeRepository;
import com.logwiki.specialsurveyservice.domain.accountcode.AccountCodeType;
import com.logwiki.specialsurveyservice.domain.authority.Authority;
import com.logwiki.specialsurveyservice.domain.authority.AuthorityRepository;
import com.logwiki.specialsurveyservice.domain.authority.AuthorityType;
import com.logwiki.specialsurveyservice.domain.giveaway.Giveaway;
import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayRepository;
import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayType;
import com.logwiki.specialsurveyservice.domain.questioncategory.QuestionCategoryType;
import com.logwiki.specialsurveyservice.domain.survey.Survey;
import com.logwiki.specialsurveyservice.domain.survey.SurveyRepository;
import com.logwiki.specialsurveyservice.domain.surveycategory.SurveyCategory;
import com.logwiki.specialsurveyservice.domain.surveycategory.SurveyCategoryRepository;
import com.logwiki.specialsurveyservice.domain.surveycategory.SurveyCategoryType;
import com.logwiki.specialsurveyservice.domain.surveygiveaway.SurveyGiveaway;
import com.logwiki.specialsurveyservice.domain.surveyresult.SurveyResult;
import com.logwiki.specialsurveyservice.domain.surveyresult.SurveyResultRepository;
import com.logwiki.specialsurveyservice.domain.targetnumber.TargetNumber;
import com.logwiki.specialsurveyservice.exception.BaseException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    private GiveawayService giveawayService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private SurveyService surveyService;
    @Autowired
    private QuestionAnswerService questionAnswerService;
    @Autowired
    private AccountCodeRepository accountCodeRepository;
    @Autowired
    private SurveyCategoryRepository surveyCategoryRepository;

    @DisplayName("설문 번호와 설문 응답 시간을 이용하여 설문 응답 결과를 제출한다.")
    @WithMockUser(username = "duswo0624@naver.com")
    @Test
    void addSubmitResult() {
        // given
        setAuthority();
        saveAccountYJ("duswo0624@naver.com");

        SurveyCategory surveyCategory = SurveyCategory.builder()
                .type(SurveyCategoryType.NORMAL)
                .build();
        Survey survey = getSurvey(50, 100, surveyCategory);

        TargetNumber targetNumber = TargetNumber.builder()
                .number(3)
                .survey(survey)
                .build();
        survey.addTargetNumbers(List.of(targetNumber));
        surveyRepository.save(survey);

        int submitOrder = surveyResultService.createSubmitOrderIn(survey.getId());
        LocalDateTime answerDateTime = LocalDateTime.now();

        // when
        SurveyResult surveyResult = surveyResultService.addSubmitResult(survey.getId(),
                answerDateTime);

        // then
        assertThat(surveyResult)
                .extracting("answerDateTime", "submitOrder")
                .contains(answerDateTime, submitOrder);
        assertThat(surveyResult.getSurvey()).isEqualTo(survey);
    }

    @DisplayName("설문이 닫혀 있으면 설문 응답 결과를 제출할 수 없다.")
    @WithMockUser(username = "duswo0624@naver.com")
    @Test
    void cannotAddSubmitResultWithClosedSurvey() {
        // given
        setAuthority();
        saveAccountYJ("duswo0624@naver.com");

        SurveyCategory surveyCategory = SurveyCategory.builder()
                .type(SurveyCategoryType.NORMAL)
                .build();
        Survey survey = Survey.builder()
                .title("당신은 어떤 과일을 좋아하십니까?")
                .startTime(LocalDateTime.now().minusDays(1))
                .endTime(LocalDateTime.now().plusDays(3))
                .headCount(0)
                .closedHeadCount(10)
                .writer(accountService.getCurrentAccountBySecurity().getId())
                .type(surveyCategory)
                .build();
        survey.toClose();

        TargetNumber targetNumber = TargetNumber.builder()
                .number(3)
                .survey(survey)
                .build();
        survey.addTargetNumbers(List.of(targetNumber));
        surveyRepository.save(survey);

        LocalDateTime answerDateTime = LocalDateTime.now();

        // when // then
        assertThatThrownBy(() -> surveyResultService.addSubmitResult(survey.getId(), answerDateTime))
                .isInstanceOf(BaseException.class)
                .hasMessage("마감된 설문입니다.");
    }

    @DisplayName("이미 응답한 설문에 설문 응답 결과를 제출할 수 없다.")
    @WithMockUser(username = "duswo0624@naver.com")
    @Test
    void cannotAddSubmitResultToAlreadyAnsweredSurvey() {
        // given
        setAuthority();
        saveAccountYJ("duswo0624@naver.com");

        SurveyCategory surveyCategory = SurveyCategory.builder()
                .type(SurveyCategoryType.NORMAL)
                .build();
        Survey survey = getSurvey(50, 100, surveyCategory);

        TargetNumber targetNumber = TargetNumber.builder()
                .number(3)
                .survey(survey)
                .build();
        survey.addTargetNumbers(List.of(targetNumber));
        surveyRepository.save(survey);

        LocalDateTime answerDateTime = LocalDateTime.now();

        surveyResultService.addSubmitResult(survey.getId(), answerDateTime);

        // when // then
        assertThatThrownBy(() -> surveyResultService.addSubmitResult(survey.getId(), answerDateTime))
                .isInstanceOf(BaseException.class)
                .hasMessage("이미 응답한 설문입니다.");
    }

    @DisplayName("설문에 응답할 때 상품에 당첨되는 경우, 사용자의 설문 당첨 횟수를 증가시키고 설문 응답의 당첨 여부를 참으로 바꾼다.")
    @WithMockUser(username = "duswo0624@naver.com")
    @Test
    void changeWinningGiveawayOfAccountCountAndWinOfSurveyResult() {
        // given
        setAuthority();
        saveAccountYJ("duswo0624@naver.com");

        SurveyCategory surveyCategory = SurveyCategory.builder()
                .type(SurveyCategoryType.INSTANT_WIN)
                .build();
        Survey survey = getSurvey(0, 1, surveyCategory);

        TargetNumber targetNumber = TargetNumber.builder()
                .number(1)
                .survey(survey)
                .build();
        survey.addTargetNumbers(List.of(targetNumber));
        surveyRepository.save(survey);

        LocalDateTime answerDateTime = LocalDateTime.now();

        surveyResultService.addSubmitResult(survey.getId(), answerDateTime);

        // when // then
        Optional<Account> account = accountRepository.findOneWithAuthoritiesByEmail(
                "duswo0624@naver.com");
        assertThat(account.get().getWinningGiveawayCount()).isOne();
        SurveyResult surveyResult = surveyResultRepository.findSurveyResultBySurvey_IdAndAccount_Id(
                survey.getId(), account.get().getId());
        assertThat(surveyResult.isWin()).isTrue();
    }

    @DisplayName("설문ID가 올바르지 않은 경우 설문 응답 결과를 제출할 수 없다.")
    @WithMockUser(username = "duswo0624@naver.com")
    @Test
    void cannotAddSubmitResultWithInvalidSurveyId() {
        // given
        setAuthority();
        saveAccountYJ("duswo0624@naver.com");

        SurveyCategory surveyCategory = SurveyCategory.builder()
                .type(SurveyCategoryType.NORMAL)
                .build();
        Survey survey = getSurvey(50, 100, surveyCategory);

        TargetNumber targetNumber = TargetNumber.builder()
                .number(3)
                .survey(survey)
                .build();
        survey.addTargetNumbers(List.of(targetNumber));
        surveyRepository.save(survey);

        LocalDateTime answerDateTime = LocalDateTime.now();

        surveyResultService.addSubmitResult(survey.getId(), answerDateTime);
        Long invalidSurveyId = survey.getId() + 1L;
        // when // then
        assertThatThrownBy(() -> surveyResultService.addSubmitResult(invalidSurveyId, answerDateTime))
                .isInstanceOf(BaseException.class)
                .hasMessage("설문조사 PK가 올바르지 않습니다.");
    }

    @DisplayName("특정 설문에 부여해야할 설문 응답 번호를 받는다.")
    @WithMockUser(username = "duswo0624@naver.com")
    @TestFactory
    Collection<DynamicTest> createSubmitOrderIn() {
        // given
        setAuthority();
        saveAccountYJ("duswo0624@naver.com");


        SurveyCategory surveyCategory = SurveyCategory.builder()
                .type(SurveyCategoryType.NORMAL)
                .build();

        Survey survey = getSurvey(50, 100, surveyCategory);


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
        saveAccountYJ("duswo0624@naver.com");
        // 카테고리 생성
        SurveyCategory surveyCategory = SurveyCategory.builder()
                .type(SurveyCategoryType.NORMAL)
                .build();

        // 상품 생성
        Giveaway giveaway = Giveaway.builder()
                .giveawayType(GiveawayType.COFFEE)
                .name("커피")
                .price(10000)
                .build();

        Survey survey = getSurvey(1, 1, surveyCategory);
        survey.toClose();

        SurveyGiveaway surveyGiveaway = SurveyGiveaway.builder()
                .giveaway(giveaway)
                .count(1)
                .survey(survey)
                .build();
        survey.addSurveyGiveaways(List.of(surveyGiveaway));

        TargetNumber targetNumber = TargetNumber.builder()
                .number(1)
                .survey(survey)
                .giveaway(giveaway)
                .build();
        survey.addTargetNumbers(List.of(targetNumber));

        giveawayRepository.save(giveaway);

        //설문 당첨 상품
        surveyRepository.save(survey);

        // 상품 생성
        SurveyResult surveyResult = getSurveyResult(survey, 2, true);

        List<MyGiveawayResponse> myGiveawayResponses = giveawayService.getMyGiveaways();

        double ZERO_PROBABILITY = 0;
        assertThat(myGiveawayResponses.get(0).getProbabilty()).isEqualTo(ZERO_PROBABILITY);
    }


    @DisplayName("당첨이 상품이1개 참여인원 1명 일시에 확률은 100프로다.")
    @WithMockUser(username = "duswo0624@naver.com")
    @Test
    void winProbability100Percent1() {
        // given
        setAuthority();
        saveAccountYJ("duswo0624@naver.com");

        SurveyCategory surveyCategory = SurveyCategory.builder()
                .type(SurveyCategoryType.NORMAL)
                .build();

        // 상품 생성
        Giveaway giveaway = Giveaway.builder()
                .giveawayType(GiveawayType.COFFEE)
                .name("커피")
                .price(10000)
                .build();
        Survey survey = getSurvey(1, 1, surveyCategory);
        survey.toClose();

        SurveyGiveaway surveyGiveaway = SurveyGiveaway.builder()
                .giveaway(giveaway)
                .count(1)
                .survey(survey)
                .build();

        survey.addSurveyGiveaways(List.of(surveyGiveaway));

        TargetNumber targetNumber = TargetNumber.builder()
                .number(1)
                .survey(survey)
                .giveaway(giveaway)
                .build();
        survey.addTargetNumbers(List.of(targetNumber));

        giveawayRepository.save(giveaway);

        //설문 당첨 상품

        surveyRepository.save(survey);

        // 상품 생성
        SurveyResult surveyResult = getSurveyResult(survey, 1, true);

        List<MyGiveawayResponse> myGiveawayResponses = giveawayService.getMyGiveaways();

        double PROBABILITY_100 = 100;

        assertThat(myGiveawayResponses.get(0).getProbabilty()).isEqualTo(PROBABILITY_100);
    }

    @Nested
    @DisplayName("즉시당첨 설문 응답 결과 확인 테스트")
    class SurveyResultCheck {
        @Nested
        @DisplayName("실패 테스트")
        class Fail {

            @DisplayName("없는 설문을 조회할시 에러를 반환한다.")
            @WithMockUser(username = "duswo0624@naver.com")
            @Test
            void emptySurveyIsThrowError() {
                // given
                setAuthority();
                saveAccountYJ("duswo0624@naver.com");
                assertThatThrownBy(() -> surveyResultService.getSurveyResult(1L))
                        .isInstanceOf(BaseException.class)
                        .hasMessage("없는 설문입니다.");
            }

            @DisplayName("즉시당첨만 확인이 가능하다.")
            @WithMockUser(username = "duswo0624@naver.com")
            @Test
            void canCheckInstance() {
                // given
                setAuthority();
                saveAccountYJ("duswo0624@naver.com");
                SurveyCategory surveyCategory = SurveyCategory.builder()
                        .type(SurveyCategoryType.NORMAL)
                        .build();
                Survey survey = getSurvey(0, 100, surveyCategory);
                surveyRepository.save(survey);
                assertThatThrownBy(() -> surveyResultService.getSurveyResult(survey.getId()))
                        .isInstanceOf(BaseException.class)
                        .hasMessage("즉시 당첨만 확인이 가능합니다.");
            }

            @DisplayName("응답한 설문만 확인이 가능하다.")
            @WithMockUser(username = "duswo0624@naver.com")
            @Test
            void canCheckAnsweredSurvey() {
                // given
                setAuthority();
                saveAccountYJ("duswo0624@naver.com");
                SurveyCategory surveyCategory = SurveyCategory.builder()
                        .type(SurveyCategoryType.INSTANT_WIN)
                        .build();
                Survey survey = getSurvey(0, 100, surveyCategory);
                surveyRepository.save(survey);
                assertThatThrownBy(() -> surveyResultService.getSurveyResult(survey.getId()))
                        .isInstanceOf(BaseException.class)
                        .hasMessage("미응답 설문입니다.");
            }
        }

        @Nested
        @DisplayName("성공 테스트")
        class Success {
            @DisplayName("당첨이 되지않은 결과인 경우 isWin 은 False 이다.")
            @WithMockUser(username = "duswo0624@naver.com")
            @Test
            void canCheckAnsweredSurvey() {
                // given
                setAuthority();
                saveAccountYJ("duswo0624@naver.com");

                SurveyCategory surveyCategory = SurveyCategory.builder()
                        .type(SurveyCategoryType.INSTANT_WIN)
                        .build();

                // 상품 생성
                Giveaway giveaway = Giveaway.builder()
                        .giveawayType(GiveawayType.COFFEE)
                        .name("커피")
                        .price(10000)
                        .build();
                Survey survey = getSurvey(1, 1, surveyCategory);
                survey.toClose();

                SurveyGiveaway surveyGiveaway = SurveyGiveaway.builder()
                        .giveaway(giveaway)
                        .count(1)
                        .survey(survey)
                        .build();

                survey.addSurveyGiveaways(List.of(surveyGiveaway));

                TargetNumber targetNumber = TargetNumber.builder()
                        .number(1)
                        .survey(survey)
                        .giveaway(giveaway)
                        .build();
                survey.addTargetNumbers(List.of(targetNumber));

                giveawayRepository.save(giveaway);
                //설문 당첨 상품
                surveyRepository.save(survey);
                SurveyResult surveyResult = getSurveyResult(survey, 2, false);

                assertThat(surveyResultService.getSurveyResult(survey.getId()).isWin()).isEqualTo(false);
            }

            @DisplayName("당첨된 결과인 경우 isWin 은 true 이다.")
            @WithMockUser(username = "duswo0624@naver.com")
            @Test
            void isWinTrue() {
                // given
                setAuthority();
                saveAccountYJ("duswo0624@naver.com");

                SurveyCategory surveyCategory = SurveyCategory.builder()
                        .type(SurveyCategoryType.INSTANT_WIN)
                        .build();

                // 상품 생성
                Giveaway giveaway = Giveaway.builder()
                        .giveawayType(GiveawayType.COFFEE)
                        .name("커피")
                        .price(10000)
                        .build();
                Survey survey = getSurvey(1, 1, surveyCategory);
                survey.toClose();

                SurveyGiveaway surveyGiveaway = SurveyGiveaway.builder()
                        .giveaway(giveaway)
                        .count(1)
                        .survey(survey)
                        .build();

                survey.addSurveyGiveaways(List.of(surveyGiveaway));

                TargetNumber targetNumber = TargetNumber.builder()
                        .number(1)
                        .survey(survey)
                        .giveaway(giveaway)
                        .build();
                survey.addTargetNumbers(List.of(targetNumber));

                giveawayRepository.save(giveaway);
                //설문 당첨 상품
                surveyRepository.save(survey);
                SurveyResult surveyResult = getSurveyResult(survey, 1, true);

                assertThat(surveyResultService.getSurveyResult(survey.getId()).isWin()).isEqualTo(true);
            }

            @DisplayName("당첨인원이 2명인 상품에 대하여 설문 인원이 2명이면 당첨 확률은 100%다.")
            @WithMockUser(username = "duswo0624@naver.com")
            @Test
            void mustGetGiveaway() {
                // given
                setAuthority();
                saveAccountYJ("duswo0624@naver.com");

                SurveyCategory surveyCategory = SurveyCategory.builder()
                        .type(SurveyCategoryType.INSTANT_WIN)
                        .build();

                // 상품 생성
                Giveaway giveaway1 = Giveaway.builder()
                        .giveawayType(GiveawayType.COFFEE)
                        .name("커피")
                        .price(10000)
                        .build();
                Giveaway giveaway2 = Giveaway.builder()
                        .giveawayType(GiveawayType.CHICKEN)
                        .name("치킨")
                        .price(10000)
                        .build();
                Survey survey = getSurvey(2, 5, surveyCategory);
                survey.toClose();

                SurveyGiveaway surveyGiveaway1 = SurveyGiveaway.builder()
                        .giveaway(giveaway1)
                        .count(2)
                        .survey(survey)
                        .build();
                SurveyGiveaway surveyGiveaway2 = SurveyGiveaway.builder()
                        .giveaway(giveaway2)
                        .count(2)
                        .survey(survey)
                        .build();

                survey.addSurveyGiveaways(List.of(surveyGiveaway1, surveyGiveaway2));

                TargetNumber targetNumber = TargetNumber.builder()
                        .number(1)
                        .survey(survey)
                        .giveaway(giveaway1)
                        .build();
                survey.addTargetNumbers(List.of(targetNumber));

                giveawayRepository.save(giveaway1);
                giveawayRepository.save(giveaway2);
                //설문 당첨 상품
                surveyRepository.save(survey);
                SurveyResult surveyResult = getSurveyResult(survey, 1, true);

                double oneHundredPercent = 1.0;
                assertThat(surveyResultService.getSurveyResult(survey.getId()).getProbability()).isEqualTo(oneHundredPercent);
            }

        }
    }

    @Nested
    @DisplayName("마이페이지 설문 당첨결과 확인 테스트")
    class MyPageSurveyResultCheck {
        @Nested
        @DisplayName("실패 테스트")
        class Fail {

            @DisplayName("없는 설문을 조회할시 에러를 반환한다.")
            @WithMockUser(username = "duswo0624@naver.com")
            @Test
            void emptySurveyIsThrowError() {
                // given
                setAuthority();
                saveAccountYJ("duswo0624@naver.com");
                assertThatThrownBy(() -> surveyResultService.patchSurveyResult(1L))
                        .isInstanceOf(BaseException.class)
                        .hasMessage("없는 설문입니다.");
            }

            @DisplayName("응답한 설문만 확인이 가능하다.")
            @WithMockUser(username = "duswo0624@naver.com")
            @Test
            void canCheckAnsweredSurvey() {
                // given
                setAuthority();
                saveAccountYJ("duswo0624@naver.com");
                SurveyCategory surveyCategory = SurveyCategory.builder()
                        .type(SurveyCategoryType.NORMAL)
                        .build();
                Survey survey = getSurvey(0, 100, surveyCategory);
                survey.toClose();
                surveyRepository.save(survey);

                assertThatThrownBy(() -> surveyResultService.patchSurveyResult(survey.getId()))
                        .isInstanceOf(BaseException.class)
                        .hasMessage("미응답 설문입니다.");
            }

            @DisplayName("마감된 설문만 확인이 가능하다.")
            @WithMockUser(username = "duswo0624@naver.com")
            @Test
            void canCheckCloseSurvey() {
                // given
                setAuthority();
                saveAccountYJ("duswo0624@naver.com");
                SurveyCategory surveyCategory = SurveyCategory.builder()
                        .type(SurveyCategoryType.NORMAL)
                        .build();
                Survey survey = getSurvey(0, 100, surveyCategory);
                surveyRepository.save(survey);

                assertThatThrownBy(() -> surveyResultService.patchSurveyResult(survey.getId()))
                        .isInstanceOf(BaseException.class)
                        .hasMessage("마감되지 않은 설문은 결과를 확인할수 없습니다.");
            }

            @DisplayName("마이페이지에서 결과 확인은 노말타입 설문만 확인이 가능하다.")
            @WithMockUser(username = "duswo0624@naver.com")
            @Test
            void canCheckTypeIsNormal() {
                // given
                setAuthority();
                saveAccountYJ("duswo0624@naver.com");
                SurveyCategory surveyCategory = SurveyCategory.builder()
                        .type(SurveyCategoryType.INSTANT_WIN)
                        .build();
                Survey survey = getSurvey(0, 100, surveyCategory);
                surveyRepository.save(survey);

                assertThatThrownBy(() -> surveyResultService.patchSurveyResult(survey.getId()))
                        .isInstanceOf(BaseException.class)
                        .hasMessage("마이페이지 당첨 결과는 노말 타입만 확인이 가능합니다.");
            }

            @DisplayName("미응답 설문은 결과를 확인 할 수 없다.")
            @WithMockUser(username = "duswo0624@naver.com")
            @Test
            void canNotCheckResultNoAnsweredSurvey() {
                // given
                setAuthority();
                saveAccountYJ("duswo0624@naver.com");
                SurveyCategory surveyCategory = SurveyCategory.builder()
                        .type(SurveyCategoryType.NORMAL)
                        .build();
                Survey survey = getSurvey(0, 100, surveyCategory);
                surveyRepository.save(survey);

                assertThatThrownBy(() -> surveyResultService.patchSurveyResult(survey.getId()))
                        .isInstanceOf(BaseException.class)
                        .hasMessage("마감되지 않은 설문은 결과를 확인할수 없습니다.");
            }
        }

        @Nested
        @DisplayName("성공 테스트")
        class Success {
            @DisplayName("당첨된 결과인 경우 isWin 은 true 이다.")
            @WithMockUser(username = "duswo0624@naver.com")
            @Test
            void isWinTrue() {
                // given
                setAuthority();
                saveAccountYJ("duswo0624@naver.com");

                SurveyCategory surveyCategory = SurveyCategory.builder()
                        .type(SurveyCategoryType.NORMAL)
                        .build();

                // 상품 생성
                Giveaway giveaway = Giveaway.builder()
                        .giveawayType(GiveawayType.COFFEE)
                        .name("커피")
                        .price(10000)
                        .build();
                Survey survey = getSurvey(1, 1, surveyCategory);
                survey.toClose();

                SurveyGiveaway surveyGiveaway = SurveyGiveaway.builder()
                        .giveaway(giveaway)
                        .count(1)
                        .survey(survey)
                        .build();

                survey.addSurveyGiveaways(List.of(surveyGiveaway));

                TargetNumber targetNumber = TargetNumber.builder()
                        .number(1)
                        .survey(survey)
                        .giveaway(giveaway)
                        .build();
                survey.addTargetNumbers(List.of(targetNumber));

                giveawayRepository.save(giveaway);
                //설문 당첨 상품
                surveyRepository.save(survey);
                SurveyResult surveyResult = getSurveyResult(survey, 1, true);

                assertThat(surveyResultService.patchSurveyResult(survey.getId()).isWin()).isEqualTo(true);
            }

            @DisplayName("당첨이 되지 않은 결과인 경우 isWin 은 false다.")
            @WithMockUser(username = "duswo0624@naver.com")
            @Test
            void isWinFalse() {
                // given
                setAuthority();
                saveAccountYJ("duswo0624@naver.com");

                SurveyCategory surveyCategory = SurveyCategory.builder()
                        .type(SurveyCategoryType.NORMAL)
                        .build();

                // 상품 생성
                Giveaway giveaway = Giveaway.builder()
                        .giveawayType(GiveawayType.COFFEE)
                        .name("커피")
                        .price(10000)
                        .build();
                Survey survey = getSurvey(2, 2, surveyCategory);
                survey.toClose();

                SurveyGiveaway surveyGiveaway = SurveyGiveaway.builder()
                        .giveaway(giveaway)
                        .count(1)
                        .survey(survey)
                        .build();

                survey.addSurveyGiveaways(List.of(surveyGiveaway));

                TargetNumber targetNumber = TargetNumber.builder()
                        .number(1)
                        .survey(survey)
                        .giveaway(giveaway)
                        .build();
                survey.addTargetNumbers(List.of(targetNumber));

                giveawayRepository.save(giveaway);
                //설문 당첨 상품
                surveyRepository.save(survey);
                SurveyResult surveyResult = getSurveyResult(survey, 1, false);

                assertThat(surveyResultService.patchSurveyResult(survey.getId()).isWin()).isEqualTo(false);
            }
        }
    }

    @DisplayName("설문ID를 이용하여 당첨자 목록을 조회한다.")
    @WithMockUser(username = "duswo0624@naver.com")
    @Test
    void getWinningUsers() {
        // given
        setAuthority();
        setAccountCode();
        setSurveyCategory();
        String email = "duswo0624@naver.com";
        saveAccountYJ(email);

        QuestionCreateServiceRequest questionCreateServiceRequestByShortForm = QuestionCreateServiceRequest.builder()
                .questionNumber(1L)
                .content("좋아하는 과일을 작성해주세요.")
                .imgAddress(null)
                .type(QuestionCategoryType.SHORT_FORM)
                .build();
        List<QuestionCreateServiceRequest> questionCreateServiceRequests = List.of(questionCreateServiceRequestByShortForm);

        GiveawayType giveawayType = GiveawayType.COFFEE;
        String giveawayName = "스타벅스 아메리카노";
        int price = 4500;
        GiveawayRequest request = GiveawayRequest.builder()
                .giveawayType(giveawayType)
                .name(giveawayName)
                .price(price)
                .build();
        giveawayService.createGiveaway(request);
        Optional<Giveaway> giveaway = giveawayRepository.findGiveawayByName(giveawayName);
        Long giveawayId = giveaway.get().getId();
        GiveawayAssignServiceRequest giveawayAssignServiceRequest = GiveawayAssignServiceRequest.builder()
                .id(giveawayId)
                .count(1)
                .build();
        List<GiveawayAssignServiceRequest> giveawayAssignServiceRequests = List.of(giveawayAssignServiceRequest);

        LocalDateTime now = LocalDateTime.now();
        String surveyTitle = "당신은 어떤 과일을 좋아하나요?";
        SurveyCreateServiceRequest surveyCreateServiceRequest = SurveyCreateServiceRequest.builder()
                .title(surveyTitle)
                .startTime(now.minusDays(1))
                .endTime(now.plusDays(1))
                .headCount(0)
                .surveyTarget(List.of(AccountCodeType.MAN, AccountCodeType.WOMAN, AccountCodeType.TWENTIES))
                .closedHeadCount(1)
                .type(SurveyCategoryType.NORMAL)
                .questions(questionCreateServiceRequests)
                .giveaways(giveawayAssignServiceRequests)
                .build();

        SurveyResponse saveSurvey = surveyService.addSurvey(surveyCreateServiceRequest);
        Long surveyId = saveSurvey.getId();
        surveyRepository.findById(surveyId).get().toOpen();

        QuestionAnswerCreateServiceRequest questionAnswerCreateServiceRequest
                = QuestionAnswerCreateServiceRequest
                .builder()
                .questionId(saveSurvey.getQuestions().get(0).getId())
                .multipleChoiceAnswer(null)
                .shorFormAnswer("치즈케익")
                .build();
        questionAnswerService.addQuestionAnswer(LocalDateTime.now(), surveyId, List.of(questionAnswerCreateServiceRequest));

        // when
        List<WinningAccountResponse> winningUsers = surveyResultService.getWinningUsers(surveyId);

        // then
        assertThat(winningUsers.size()).isOne();
        assertThat(winningUsers.get(0).getEmail()).isEqualTo(email);
    }

    @DisplayName("존재하지 않는 설문의 경우 당첨자 목록을 조회할 수 없다.")
    @Test
    void cannotGetWinningUsersWithInvalidSurveyId() {
        // given
        Long invalidSurveyId = 1L;

        // when // then
        assertThatThrownBy(() -> surveyResultService.getWinningUsers(invalidSurveyId))
                .isInstanceOf(BaseException.class)
                .hasMessage("없는 설문입니다.");
    }

    private Survey getSurvey(int headCount, int closedHeadCount, SurveyCategory surveyCategory) {
        Survey survey = Survey.builder()
                .title("당신은 어떤 과일을 좋아하십니까?")
                .startTime(LocalDateTime.now().minusDays(1))
                .endTime(LocalDateTime.now().plusDays(3))
                .headCount(headCount)
                .closedHeadCount(closedHeadCount)
                .writer(accountService.getCurrentAccountBySecurity().getId())
                .type(surveyCategory)
                .build();
        survey.toOpen();
        return survey;
    }

    private void saveAccountYJ(String email) {
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
    }

    private SurveyResult getSurveyResult(Survey survey, int submitOrder, boolean isWin) {
        SurveyResult surveyResult = SurveyResult.builder()
                .win(isWin)
                .answerDateTime(LocalDateTime.now())
                .survey(survey)
                .userCheck(false)
                .account(accountService.getCurrentAccountBySecurity())
                .submitOrder(submitOrder)
                .build();
        surveyResultRepository.save(surveyResult);
        return surveyResult;
    }

    private void setAuthority() {
        Authority userAuthority = Authority.builder()
                .type(AuthorityType.ROLE_USER)
                .build();

        authorityRepository.save(userAuthority);
    }

    private void setAccountCode() {
        List<AccountCodeType> accountCodeTypes = List.of(AccountCodeType.MAN, AccountCodeType.WOMAN, AccountCodeType.UNDER_TEENS,
                AccountCodeType.TEENS, AccountCodeType.TWENTIES, AccountCodeType.THIRTIES,
                AccountCodeType.FORTIES, AccountCodeType.FIFTIES, AccountCodeType.SIXTIES);
        for (AccountCodeType accountCodeType : accountCodeTypes) {
            accountCodeRepository.save(AccountCode.builder()
                    .type(accountCodeType)
                    .build());
        }
    }

    private void setSurveyCategory() {
        surveyCategoryRepository.save(SurveyCategory.builder()
                .type(SurveyCategoryType.NORMAL)
                .build());
        surveyCategoryRepository.save(SurveyCategory.builder()
                .type(SurveyCategoryType.INSTANT_WIN)
                .build());
    }
}