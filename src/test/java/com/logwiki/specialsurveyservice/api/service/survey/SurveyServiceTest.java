package com.logwiki.specialsurveyservice.api.service.survey;

import com.logwiki.specialsurveyservice.IntegrationTestSupport;
import com.logwiki.specialsurveyservice.api.controller.giveaway.request.GiveawayRequest;
import com.logwiki.specialsurveyservice.api.service.account.AccountService;
import com.logwiki.specialsurveyservice.api.service.account.request.AccountCreateServiceRequest;
import com.logwiki.specialsurveyservice.api.service.account.response.AccountResponse;
import com.logwiki.specialsurveyservice.api.service.auth.AuthService;
import com.logwiki.specialsurveyservice.api.service.giveaway.GiveawayService;
import com.logwiki.specialsurveyservice.api.service.question.QuestionAnswerService;
import com.logwiki.specialsurveyservice.api.service.question.request.MultipleChoiceCreateServiceRequest;
import com.logwiki.specialsurveyservice.api.service.question.request.QuestionAnswerCreateServiceRequest;
import com.logwiki.specialsurveyservice.api.service.question.request.QuestionCreateServiceRequest;
import com.logwiki.specialsurveyservice.api.service.sse.response.SurveyAnswerResponse;
import com.logwiki.specialsurveyservice.api.service.survey.request.GiveawayAssignServiceRequest;
import com.logwiki.specialsurveyservice.api.service.survey.request.SurveyCreateServiceRequest;
import com.logwiki.specialsurveyservice.api.service.survey.response.AbstractSurveyResponse;
import com.logwiki.specialsurveyservice.api.service.survey.response.SurveyResponse;
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
import com.logwiki.specialsurveyservice.domain.survey.AnswerPossibleType;
import com.logwiki.specialsurveyservice.domain.survey.Survey;
import com.logwiki.specialsurveyservice.domain.survey.SurveyRepository;
import com.logwiki.specialsurveyservice.domain.surveycategory.SurveyCategory;
import com.logwiki.specialsurveyservice.domain.surveycategory.SurveyCategoryRepository;
import com.logwiki.specialsurveyservice.domain.surveycategory.SurveyCategoryType;
import com.logwiki.specialsurveyservice.exception.BaseException;
import java.util.Collection;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@Transactional
class SurveyServiceTest extends IntegrationTestSupport {

    @Autowired
    private GiveawayRepository giveawayRepository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private SurveyService surveyService;
    @Autowired
    GiveawayService giveawayService;
    @Autowired
    AuthorityRepository authorityRepository;
    @Autowired
    private AccountCodeRepository accountCodeRepository;
    @Autowired
    private SurveyCategoryRepository surveyCategoryRepository;
    @Autowired
    private SurveyRepository surveyRepository;
    @Autowired
    private QuestionAnswerService questionAnswerService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AuthService authService;
    @Autowired
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    @BeforeEach
    void setUp() {
        setAuthority();
        setAccountCode();
        setSurveyCategory();
    }

    @DisplayName("설문 이름, 시작 시간, 마감 시간, 설문 인원, 설문 마감 인원, 설문 타입, 질문 목록, 당첨 상품 목록, 설문 대상자를 이용하여 설문을 등록한다.")
    @WithMockUser(username = "duswo0624@naver.com")
    @Test
    void addSurvey() throws SchedulerException {
        // given
        String email = "duswo0624@naver.com";
        String password = "1234";
        AccountCodeType gender = AccountCodeType.MAN;
        AccountCodeType age = AccountCodeType.TWENTIES;
        String name = "최연재";
        String phoneNumber = "010-1234-5678";
        LocalDate birthday = LocalDate.of(1997, Month.JUNE, 24);
        AccountCreateServiceRequest accountCreateServiceRequest = getAccountCreateServiceRequest(
                email, password, gender, age, name, phoneNumber, birthday);
        accountService.signup(accountCreateServiceRequest);

        MultipleChoiceCreateServiceRequest multipleChoiceCreateServiceRequest1 = MultipleChoiceCreateServiceRequest.builder()
                .content("사과")
                .linkNumber(2L)
                .build();
        MultipleChoiceCreateServiceRequest multipleChoiceCreateServiceRequest2 = MultipleChoiceCreateServiceRequest.builder()
                .content("바나나")
                .linkNumber(3L)
                .build();
        List<MultipleChoiceCreateServiceRequest> multipleChoiceCreateServiceRequests = List.of(multipleChoiceCreateServiceRequest1, multipleChoiceCreateServiceRequest2);

        QuestionCreateServiceRequest questionCreateServiceRequestByMultipleChoice = QuestionCreateServiceRequest.builder()
                .questionNumber(1L)
                .content("좋아하는 과일을 고르세요.")
                .imgAddress(null)
                .type(QuestionCategoryType.MULTIPLE_CHOICE)
                .multipleChoices(multipleChoiceCreateServiceRequests)
                .build();
        QuestionCreateServiceRequest questionCreateServiceRequestByShortForm1 = QuestionCreateServiceRequest.builder()
                .questionNumber(2L)
                .content("사과를 좋아하는 이유는 무엇인가요?")
                .imgAddress(null)
                .type(QuestionCategoryType.SHORT_FORM)
                .multipleChoices(null)
                .build();
        QuestionCreateServiceRequest questionCreateServiceRequestByShortForm2 = QuestionCreateServiceRequest.builder()
                .questionNumber(3L)
                .content("바나나를 좋아하는 이유는 무엇인가요?")
                .imgAddress(null)
                .type(QuestionCategoryType.SHORT_FORM)
                .multipleChoices(null)
                .build();
        List<QuestionCreateServiceRequest> questionCreateServiceRequests = List.of(questionCreateServiceRequestByMultipleChoice,
                questionCreateServiceRequestByShortForm1,
                questionCreateServiceRequestByShortForm2);

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
                .count(10)
                .build();
        List<GiveawayAssignServiceRequest> giveawayAssignServiceRequests = List.of(giveawayAssignServiceRequest);

        String title = "당신은 어떤 과일을 좋아하나요?";
        SurveyCategoryType surveyCategoryType = SurveyCategoryType.INSTANT_WIN;
        int closedHeadCount = 100;
        LocalDateTime startTime = LocalDateTime.now().minusDays(1);
        LocalDateTime endTime = LocalDateTime.now().plusDays(1);
        List<AccountCodeType> targets = List.of(AccountCodeType.MAN, AccountCodeType.WOMAN,
                AccountCodeType.UNDER_TEENS, AccountCodeType.TEENS, AccountCodeType.TWENTIES,
                AccountCodeType.THIRTIES, AccountCodeType.FORTIES, AccountCodeType.FIFTIES,
                AccountCodeType.SIXTIES);
        SurveyCreateServiceRequest surveyCreateServiceRequest = SurveyCreateServiceRequest.builder()
                .title(title)
                .startTime(startTime)
                .endTime(endTime)
                .headCount(0)
                .surveyTarget(targets)
                .closedHeadCount(closedHeadCount)
                .type(surveyCategoryType)
                .questions(questionCreateServiceRequests)
                .giveaways(giveawayAssignServiceRequests)
                .build();

        // when
        SurveyResponse saveSurvey = surveyService.addSurvey(surveyCreateServiceRequest);

        // then
        assertThat(saveSurvey).isNotNull();
        assertThat(saveSurvey).extracting("title", "startTime", "endTime")
                .contains(title, startTime, endTime);
    }

    @DisplayName("설문을 작성하기 위해서는 로그인된 유저가 있어야 한다.")
    @Test
    void addSurveyWithInvalidUser() {
        // given
        SurveyCreateServiceRequest surveyCreateServiceRequest = SurveyCreateServiceRequest.builder()
                .title("어떤 외국어를 배우고 싶습니까?")
                .startTime(LocalDateTime.now().minusDays(1))
                .endTime(LocalDateTime.now().plusDays(1))
                .headCount(0)
                .surveyTarget(List.of(AccountCodeType.MAN, AccountCodeType.WOMAN))
                .closedHeadCount(100)
                .build();

        // when // then
        assertThatThrownBy(() -> surveyService.addSurvey(surveyCreateServiceRequest))
                .isInstanceOf(BaseException.class)
                .hasMessage("인증 정보가 등록되어 있지 않습니다.");
    }

    @DisplayName("존재하지 않는 나이성별 코드로는 설문을 작성할 수 없다.")
    @WithMockUser(username = "duswo0624@naver.com")
    @Test
    void cannotAddSurveyWithInvalidAccountCode() {
        // given
        String email = "duswo0624@naver.com";
        AccountCreateServiceRequest accountCreateServiceRequest = getAccountCreateServiceRequest(
                email, "1234", AccountCodeType.MAN, AccountCodeType.TWENTIES, "최연재",
                "010-1234-5678",
                LocalDate.of(1997, 6, 24));
        accountService.signup(accountCreateServiceRequest);

        QuestionCreateServiceRequest questionCreateServiceRequestByShortForm = QuestionCreateServiceRequest.builder()
                .questionNumber(1L)
                .content("바나나를 좋아하는 이유는 무엇인가요?")
                .imgAddress(null)
                .type(QuestionCategoryType.SHORT_FORM)
                .multipleChoices(null)
                .build();
        List<QuestionCreateServiceRequest> questionCreateServiceRequests = List.of(
                questionCreateServiceRequestByShortForm);

        GiveawayAssignServiceRequest giveawayAssignServiceRequest = GiveawayAssignServiceRequest.builder()
                .id(1L)
                .count(10)
                .build();
        List<GiveawayAssignServiceRequest> giveawayAssignServiceRequests = List.of(giveawayAssignServiceRequest);

        AccountCodeType accountCodeType = AccountCodeType.MAN;
        SurveyCreateServiceRequest surveyCreateServiceRequest = SurveyCreateServiceRequest.builder()
                .title("당신은 어떤 과일을 좋아하나요?")
                .startTime(LocalDateTime.now().minusDays(1))
                .endTime(LocalDateTime.now().plusDays(1))
                .headCount(0)
                .surveyTarget(List.of(accountCodeType))
                .closedHeadCount(100)
                .type(SurveyCategoryType.INSTANT_WIN)
                .questions(questionCreateServiceRequests)
                .giveaways(giveawayAssignServiceRequests)
                .build();

        accountCodeRepository.delete(accountCodeRepository.findAccountCodeByType(accountCodeType).get());

        // when // then
        assertThatThrownBy(() -> surveyService.addSurvey(surveyCreateServiceRequest))
                .isInstanceOf(BaseException.class)
                .hasMessage("없는 나이,성별 코드 입니다.");
    }

    @DisplayName("설문을 제작할 때는 등록된 당첨 상품을 사용해야 한다.")
    @WithMockUser(username = "duswo0624@naver.com")
    @Test
    void addSurveyWithInvalidGiveaway() {
        // given
        String email = "duswo0624@naver.com";
        String password = "1234";
        AccountCodeType gender = AccountCodeType.MAN;
        AccountCodeType age = AccountCodeType.TWENTIES;
        String name = "최연재";
        String phoneNumber = "010-1234-5678";
        LocalDate birthday = LocalDate.of(1997, Month.JUNE, 24);
        AccountCreateServiceRequest accountCreateServiceRequest = getAccountCreateServiceRequest(
                email, password, gender, age, name, phoneNumber, birthday);
        accountService.signup(accountCreateServiceRequest);

        MultipleChoiceCreateServiceRequest multipleChoiceCreateServiceRequest1 = MultipleChoiceCreateServiceRequest.builder()
                .content("사과")
                .linkNumber(2L)
                .build();
        MultipleChoiceCreateServiceRequest multipleChoiceCreateServiceRequest2 = MultipleChoiceCreateServiceRequest.builder()
                .content("바나나")
                .linkNumber(3L)
                .build();
        List<MultipleChoiceCreateServiceRequest> multipleChoiceCreateServiceRequests = List.of(multipleChoiceCreateServiceRequest1, multipleChoiceCreateServiceRequest2);
        QuestionCreateServiceRequest questionCreateServiceRequestByMultipleChoice = QuestionCreateServiceRequest.builder()
                .questionNumber(1L)
                .content("좋아하는 과일을 고르세요.")
                .imgAddress(null)
                .type(QuestionCategoryType.MULTIPLE_CHOICE)
                .multipleChoices(multipleChoiceCreateServiceRequests)
                .build();
        QuestionCreateServiceRequest questionCreateServiceRequestByShortForm1 = QuestionCreateServiceRequest.builder()
                .questionNumber(2L)
                .content("사과를 좋아하는 이유는 무엇인가요?")
                .imgAddress(null)
                .type(QuestionCategoryType.SHORT_FORM)
                .multipleChoices(null)
                .build();
        QuestionCreateServiceRequest questionCreateServiceRequestByShortForm2 = QuestionCreateServiceRequest.builder()
                .questionNumber(3L)
                .content("바나나를 좋아하는 이유는 무엇인가요?")
                .imgAddress(null)
                .type(QuestionCategoryType.SHORT_FORM)
                .multipleChoices(null)
                .build();
        List<QuestionCreateServiceRequest> questionCreateServiceRequests = List.of(questionCreateServiceRequestByMultipleChoice,
                questionCreateServiceRequestByShortForm1,
                questionCreateServiceRequestByShortForm2);

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
        Long InvalidGiveawayId = giveawayId + 1L;

        GiveawayAssignServiceRequest giveawayAssignServiceRequest = GiveawayAssignServiceRequest.builder()
                .id(InvalidGiveawayId)
                .count(10)
                .build();
        List<GiveawayAssignServiceRequest> giveawayAssignServiceRequests = List.of(giveawayAssignServiceRequest);

        String title = "당신은 어떤 과일을 좋아하나요?";
        SurveyCategoryType surveyCategoryType = SurveyCategoryType.INSTANT_WIN;
        int closedHeadCount = 100;
        SurveyCreateServiceRequest surveyCreateServiceRequest = SurveyCreateServiceRequest.builder()
                .title(title)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusDays(1))
                .headCount(50)
                .closedHeadCount(closedHeadCount)
                .type(surveyCategoryType)
                .surveyTarget(new ArrayList<>())
                .questions(questionCreateServiceRequests)
                .giveaways(giveawayAssignServiceRequests)
                .build();

        // when // then
        assertThatThrownBy(() -> surveyService.addSurvey(surveyCreateServiceRequest))
                .isInstanceOf(BaseException.class)
                .hasMessage("등록되어 있지 않은 당첨 상품을 포함하고 있습니다.");
    }

    @DisplayName("설문 추천은 '사용자의 성별과 나이', '설문 타입(NORMAL, INSTANT_WIN)'이 일치하는 설문을 추천해준다.")
    @WithMockUser(username = "duswo0624@naver.com")
    @CsvSource({"MAN,TWENTIES, NORMAL", "MAN, THIRTIES, NORMAL", "WOMAN, THIRTIES, INSTANT_WIN"})
    @ParameterizedTest
    void normalSurveyRecommendWithCategories(AccountCodeType userGender, AccountCodeType userAge, SurveyCategoryType surveyCategoryType) {
        // given
        String email = "duswo0624@naver.com";
        AccountCreateServiceRequest accountCreateServiceRequest = getAccountCreateServiceRequest(
                email, "1234", userGender, userAge, "최연재", "010-1234-5678",
                LocalDate.of(1997, Month.JUNE, 24));
        accountService.signup(accountCreateServiceRequest);

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
                .count(10)
                .build();
        List<GiveawayAssignServiceRequest> giveawayAssignServiceRequests = List.of(giveawayAssignServiceRequest);

        LocalDateTime now = LocalDateTime.now();
        SurveyCreateServiceRequest surveyCreateServiceRequest1 = SurveyCreateServiceRequest.builder()
                .title("당신은 어떤 과일을 좋아하나요?")
                .startTime(now.minusDays(1))
                .endTime(now.plusDays(1))
                .headCount(0)
                .surveyTarget(List.of(AccountCodeType.MAN, AccountCodeType.TWENTIES))
                .closedHeadCount(100)
                .type(surveyCategoryType)
                .questions(questionCreateServiceRequests)
                .giveaways(giveawayAssignServiceRequests)
                .build();

        SurveyCreateServiceRequest surveyCreateServiceRequest2 = SurveyCreateServiceRequest.builder()
                .title("당신은 어떤 음식을 좋아하나요?")
                .startTime(now.minusDays(1))
                .endTime(now.plusDays(2))
                .headCount(0)
                .surveyTarget(List.of(AccountCodeType.MAN, AccountCodeType.THIRTIES))
                .closedHeadCount(100)
                .type(surveyCategoryType)
                .questions(questionCreateServiceRequests)
                .giveaways(giveawayAssignServiceRequests)
                .build();
        SurveyCreateServiceRequest surveyCreateServiceRequest3 = SurveyCreateServiceRequest.builder()
                .title("당신은 어떤 동물을 좋아하나요?")
                .startTime(now.minusDays(1))
                .endTime(now.plusDays(3))
                .headCount(0)
                .surveyTarget(List.of(AccountCodeType.WOMAN, AccountCodeType.THIRTIES))
                .closedHeadCount(100)
                .type(SurveyCategoryType.INSTANT_WIN)
                .questions(questionCreateServiceRequests)
                .giveaways(giveawayAssignServiceRequests)
                .build();

        SurveyResponse saveSurvey1 = surveyService.addSurvey(surveyCreateServiceRequest1);
        surveyRepository.findById(saveSurvey1.getId()).get().toOpen();
        SurveyResponse saveSurvey2 = surveyService.addSurvey(surveyCreateServiceRequest2);
        surveyRepository.findById(saveSurvey2.getId()).get().toOpen();
        SurveyResponse saveSurvey3 = surveyService.addSurvey(surveyCreateServiceRequest3);
        surveyRepository.findById(saveSurvey3.getId()).get().toOpen();

        // when
        List<AbstractSurveyResponse> recommendNormalSurvey = surveyService.getRecommendNormalSurveyForUser();

        // then
        assertThat(recommendNormalSurvey.stream()
                .allMatch(surveyResponse -> surveyResponse.getSurveyTarget().contains(userGender)
                        && surveyResponse.getSurveyTarget().contains(userAge)
                        && surveyResponse.getSurveyCategoryType().equals(surveyCategoryType)))
                .isTrue();
    }


    @DisplayName("일반(타임어택) 설문 추천은 마감시간이 짧은 순으로 설문을 추천 받는다.(익명유저)")
    @WithMockUser(username = "duswo0624@naver.com")
    @Test
    void getNormalRecommendSurveyOrderByEndTimeForAnonymous() {
        // given
        String email = "duswo0624@naver.com";
        AccountCreateServiceRequest accountCreateServiceRequest = getAccountCreateServiceRequest(
                email, "1234", AccountCodeType.MAN, AccountCodeType.TWENTIES, "최연재", "010-1234-5678",
                LocalDate.of(1997, Month.JUNE, 24));
        accountService.signup(accountCreateServiceRequest);

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
                .count(10)
                .build();
        List<GiveawayAssignServiceRequest> giveawayAssignServiceRequests = List.of(giveawayAssignServiceRequest);

        LocalDateTime now = LocalDateTime.now();
        SurveyCreateServiceRequest surveyCreateServiceRequest1 = SurveyCreateServiceRequest.builder()
                .title("당신은 어떤 과일을 좋아하나요?")
                .startTime(now.minusDays(1))
                .endTime(now.plusDays(1))
                .headCount(0)
                .surveyTarget(List.of(AccountCodeType.MAN, AccountCodeType.WOMAN, AccountCodeType.TEENS))
                .closedHeadCount(100)
                .type(SurveyCategoryType.NORMAL)
                .questions(questionCreateServiceRequests)
                .giveaways(giveawayAssignServiceRequests)
                .build();

        SurveyCreateServiceRequest surveyCreateServiceRequest2 = SurveyCreateServiceRequest.builder()
                .title("당신은 어떤 음료를 좋아하나요?")
                .startTime(now.minusDays(1))
                .endTime(now.plusDays(2))
                .headCount(0)
                .surveyTarget(List.of(AccountCodeType.MAN, AccountCodeType.WOMAN, AccountCodeType.TWENTIES))
                .closedHeadCount(100)
                .type(SurveyCategoryType.NORMAL)
                .questions(questionCreateServiceRequests)
                .giveaways(giveawayAssignServiceRequests)
                .build();
        SurveyCreateServiceRequest surveyCreateServiceRequest3 = SurveyCreateServiceRequest.builder()
                .title("당신은 어떤 케익을 좋아하나요?")
                .startTime(now.minusDays(1))
                .endTime(now.plusDays(3))
                .headCount(0)
                .surveyTarget(List.of(AccountCodeType.MAN, AccountCodeType.WOMAN, AccountCodeType.THIRTIES))
                .closedHeadCount(100)
                .type(SurveyCategoryType.NORMAL)
                .questions(questionCreateServiceRequests)
                .giveaways(giveawayAssignServiceRequests)
                .build();

        SurveyResponse saveSurvey1 = surveyService.addSurvey(surveyCreateServiceRequest1);
        surveyRepository.findById(saveSurvey1.getId()).get().toOpen();
        SurveyResponse saveSurvey2 = surveyService.addSurvey(surveyCreateServiceRequest2);
        surveyRepository.findById(saveSurvey2.getId()).get().toOpen();
        SurveyResponse saveSurvey3 = surveyService.addSurvey(surveyCreateServiceRequest3);
        surveyRepository.findById(saveSurvey3.getId()).get().toOpen();

        // when
        List<AbstractSurveyResponse> recommendNormalSurvey = surveyService.getRecommendNormalSurveyForAnonymous();

        // then
        assertThat(recommendNormalSurvey.size()).isEqualTo(3);

        List<AbstractSurveyResponse> sortedSurveyResponses = recommendNormalSurvey.stream()
                .sorted(Comparator.comparing(AbstractSurveyResponse::getEndTime))
                .toList();
        boolean sameOrder = true;
        for (int i = 0; i < recommendNormalSurvey.size(); i++) {
            if (recommendNormalSurvey.get(i).getId() != sortedSurveyResponses.get(i).getId())
                sameOrder = false;
        }
        assertThat(sameOrder).isTrue();
    }

    @DisplayName("즉시당첨(INSTANT_WIN) 설문 추천은 당첨 확률이 높은 순으로 설문을 추천 받는다.(익명유저)")
    @WithMockUser(username = "duswo0624@naver.com")
    @Test
    void getInstantWinRecommendSurveyOrderByWinningPercentForAnonymous() {
        // given
        String email = "duswo0624@naver.com";
        AccountCreateServiceRequest accountCreateServiceRequest = getAccountCreateServiceRequest(
                email, "1234", AccountCodeType.MAN, AccountCodeType.TWENTIES, "최연재", "010-1234-5678",
                LocalDate.of(1997, Month.JUNE, 24));
        accountService.signup(accountCreateServiceRequest);

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
        GiveawayAssignServiceRequest giveawayAssignServiceRequest1 = GiveawayAssignServiceRequest.builder()
                .id(giveawayId)
                .count(10)
                .build();
        List<GiveawayAssignServiceRequest> giveawayAssignServiceRequests1 = List.of(giveawayAssignServiceRequest1);

        LocalDateTime now = LocalDateTime.now();
        SurveyCreateServiceRequest surveyCreateServiceRequest1 = SurveyCreateServiceRequest.builder()
                .title("당신은 어떤 과일을 좋아하나요?")
                .startTime(now.minusDays(1))
                .endTime(now.plusDays(1))
                .headCount(0)
                .surveyTarget(List.of(AccountCodeType.MAN, AccountCodeType.WOMAN, AccountCodeType.TEENS))
                .closedHeadCount(100)
                .type(SurveyCategoryType.INSTANT_WIN)
                .questions(questionCreateServiceRequests)
                .giveaways(giveawayAssignServiceRequests1)
                .build();

        GiveawayAssignServiceRequest giveawayAssignServiceRequest2 = GiveawayAssignServiceRequest.builder()
                .id(giveawayId)
                .count(30)
                .build();
        List<GiveawayAssignServiceRequest> giveawayAssignServiceRequests2 = List.of(giveawayAssignServiceRequest2);
        SurveyCreateServiceRequest surveyCreateServiceRequest2 = SurveyCreateServiceRequest.builder()
                .title("당신은 어떤 음료를 좋아하나요?")
                .startTime(now.minusDays(1))
                .endTime(now.plusDays(2))
                .headCount(0)
                .surveyTarget(List.of(AccountCodeType.MAN, AccountCodeType.WOMAN, AccountCodeType.TWENTIES))
                .closedHeadCount(100)
                .type(SurveyCategoryType.INSTANT_WIN)
                .questions(questionCreateServiceRequests)
                .giveaways(giveawayAssignServiceRequests2)
                .build();

        GiveawayAssignServiceRequest giveawayAssignServiceRequest3 = GiveawayAssignServiceRequest.builder()
                .id(giveawayId)
                .count(20)
                .build();
        List<GiveawayAssignServiceRequest> giveawayAssignServiceRequests3 = List.of(giveawayAssignServiceRequest3);
        SurveyCreateServiceRequest surveyCreateServiceRequest3 = SurveyCreateServiceRequest.builder()
                .title("당신은 어떤 케익을 좋아하나요?")
                .startTime(now.minusDays(1))
                .endTime(now.plusDays(3))
                .headCount(0)
                .surveyTarget(List.of(AccountCodeType.MAN, AccountCodeType.WOMAN, AccountCodeType.THIRTIES))
                .closedHeadCount(100)
                .type(SurveyCategoryType.INSTANT_WIN)
                .questions(questionCreateServiceRequests)
                .giveaways(giveawayAssignServiceRequests3)
                .build();

        SurveyResponse saveSurvey1 = surveyService.addSurvey(surveyCreateServiceRequest1);
        surveyRepository.findById(saveSurvey1.getId()).get().toOpen();
        SurveyResponse saveSurvey2 = surveyService.addSurvey(surveyCreateServiceRequest2);
        surveyRepository.findById(saveSurvey2.getId()).get().toOpen();
        SurveyResponse saveSurvey3 = surveyService.addSurvey(surveyCreateServiceRequest3);
        surveyRepository.findById(saveSurvey3.getId()).get().toOpen();

        // when
        List<AbstractSurveyResponse> recommendInstantSurvey = surveyService.getRecommendInstantSurveyForAnonymous();

        // then
        assertThat(recommendInstantSurvey.size()).isEqualTo(3);

        List<AbstractSurveyResponse> sortedSurveyResponses = surveyService.getRecommendInstantSurveyForAnonymous().stream()
                .sorted(Comparator.comparing(AbstractSurveyResponse::getWinningPercent).reversed())
                .toList();

        boolean sameOrder = true;
        for (int i = 0; i < recommendInstantSurvey.size(); i++) {
            if (recommendInstantSurvey.get(i).getId() != sortedSurveyResponses.get(i).getId())
                sameOrder = false;
        }
        assertThat(sameOrder).isTrue();
    }

    @DisplayName("설문 소요 시간을 활용한 설문 추천은 설문 추천 시간이 짧은 설문 순서대로 추천 받는다.(익명유저)")
    @WithMockUser(username = "duswo0624@naver.com")
    @Test
    void getShortTimeRecommendSurveyOrderByRequiredTimeForAnonymous() {
        // given
        String email = "duswo0624@naver.com";
        AccountCreateServiceRequest accountCreateServiceRequest = getAccountCreateServiceRequest(
                email, "1234", AccountCodeType.MAN, AccountCodeType.TWENTIES, "최연재", "010-1234-5678",
                LocalDate.of(1997, Month.JUNE, 24));
        accountService.signup(accountCreateServiceRequest);

        MultipleChoiceCreateServiceRequest multipleChoiceCreateServiceRequest1 = MultipleChoiceCreateServiceRequest.builder()
                .content("사과")
                .linkNumber(2L)
                .build();
        MultipleChoiceCreateServiceRequest multipleChoiceCreateServiceRequest2 = MultipleChoiceCreateServiceRequest.builder()
                .content("바나나")
                .linkNumber(3L)
                .build();
        List<MultipleChoiceCreateServiceRequest> multipleChoiceCreateServiceRequests = List.of(multipleChoiceCreateServiceRequest1, multipleChoiceCreateServiceRequest2);

        QuestionCreateServiceRequest questionCreateServiceRequestByMultipleChoice = QuestionCreateServiceRequest.builder()
                .questionNumber(1L)
                .content("좋아하는 과일을 고르세요.")
                .imgAddress(null)
                .type(QuestionCategoryType.MULTIPLE_CHOICE)
                .multipleChoices(multipleChoiceCreateServiceRequests)
                .build();
        QuestionCreateServiceRequest questionCreateServiceRequestByShortForm1 = QuestionCreateServiceRequest.builder()
                .questionNumber(2L)
                .content("사과를 좋아하는 이유는 무엇인가요?")
                .imgAddress(null)
                .type(QuestionCategoryType.SHORT_FORM)
                .multipleChoices(null)
                .build();
        QuestionCreateServiceRequest questionCreateServiceRequestByShortForm2 = QuestionCreateServiceRequest.builder()
                .questionNumber(3L)
                .content("바나나를 좋아하는 이유는 무엇인가요?")
                .imgAddress(null)
                .type(QuestionCategoryType.SHORT_FORM)
                .multipleChoices(null)
                .build();
        List<QuestionCreateServiceRequest> questionCreateServiceRequests1 = List.of(questionCreateServiceRequestByMultipleChoice,
                questionCreateServiceRequestByShortForm1,
                questionCreateServiceRequestByShortForm2);
        List<QuestionCreateServiceRequest> questionCreateServiceRequests2 = List.of(questionCreateServiceRequestByMultipleChoice);
        List<QuestionCreateServiceRequest> questionCreateServiceRequests3 = List.of(questionCreateServiceRequestByShortForm1);

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
                .count(10)
                .build();
        List<GiveawayAssignServiceRequest> giveawayAssignServiceRequests = List.of(giveawayAssignServiceRequest);

        LocalDateTime now = LocalDateTime.now();
        SurveyCreateServiceRequest surveyCreateServiceRequest1 = SurveyCreateServiceRequest.builder()
                .title("제목 1")
                .startTime(now.minusDays(1))
                .endTime(now.plusDays(1))
                .headCount(0)
                .surveyTarget(List.of(AccountCodeType.MAN, AccountCodeType.WOMAN, AccountCodeType.TEENS))
                .closedHeadCount(100)
                .type(SurveyCategoryType.NORMAL)
                .questions(questionCreateServiceRequests1)
                .giveaways(giveawayAssignServiceRequests)
                .build();

        SurveyCreateServiceRequest surveyCreateServiceRequest2 = SurveyCreateServiceRequest.builder()
                .title("제목 2")
                .startTime(now.minusDays(1))
                .endTime(now.plusDays(2))
                .headCount(0)
                .surveyTarget(List.of(AccountCodeType.MAN, AccountCodeType.WOMAN, AccountCodeType.TWENTIES))
                .closedHeadCount(100)
                .type(SurveyCategoryType.NORMAL)
                .questions(questionCreateServiceRequests2)
                .giveaways(giveawayAssignServiceRequests)
                .build();
        SurveyCreateServiceRequest surveyCreateServiceRequest3 = SurveyCreateServiceRequest.builder()
                .title("제목 3")
                .startTime(now.minusDays(1))
                .endTime(now.plusDays(3))
                .headCount(0)
                .surveyTarget(List.of(AccountCodeType.MAN, AccountCodeType.WOMAN, AccountCodeType.THIRTIES))
                .closedHeadCount(100)
                .type(SurveyCategoryType.NORMAL)
                .questions(questionCreateServiceRequests3)
                .giveaways(giveawayAssignServiceRequests)
                .build();

        SurveyResponse saveSurvey1 = surveyService.addSurvey(surveyCreateServiceRequest1);
        surveyRepository.findById(saveSurvey1.getId()).get().toOpen();
        SurveyResponse saveSurvey2 = surveyService.addSurvey(surveyCreateServiceRequest2);
        surveyRepository.findById(saveSurvey2.getId()).get().toOpen();
        SurveyResponse saveSurvey3 = surveyService.addSurvey(surveyCreateServiceRequest3);
        surveyRepository.findById(saveSurvey3.getId()).get().toOpen();

        // when
        List<AbstractSurveyResponse> recommendNormalSurvey = surveyService.getRecommendShortTimeSurveyForAnonymous();

        // then
        assertThat(recommendNormalSurvey.size()).isEqualTo(3);

        List<AbstractSurveyResponse> sortedSurveyResponses = recommendNormalSurvey.stream()
                .sorted(Comparator.comparing(AbstractSurveyResponse::getRequiredTimeInSeconds))
                .toList();
        boolean sameOrder = true;
        for (int i = 0; i < recommendNormalSurvey.size(); i++) {
            if (recommendNormalSurvey.get(i).getId() != sortedSurveyResponses.get(i).getId())
                sameOrder = false;
        }
        assertThat(sameOrder).isTrue();
    }

    @DisplayName("일반(타임어택) 설문 추천은 마감시간이 짧은 순으로 설문을 추천 받는다.(로그인된 유저)")
    @WithMockUser(username = "duswo0624@naver.com")
    @Test
    void getNormalRecommendSurveyOrderByEndTime() {
        // given
        String email = "duswo0624@naver.com";
        AccountCodeType userGender = AccountCodeType.MAN;
        AccountCodeType userAge = AccountCodeType.TWENTIES;
        AccountCreateServiceRequest accountCreateServiceRequest = getAccountCreateServiceRequest(
                email, "1234", userGender, userAge, "최연재", "010-1234-5678",
                LocalDate.of(1997, Month.JUNE, 24));
        accountService.signup(accountCreateServiceRequest);

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
                .count(10)
                .build();
        List<GiveawayAssignServiceRequest> giveawayAssignServiceRequests = List.of(giveawayAssignServiceRequest);

        LocalDateTime now = LocalDateTime.now();
        SurveyCreateServiceRequest surveyCreateServiceRequest1 = SurveyCreateServiceRequest.builder()
                .title("당신은 어떤 과일을 좋아하나요?")
                .startTime(now.minusDays(1))
                .endTime(now.plusDays(1))
                .headCount(0)
                .surveyTarget(List.of(userGender, userAge))
                .closedHeadCount(100)
                .type(SurveyCategoryType.NORMAL)
                .questions(questionCreateServiceRequests)
                .giveaways(giveawayAssignServiceRequests)
                .build();

        SurveyCreateServiceRequest surveyCreateServiceRequest2 = SurveyCreateServiceRequest.builder()
                .title("당신은 어떤 음료를 좋아하나요?")
                .startTime(now.minusDays(1))
                .endTime(now.plusDays(2))
                .headCount(0)
                .surveyTarget(List.of(userGender, userAge))
                .closedHeadCount(100)
                .type(SurveyCategoryType.NORMAL)
                .questions(questionCreateServiceRequests)
                .giveaways(giveawayAssignServiceRequests)
                .build();
        SurveyCreateServiceRequest surveyCreateServiceRequest3 = SurveyCreateServiceRequest.builder()
                .title("당신은 어떤 케익을 좋아하나요?")
                .startTime(now.minusDays(1))
                .endTime(now.plusDays(3))
                .headCount(0)
                .surveyTarget(List.of(userGender, userAge))
                .closedHeadCount(100)
                .type(SurveyCategoryType.NORMAL)
                .questions(questionCreateServiceRequests)
                .giveaways(giveawayAssignServiceRequests)
                .build();

        SurveyResponse saveSurvey1 = surveyService.addSurvey(surveyCreateServiceRequest1);
        surveyRepository.findById(saveSurvey1.getId()).get().toOpen();
        SurveyResponse saveSurvey2 = surveyService.addSurvey(surveyCreateServiceRequest2);
        surveyRepository.findById(saveSurvey2.getId()).get().toOpen();
        SurveyResponse saveSurvey3 = surveyService.addSurvey(surveyCreateServiceRequest3);
        surveyRepository.findById(saveSurvey3.getId()).get().toOpen();

        // when
        List<AbstractSurveyResponse> recommendNormalSurvey = surveyService.getRecommendNormalSurveyForUser();

        // then
        assertThat(recommendNormalSurvey.size()).isEqualTo(3);

        List<AbstractSurveyResponse> sortedSurveyResponses = recommendNormalSurvey.stream()
                .sorted(Comparator.comparing(AbstractSurveyResponse::getEndTime))
                .toList();
        boolean sameOrder = true;
        for (int i = 0; i < recommendNormalSurvey.size(); i++) {
            if (recommendNormalSurvey.get(i).getId() != sortedSurveyResponses.get(i).getId())
                sameOrder = false;
        }
        assertThat(sameOrder).isTrue();
    }

    @DisplayName("즉시당첨(INSTANT_WIN) 설문 추천은 당첨 확률이 높은 순으로 설문을 추천 받는다.(로그인된 유저)")
    @WithMockUser(username = "duswo0624@naver.com")
    @Test
    void getInstantWinRecommendSurveyOrderByWinningPercent() {
        // given
        String email = "duswo0624@naver.com";
        AccountCodeType userGender = AccountCodeType.MAN;
        AccountCodeType userAge = AccountCodeType.TWENTIES;
        AccountCreateServiceRequest accountCreateServiceRequest = getAccountCreateServiceRequest(
                email, "1234", userGender, userAge, "최연재", "010-1234-5678",
                LocalDate.of(1997, Month.JUNE, 24));
        accountService.signup(accountCreateServiceRequest);

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
        GiveawayAssignServiceRequest giveawayAssignServiceRequest1 = GiveawayAssignServiceRequest.builder()
                .id(giveawayId)
                .count(10)
                .build();
        List<GiveawayAssignServiceRequest> giveawayAssignServiceRequests1 = List.of(giveawayAssignServiceRequest1);

        LocalDateTime now = LocalDateTime.now();
        SurveyCreateServiceRequest surveyCreateServiceRequest1 = SurveyCreateServiceRequest.builder()
                .title("당신은 어떤 과일을 좋아하나요?")
                .startTime(now.minusDays(1))
                .endTime(now.plusDays(1))
                .headCount(0)
                .surveyTarget(List.of(userGender, userAge))
                .closedHeadCount(100)
                .type(SurveyCategoryType.INSTANT_WIN)
                .questions(questionCreateServiceRequests)
                .giveaways(giveawayAssignServiceRequests1)
                .build();

        GiveawayAssignServiceRequest giveawayAssignServiceRequest2 = GiveawayAssignServiceRequest.builder()
                .id(giveawayId)
                .count(30)
                .build();
        List<GiveawayAssignServiceRequest> giveawayAssignServiceRequests2 = List.of(giveawayAssignServiceRequest2);
        SurveyCreateServiceRequest surveyCreateServiceRequest2 = SurveyCreateServiceRequest.builder()
                .title("당신은 어떤 음료를 좋아하나요?")
                .startTime(now.minusDays(1))
                .endTime(now.plusDays(2))
                .headCount(0)
                .surveyTarget(List.of(userGender, userAge))
                .closedHeadCount(100)
                .type(SurveyCategoryType.INSTANT_WIN)
                .questions(questionCreateServiceRequests)
                .giveaways(giveawayAssignServiceRequests2)
                .build();

        GiveawayAssignServiceRequest giveawayAssignServiceRequest3 = GiveawayAssignServiceRequest.builder()
                .id(giveawayId)
                .count(20)
                .build();
        List<GiveawayAssignServiceRequest> giveawayAssignServiceRequests3 = List.of(giveawayAssignServiceRequest3);
        SurveyCreateServiceRequest surveyCreateServiceRequest3 = SurveyCreateServiceRequest.builder()
                .title("당신은 어떤 케익을 좋아하나요?")
                .startTime(now.minusDays(1))
                .endTime(now.plusDays(3))
                .headCount(0)
                .surveyTarget(List.of(userGender, userAge))
                .closedHeadCount(100)
                .type(SurveyCategoryType.INSTANT_WIN)
                .questions(questionCreateServiceRequests)
                .giveaways(giveawayAssignServiceRequests3)
                .build();

        SurveyResponse saveSurvey1 = surveyService.addSurvey(surveyCreateServiceRequest1);
        surveyRepository.findById(saveSurvey1.getId()).get().toOpen();
        SurveyResponse saveSurvey2 = surveyService.addSurvey(surveyCreateServiceRequest2);
        surveyRepository.findById(saveSurvey2.getId()).get().toOpen();
        SurveyResponse saveSurvey3 = surveyService.addSurvey(surveyCreateServiceRequest3);
        surveyRepository.findById(saveSurvey3.getId()).get().toOpen();

        // when
        List<AbstractSurveyResponse> recommendInstantSurvey = surveyService.getRecommendInstantSurveyForUser();

        // then
        assertThat(recommendInstantSurvey.size()).isEqualTo(3);

        List<AbstractSurveyResponse> sortedSurveyResponses = surveyService.getRecommendInstantSurveyForUser().stream()
                .sorted(Comparator.comparing(AbstractSurveyResponse::getWinningPercent).reversed())
                .toList();

        boolean sameOrder = true;
        for (int i = 0; i < recommendInstantSurvey.size(); i++) {
            if (recommendInstantSurvey.get(i).getId() != sortedSurveyResponses.get(i).getId())
                sameOrder = false;
        }
        assertThat(sameOrder).isTrue();
    }

    @DisplayName("설문 소요 시간을 활용한 설문 추천은 설문 추천 시간이 짧은 설문 순서대로 추천 받는다.(로그인된 유저)")
    @WithMockUser(username = "duswo0624@naver.com")
    @Test
    void getShortTimeRecommendSurveyOrderByRequiredTime() {
        // given
        String email = "duswo0624@naver.com";
        AccountCodeType userGender = AccountCodeType.MAN;
        AccountCodeType userAge = AccountCodeType.TWENTIES;
        AccountCreateServiceRequest accountCreateServiceRequest = getAccountCreateServiceRequest(
                email, "1234", userGender, userAge, "최연재", "010-1234-5678",
                LocalDate.of(1997, Month.JUNE, 24));
        accountService.signup(accountCreateServiceRequest);

        MultipleChoiceCreateServiceRequest multipleChoiceCreateServiceRequest1 = MultipleChoiceCreateServiceRequest.builder()
                .content("사과")
                .linkNumber(2L)
                .build();
        MultipleChoiceCreateServiceRequest multipleChoiceCreateServiceRequest2 = MultipleChoiceCreateServiceRequest.builder()
                .content("바나나")
                .linkNumber(3L)
                .build();
        List<MultipleChoiceCreateServiceRequest> multipleChoiceCreateServiceRequests = List.of(multipleChoiceCreateServiceRequest1, multipleChoiceCreateServiceRequest2);

        QuestionCreateServiceRequest questionCreateServiceRequestByMultipleChoice = QuestionCreateServiceRequest.builder()
                .questionNumber(1L)
                .content("좋아하는 과일을 고르세요.")
                .imgAddress(null)
                .type(QuestionCategoryType.MULTIPLE_CHOICE)
                .multipleChoices(multipleChoiceCreateServiceRequests)
                .build();
        QuestionCreateServiceRequest questionCreateServiceRequestByShortForm1 = QuestionCreateServiceRequest.builder()
                .questionNumber(2L)
                .content("사과를 좋아하는 이유는 무엇인가요?")
                .imgAddress(null)
                .type(QuestionCategoryType.SHORT_FORM)
                .multipleChoices(null)
                .build();
        QuestionCreateServiceRequest questionCreateServiceRequestByShortForm2 = QuestionCreateServiceRequest.builder()
                .questionNumber(3L)
                .content("바나나를 좋아하는 이유는 무엇인가요?")
                .imgAddress(null)
                .type(QuestionCategoryType.SHORT_FORM)
                .multipleChoices(null)
                .build();
        List<QuestionCreateServiceRequest> questionCreateServiceRequests1 = List.of(questionCreateServiceRequestByMultipleChoice,
                questionCreateServiceRequestByShortForm1,
                questionCreateServiceRequestByShortForm2);
        List<QuestionCreateServiceRequest> questionCreateServiceRequests2 = List.of(questionCreateServiceRequestByMultipleChoice);
        List<QuestionCreateServiceRequest> questionCreateServiceRequests3 = List.of(questionCreateServiceRequestByShortForm1);

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
                .count(10)
                .build();
        List<GiveawayAssignServiceRequest> giveawayAssignServiceRequests = List.of(giveawayAssignServiceRequest);

        LocalDateTime now = LocalDateTime.now();
        SurveyCreateServiceRequest surveyCreateServiceRequest1 = SurveyCreateServiceRequest.builder()
                .title("제목 1")
                .startTime(now.minusDays(1))
                .endTime(now.plusDays(1))
                .headCount(0)
                .surveyTarget(List.of(userGender, userAge))
                .closedHeadCount(100)
                .type(SurveyCategoryType.NORMAL)
                .questions(questionCreateServiceRequests1)
                .giveaways(giveawayAssignServiceRequests)
                .build();

        SurveyCreateServiceRequest surveyCreateServiceRequest2 = SurveyCreateServiceRequest.builder()
                .title("제목 2")
                .startTime(now.minusDays(1))
                .endTime(now.plusDays(2))
                .headCount(0)
                .surveyTarget(List.of(userGender, userAge))
                .closedHeadCount(100)
                .type(SurveyCategoryType.NORMAL)
                .questions(questionCreateServiceRequests2)
                .giveaways(giveawayAssignServiceRequests)
                .build();
        SurveyCreateServiceRequest surveyCreateServiceRequest3 = SurveyCreateServiceRequest.builder()
                .title("제목 3")
                .startTime(now.minusDays(1))
                .endTime(now.plusDays(3))
                .headCount(0)
                .surveyTarget(List.of(userGender, userAge))
                .closedHeadCount(100)
                .type(SurveyCategoryType.NORMAL)
                .questions(questionCreateServiceRequests3)
                .giveaways(giveawayAssignServiceRequests)
                .build();

        SurveyResponse saveSurvey1 = surveyService.addSurvey(surveyCreateServiceRequest1);
        surveyRepository.findById(saveSurvey1.getId()).get().toOpen();
        SurveyResponse saveSurvey2 = surveyService.addSurvey(surveyCreateServiceRequest2);
        surveyRepository.findById(saveSurvey2.getId()).get().toOpen();
        SurveyResponse saveSurvey3 = surveyService.addSurvey(surveyCreateServiceRequest3);
        surveyRepository.findById(saveSurvey3.getId()).get().toOpen();

        // when
        List<AbstractSurveyResponse> recommendNormalSurvey = surveyService.getRecommendShortTimeSurveyForUser();

        // then
        assertThat(recommendNormalSurvey.size()).isEqualTo(3);

        List<AbstractSurveyResponse> sortedSurveyResponses = recommendNormalSurvey.stream()
                .sorted(Comparator.comparing(AbstractSurveyResponse::getRequiredTimeInSeconds))
                .toList();
        boolean sameOrder = true;
        for (int i = 0; i < recommendNormalSurvey.size(); i++) {
            if (recommendNormalSurvey.get(i).getId() != sortedSurveyResponses.get(i).getId())
                sameOrder = false;
        }
        assertThat(sameOrder).isTrue();
    }

    private void setAuthority() {
        Authority userAuthority = Authority.builder()
                .type(AuthorityType.ROLE_USER)
                .build();
        authorityRepository.save(userAuthority);
    }

    @DisplayName("설문 ID를 사용해 설문 상세정보를 조회한다.")
    @WithMockUser(username = "duswo0624@naver.com")
    @Test
    void getSurveyDetail() {
        // given
        String email = "duswo0624@naver.com";
        String password = "1234";
        AccountCodeType gender = AccountCodeType.MAN;
        AccountCodeType age = AccountCodeType.TWENTIES;
        String name = "최연재";
        String phoneNumber = "010-1234-5678";
        LocalDate birthday = LocalDate.of(1997, Month.JUNE, 24);
        AccountCreateServiceRequest accountCreateServiceRequest = getAccountCreateServiceRequest(
                email, password, gender, age, name, phoneNumber, birthday);
        accountService.signup(accountCreateServiceRequest);

        MultipleChoiceCreateServiceRequest multipleChoiceCreateServiceRequest1 = MultipleChoiceCreateServiceRequest.builder()
                .content("사과")
                .linkNumber(2L)
                .build();
        MultipleChoiceCreateServiceRequest multipleChoiceCreateServiceRequest2 = MultipleChoiceCreateServiceRequest.builder()
                .content("바나나")
                .linkNumber(3L)
                .build();
        List<MultipleChoiceCreateServiceRequest> multipleChoiceCreateServiceRequests = List.of(multipleChoiceCreateServiceRequest1, multipleChoiceCreateServiceRequest2);

        QuestionCreateServiceRequest questionCreateServiceRequestByMultipleChoice = QuestionCreateServiceRequest.builder()
                .questionNumber(1L)
                .content("좋아하는 과일을 고르세요.")
                .imgAddress(null)
                .type(QuestionCategoryType.MULTIPLE_CHOICE)
                .multipleChoices(multipleChoiceCreateServiceRequests)
                .build();
        QuestionCreateServiceRequest questionCreateServiceRequestByShortForm1 = QuestionCreateServiceRequest.builder()
                .questionNumber(2L)
                .content("사과를 좋아하는 이유는 무엇인가요?")
                .imgAddress(null)
                .type(QuestionCategoryType.SHORT_FORM)
                .multipleChoices(null)
                .build();
        QuestionCreateServiceRequest questionCreateServiceRequestByShortForm2 = QuestionCreateServiceRequest.builder()
                .questionNumber(3L)
                .content("바나나를 좋아하는 이유는 무엇인가요?")
                .imgAddress(null)
                .type(QuestionCategoryType.SHORT_FORM)
                .multipleChoices(null)
                .build();
        List<QuestionCreateServiceRequest> questionCreateServiceRequests = List.of(questionCreateServiceRequestByMultipleChoice,
                questionCreateServiceRequestByShortForm1,
                questionCreateServiceRequestByShortForm2);

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
                .count(10)
                .build();
        List<GiveawayAssignServiceRequest> giveawayAssignServiceRequests = List.of(giveawayAssignServiceRequest);

        String title = "당신은 어떤 과일을 좋아하나요?";
        SurveyCategoryType surveyCategoryType = SurveyCategoryType.INSTANT_WIN;
        int closedHeadCount = 100;
        LocalDateTime startTime = LocalDateTime.now().minusDays(1);
        LocalDateTime endTime = LocalDateTime.now().plusDays(1);
        List<AccountCodeType> targets = List.of(AccountCodeType.MAN, AccountCodeType.WOMAN,
                AccountCodeType.UNDER_TEENS, AccountCodeType.TEENS, AccountCodeType.TWENTIES,
                AccountCodeType.THIRTIES, AccountCodeType.FORTIES, AccountCodeType.FIFTIES,
                AccountCodeType.SIXTIES);
        SurveyCreateServiceRequest surveyCreateServiceRequest = SurveyCreateServiceRequest.builder()
                .title(title)
                .startTime(startTime)
                .endTime(endTime)
                .headCount(0)
                .surveyTarget(targets)
                .closedHeadCount(closedHeadCount)
                .type(surveyCategoryType)
                .questions(questionCreateServiceRequests)
                .giveaways(giveawayAssignServiceRequests)
                .build();

        SurveyResponse saveSurvey = surveyService.addSurvey(surveyCreateServiceRequest);
        // when
        AbstractSurveyResponse response = surveyService.getSurveyDetail(saveSurvey.getId());
        // then
        assertThat(response).isNotNull();
        assertThat(response.getSurveyCategoryType()).isEqualTo(surveyCategoryType);
        assertThat(response.getTitle()).isEqualTo(title);
        assertThat(response.getStartTime()).isEqualTo(startTime);
        assertThat(response.getEndTime()).isEqualTo(endTime);
        assertThat(response.getHeadCount()).isEqualTo(0);
        assertThat(response.getClosedHeadCount()).isEqualTo(closedHeadCount);
    }

    private static AccountCreateServiceRequest getAccountCreateServiceRequest(String email,
            String password, AccountCodeType gender, AccountCodeType age, String name,
            String phoneNumber, LocalDate birthday) {
        AccountCreateServiceRequest accountCreateServiceRequest = AccountCreateServiceRequest.builder()
                .email(email)
                .password(password)
                .gender(gender)
                .age(age)
                .name(name)
                .phoneNumber(phoneNumber)
                .birthday(birthday)
                .build();
        return accountCreateServiceRequest;
    }

    @DisplayName("설문 응답 로그들을 조회한다.")
    @WithMockUser(username = "duswo0624@naver.com")
    @TestFactory
    Collection<DynamicTest> getSurveyAnswersWithInstantWin() throws SchedulerException, InterruptedException {
        // given
        String email1 = "duswo0624@naver.com";
        AccountCreateServiceRequest accountCreateServiceRequest1 = getAccountCreateServiceRequest(
                email1, "1234", AccountCodeType.MAN, AccountCodeType.TWENTIES, "최연재", "010-1111-2222", LocalDate.of(1997, Month.JUNE, 24));
        accountService.signup(accountCreateServiceRequest1);

        String email2 = "choi0624@naver.com";
        String password2 = "1234";
        AccountCreateServiceRequest accountCreateServiceRequest2 = getAccountCreateServiceRequest(
                email2, "1234", AccountCodeType.WOMAN, AccountCodeType.TWENTIES, "최연못", "010-3333-4444", LocalDate.of(1997, Month.JUNE, 24));
        accountService.signup(accountCreateServiceRequest2);

        QuestionCreateServiceRequest questionCreateServiceRequestByShortForm = QuestionCreateServiceRequest.builder()
                .questionNumber(1L)
                .content("사과를 좋아하는 이유는 무엇인가요?")
                .imgAddress(null)
                .type(QuestionCategoryType.SHORT_FORM)
                .multipleChoices(null)
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

        String title = "과일에 대한 설문";
        SurveyCategoryType surveyCategoryType = SurveyCategoryType.INSTANT_WIN;
        int closedHeadCount = 2;
        LocalDateTime startTime = LocalDateTime.now().minusDays(1);
        LocalDateTime endTime = LocalDateTime.now().plusDays(1);
        List<AccountCodeType> targets = List.of(AccountCodeType.MAN, AccountCodeType.WOMAN,
                AccountCodeType.UNDER_TEENS, AccountCodeType.TEENS, AccountCodeType.TWENTIES,
                AccountCodeType.THIRTIES, AccountCodeType.FORTIES, AccountCodeType.FIFTIES,
                AccountCodeType.SIXTIES);
        SurveyCreateServiceRequest surveyCreateServiceRequest = SurveyCreateServiceRequest.builder()
                .title(title)
                .startTime(startTime)
                .endTime(endTime)
                .headCount(0)
                .surveyTarget(targets)
                .closedHeadCount(closedHeadCount)
                .type(surveyCategoryType)
                .questions(questionCreateServiceRequests)
                .giveaways(giveawayAssignServiceRequests)
                .build();

        SurveyResponse saveSurvey = surveyService.addSurvey(surveyCreateServiceRequest);
        Long surveyId = saveSurvey.getId();
        Survey survey = surveyRepository.findById(surveyId).get();
        survey.toOpen();

        Long questionId = saveSurvey.getQuestions().get(0).getId();
        QuestionAnswerCreateServiceRequest questionAnswerCreateServiceRequest
                = QuestionAnswerCreateServiceRequest
                .builder()
                .questionId(questionId)
                .multipleChoiceAnswer(null)
                .shorFormAnswer("사과가 맛있어서 좋아합니다.")
                .build();

        return List.of(
                DynamicTest.dynamicTest("응답한 사람이 0명일 때의 응답 로그를 조회한다.", () -> {
                    List<SurveyAnswerResponse> surveyAnswerResponses1 = surveyService.getSurveyAnswers(
                            surveyId);

                    assertThat(surveyAnswerResponses1.size()).isZero();
                }),
                DynamicTest.dynamicTest("응답한 사람이 1명일 때의 응답 로그를 조회한다.", () -> {
                    questionAnswerService.addQuestionAnswer(LocalDateTime.now(),
                            survey.getId(),
                            List.of(questionAnswerCreateServiceRequest));
                    List<SurveyAnswerResponse> surveyAnswerResponses2 = surveyService.getSurveyAnswers(
                            surveyId);

                    assertThat(surveyAnswerResponses2.size()).isOne();
                }),
                DynamicTest.dynamicTest("응답한 사람이 2명일 때의 응답 로그를 조회한다.", () -> {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(email2, password2);

                    Authentication authentication = authenticationManagerBuilder.getObject()
                            .authenticate(authenticationToken);
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    questionAnswerService.addQuestionAnswer(LocalDateTime.now(),
                            surveyId,
                            List.of(questionAnswerCreateServiceRequest));
                    List<SurveyAnswerResponse> surveyAnswerResponses3 = surveyService.getSurveyAnswers(
                            saveSurvey.getId());

                    assertThat(surveyAnswerResponses3.size()).isEqualTo(2);
                })
        );
    }

    @DisplayName("설문 응답 로그들을 조회한다.")
    @WithMockUser(username = "duswo0624@naver.com")
    @TestFactory
    Collection<DynamicTest> getSurveyAnswersWithNormal() throws SchedulerException, InterruptedException {
        // given
        String email1 = "duswo0624@naver.com";
        AccountCreateServiceRequest accountCreateServiceRequest1 = getAccountCreateServiceRequest(
                email1, "1234", AccountCodeType.MAN, AccountCodeType.TWENTIES, "최연재", "010-1111-2222", LocalDate.of(1997, Month.JUNE, 24));
        accountService.signup(accountCreateServiceRequest1);

        String email2 = "choi0624@naver.com";
        String password2 = "1234";
        AccountCreateServiceRequest accountCreateServiceRequest2 = getAccountCreateServiceRequest(
                email2, "1234", AccountCodeType.WOMAN, AccountCodeType.TWENTIES, "최연못", "010-3333-4444", LocalDate.of(1997, Month.JUNE, 24));
        accountService.signup(accountCreateServiceRequest2);

        QuestionCreateServiceRequest questionCreateServiceRequestByShortForm = QuestionCreateServiceRequest.builder()
                .questionNumber(1L)
                .content("사과를 좋아하는 이유는 무엇인가요?")
                .imgAddress(null)
                .type(QuestionCategoryType.SHORT_FORM)
                .multipleChoices(null)
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

        String title = "과일에 대한 설문";
        SurveyCategoryType surveyCategoryType = SurveyCategoryType.NORMAL;
        int closedHeadCount = 2;
        LocalDateTime startTime = LocalDateTime.now().minusDays(1);
        LocalDateTime endTime = LocalDateTime.now().plusDays(1);
        List<AccountCodeType> targets = List.of(AccountCodeType.MAN, AccountCodeType.WOMAN,
                AccountCodeType.UNDER_TEENS, AccountCodeType.TEENS, AccountCodeType.TWENTIES,
                AccountCodeType.THIRTIES, AccountCodeType.FORTIES, AccountCodeType.FIFTIES,
                AccountCodeType.SIXTIES);
        SurveyCreateServiceRequest surveyCreateServiceRequest = SurveyCreateServiceRequest.builder()
                .title(title)
                .startTime(startTime)
                .endTime(endTime)
                .headCount(0)
                .surveyTarget(targets)
                .closedHeadCount(closedHeadCount)
                .type(surveyCategoryType)
                .questions(questionCreateServiceRequests)
                .giveaways(giveawayAssignServiceRequests)
                .build();

        SurveyResponse saveSurvey = surveyService.addSurvey(surveyCreateServiceRequest);
        Long surveyId = saveSurvey.getId();
        Survey survey = surveyRepository.findById(surveyId).get();
        survey.toOpen();

        Long questionId = saveSurvey.getQuestions().get(0).getId();
        QuestionAnswerCreateServiceRequest questionAnswerCreateServiceRequest
                = QuestionAnswerCreateServiceRequest
                .builder()
                .questionId(questionId)
                .multipleChoiceAnswer(null)
                .shorFormAnswer("사과가 맛있어서 좋아합니다.")
                .build();

        return List.of(
                DynamicTest.dynamicTest("응답한 사람이 0명일 때의 응답 로그를 조회한다.", () -> {
                    List<SurveyAnswerResponse> surveyAnswerResponses1 = surveyService.getSurveyAnswers(
                            surveyId);

                    assertThat(surveyAnswerResponses1.size()).isZero();
                }),
                DynamicTest.dynamicTest("응답한 사람이 1명일 때의 응답 로그를 조회한다.", () -> {
                    questionAnswerService.addQuestionAnswer(LocalDateTime.now(),
                            survey.getId(),
                            List.of(questionAnswerCreateServiceRequest));
                    List<SurveyAnswerResponse> surveyAnswerResponses2 = surveyService.getSurveyAnswers(
                            surveyId);

                    assertThat(surveyAnswerResponses2.size()).isOne();
                }),
                DynamicTest.dynamicTest("응답한 사람이 2명일 때의 응답 로그를 조회한다.", () -> {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(email2, password2);

                    Authentication authentication = authenticationManagerBuilder.getObject()
                            .authenticate(authenticationToken);
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    questionAnswerService.addQuestionAnswer(LocalDateTime.now(),
                            surveyId,
                            List.of(questionAnswerCreateServiceRequest));
                    List<SurveyAnswerResponse> surveyAnswerResponses3 = surveyService.getSurveyAnswers(
                            saveSurvey.getId());

                    assertThat(surveyAnswerResponses3.size()).isEqualTo(2);
                })
        );
    }

    @DisplayName("설문 제작 시 설문 마감 시간은 시작 시간보다 커야한다.")
    @WithMockUser("duswo0624@naver.com")
    @Test
    void endTimeShouldBeLateThanStartTime() {
        // given
        String email = "duswo0624@naver.com";
        AccountCreateServiceRequest accountCreateServiceRequest = getAccountCreateServiceRequest(
                email, "1234", AccountCodeType.MAN, AccountCodeType.TWENTIES, "최연재", "010-1234-5678",
                LocalDate.of(1997, Month.JUNE, 24));
        accountService.signup(accountCreateServiceRequest);

        LocalDateTime startDateTime = LocalDateTime.now().plusDays(2);
        LocalDateTime endDateTime = LocalDateTime.now().plusDays(1);
        SurveyCreateServiceRequest surveyCreateServiceRequest = SurveyCreateServiceRequest.builder()
                .title("어떤 외국어를 배우고 싶습니까?")
                .startTime(startDateTime)
                .endTime(endDateTime)
                .headCount(0)
                .surveyTarget(List.of(AccountCodeType.MAN, AccountCodeType.WOMAN))
                .closedHeadCount(100)
                .build();

        // when // then
        assertThatThrownBy(() -> surveyService.addSurvey(surveyCreateServiceRequest))
                .isInstanceOf(BaseException.class)
                .hasMessage("설문 마감시간은 시작 시간보다 커야합니다.");
    }

    @DisplayName("설문 제작 시 설문 마감 시간은 현재 시간보다 커야한다.")
    @WithMockUser("duswo0624@naver.com")
    @Test
    void endTimeShouldBeLateThanNowTime() {
        // given
        String email = "duswo0624@naver.com";
        AccountCreateServiceRequest accountCreateServiceRequest = getAccountCreateServiceRequest(
                email, "1234", AccountCodeType.MAN, AccountCodeType.TWENTIES, "최연재", "010-1234-5678",
                LocalDate.of(1997, Month.JUNE, 24));
        accountService.signup(accountCreateServiceRequest);

        LocalDateTime startDateTime = LocalDateTime.now().minusDays(1);
        LocalDateTime endDateTime = LocalDateTime.now();
        SurveyCreateServiceRequest surveyCreateServiceRequest = SurveyCreateServiceRequest.builder()
                .title("어떤 외국어를 배우고 싶습니까?")
                .startTime(startDateTime)
                .endTime(endDateTime)
                .headCount(0)
                .surveyTarget(List.of(AccountCodeType.MAN, AccountCodeType.WOMAN))
                .closedHeadCount(100)
                .build();

        // when // then
        assertThatThrownBy(() -> surveyService.addSurvey(surveyCreateServiceRequest))
                .isInstanceOf(BaseException.class)
                .hasMessage("설문 마감시간은 현재 시간보다 커야합니다.");
    }

    @DisplayName("설문 번호가 올바르지 않은 경우 설문 답변 로그들을 받을 수 없다.")
    @WithMockUser
    @Test
    void cannotGetSurveyAnswersWithInvalidSurveyId() {
        // given
        Long invalidSurveyId = 1L;

        // when // then
        assertThatThrownBy(() -> surveyService.getSurveyAnswers(invalidSurveyId))
                .isInstanceOf(BaseException.class)
                .hasMessage("없는 설문입니다.");
    }

    @DisplayName("설문 번호가 올바르지 않은 경우 설문 디테일 정보를 받을 수 없다.")
    @WithMockUser
    @Test
    void cannotGetSurveyDetailWithInvalidSurveyId() {
        // given
        Long invalidSurveyId = 1L;

        // when // then
        assertThatThrownBy(() -> surveyService.getSurveyDetail(invalidSurveyId))
                .isInstanceOf(BaseException.class)
                .hasMessage("없는 설문입니다.");
    }

    @DisplayName("설문 작성자가 회원 탈퇴를 한 경우 설문 디테일 정보를 가져올 수 없다.")
    @WithMockUser(username = "writer@naver.com")
    @Test
    void cannotGetSurveyDetailWithInvalidWriter() {
        // given
        AccountCreateServiceRequest accountCreateServiceRequestForWriter = getAccountCreateServiceRequest(
                "writer@naver.com", "1234", AccountCodeType.MAN, AccountCodeType.TWENTIES, "최연재", "010-1234-5678",
                LocalDate.of(1997, Month.JUNE, 24));

        AccountResponse writer = accountService.signup(accountCreateServiceRequestForWriter);

        String email = "duswo0624@naver.com";
        String password = "1234";
        AccountCreateServiceRequest accountCreateServiceRequest = getAccountCreateServiceRequest(
                email, password, AccountCodeType.MAN, AccountCodeType.TWENTIES, "최연재", "010-9999-8888",
                LocalDate.of(1997, Month.JUNE, 24));
        accountService.signup(accountCreateServiceRequest);

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
                .count(10)
                .build();
        List<GiveawayAssignServiceRequest> giveawayAssignServiceRequests = List.of(giveawayAssignServiceRequest);

        LocalDateTime now = LocalDateTime.now();
        SurveyCreateServiceRequest surveyCreateServiceRequest = SurveyCreateServiceRequest.builder()
                .title("당신은 어떤 과일을 좋아하나요?")
                .startTime(now.minusDays(1))
                .endTime(now.plusDays(1))
                .headCount(0)
                .surveyTarget(List.of(AccountCodeType.MAN, AccountCodeType.WOMAN, AccountCodeType.TEENS))
                .closedHeadCount(100)
                .type(SurveyCategoryType.NORMAL)
                .questions(questionCreateServiceRequests)
                .giveaways(giveawayAssignServiceRequests)
                .build();


        SurveyResponse saveSurvey = surveyService.addSurvey(surveyCreateServiceRequest);
        accountRepository.deleteById(writer.getId());

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, password);

        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when // then
        assertThatThrownBy(() -> surveyService.getSurveyDetail(saveSurvey.getId()))
                .isInstanceOf(BaseException.class)
                .hasMessage("설문 작성자가 존재하지 않습니다.");
    }

    @DisplayName("설문 ID를 이용하여 설문을 가져온다.")
    @WithMockUser(username = "duswo0624@naver.com")
    @Test
    void getSurvey() {
        // given
        String email = "duswo0624@naver.com";
        String password = "1234";
        AccountCreateServiceRequest accountCreateServiceRequest = getAccountCreateServiceRequest(
                email, password, AccountCodeType.MAN, AccountCodeType.TWENTIES, "최연재", "010-9999-8888",
                LocalDate.of(1997, Month.JUNE, 24));
        accountService.signup(accountCreateServiceRequest);

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
                .count(10)
                .build();
        List<GiveawayAssignServiceRequest> giveawayAssignServiceRequests = List.of(giveawayAssignServiceRequest);

        LocalDateTime now = LocalDateTime.now();
        SurveyCreateServiceRequest surveyCreateServiceRequest = SurveyCreateServiceRequest.builder()
                .title("당신은 어떤 과일을 좋아하나요?")
                .startTime(now.minusDays(1))
                .endTime(now.plusDays(1))
                .headCount(0)
                .surveyTarget(List.of(AccountCodeType.MAN, AccountCodeType.WOMAN, AccountCodeType.TEENS))
                .closedHeadCount(100)
                .type(SurveyCategoryType.NORMAL)
                .questions(questionCreateServiceRequests)
                .giveaways(giveawayAssignServiceRequests)
                .build();


        SurveyResponse saveSurvey = surveyService.addSurvey(surveyCreateServiceRequest);
        SurveyResponse getSurvey = surveyService.getSurvey(saveSurvey.getId());
        // when // then
        assertThat(getSurvey.getTitle()).isEqualTo(saveSurvey.getTitle());
    }

    @DisplayName("설문 번호가 올바르지 않은 경우 설문 정보를 받을 수 없다.")
    @WithMockUser
    @Test
    void cannotGetSurveyWithInvalidSurveyId() {
        // given
        Long invalidSurveyId = 1L;

        // when // then
        assertThatThrownBy(() -> surveyService.getSurvey(invalidSurveyId))
                .isInstanceOf(BaseException.class)
                .hasMessage("없는 설문입니다.");
    }

    @DisplayName("자신이 만든 설문을 가져온다.")
    @WithMockUser(username = "duswo0624@naver.com")
    @Test
    void getMySurveys() {
        // given
        String email = "duswo0624@naver.com";
        AccountCreateServiceRequest accountCreateServiceRequest = getAccountCreateServiceRequest(
                email, "1234", AccountCodeType.MAN, AccountCodeType.TWENTIES, "최연재", "010-1234-5678",
                LocalDate.of(1997, Month.JUNE, 24));
        accountService.signup(accountCreateServiceRequest);

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
                .count(10)
                .build();
        List<GiveawayAssignServiceRequest> giveawayAssignServiceRequests = List.of(giveawayAssignServiceRequest);

        LocalDateTime now = LocalDateTime.now();
        String surveyTitle1 = "당신은 어떤 과일을 좋아하나요?";
        SurveyCreateServiceRequest surveyCreateServiceRequest1 = SurveyCreateServiceRequest.builder()
                .title(surveyTitle1)
                .startTime(now.minusDays(1))
                .endTime(now.plusDays(1))
                .headCount(0)
                .surveyTarget(List.of(AccountCodeType.MAN, AccountCodeType.WOMAN, AccountCodeType.TEENS))
                .closedHeadCount(100)
                .type(SurveyCategoryType.NORMAL)
                .questions(questionCreateServiceRequests)
                .giveaways(giveawayAssignServiceRequests)
                .build();

        String surveyTitle2 = "당신은 어떤 음료를 좋아하나요?";
        SurveyCreateServiceRequest surveyCreateServiceRequest2 = SurveyCreateServiceRequest.builder()
                .title(surveyTitle2)
                .startTime(now.minusDays(1))
                .endTime(now.plusDays(2))
                .headCount(0)
                .surveyTarget(List.of(AccountCodeType.MAN, AccountCodeType.WOMAN, AccountCodeType.TWENTIES))
                .closedHeadCount(100)
                .type(SurveyCategoryType.NORMAL)
                .questions(questionCreateServiceRequests)
                .giveaways(giveawayAssignServiceRequests)
                .build();
        String surveyTitle3 = "당신은 어떤 케익을 좋아하나요?";
        SurveyCreateServiceRequest surveyCreateServiceRequest3 = SurveyCreateServiceRequest.builder()
                .title(surveyTitle3)
                .startTime(now.minusDays(1))
                .endTime(now.plusDays(3))
                .headCount(0)
                .surveyTarget(List.of(AccountCodeType.MAN, AccountCodeType.WOMAN, AccountCodeType.THIRTIES))
                .closedHeadCount(100)
                .type(SurveyCategoryType.NORMAL)
                .questions(questionCreateServiceRequests)
                .giveaways(giveawayAssignServiceRequests)
                .build();

        SurveyResponse saveSurvey1 = surveyService.addSurvey(surveyCreateServiceRequest1);
        SurveyResponse saveSurvey2 = surveyService.addSurvey(surveyCreateServiceRequest2);
        SurveyResponse saveSurvey3 = surveyService.addSurvey(surveyCreateServiceRequest3);

        List<AbstractSurveyResponse> mySurveys = surveyService.getMySurveys();
        assertThat(mySurveys.size()).isEqualTo(3);
        assertThat(mySurveys.get(0).getTitle()).isEqualTo(surveyTitle1);
        assertThat(mySurveys.get(1).getTitle()).isEqualTo(surveyTitle2);
        assertThat(mySurveys.get(2).getTitle()).isEqualTo(surveyTitle3);
    }

    @DisplayName("자신이 만든 설문을 가져온다.")
    @WithMockUser(username = "writer@naver.com")
    @Test
    void getAnsweredSurveys() {
        // given
        String writerEmail = "writer@naver.com";
        AccountCreateServiceRequest accountCreateServiceRequest1 = getAccountCreateServiceRequest(
                writerEmail, "1234", AccountCodeType.MAN, AccountCodeType.TWENTIES, "최연재", "010-1234-5678",
                LocalDate.of(1997, Month.JUNE, 24));
        accountService.signup(accountCreateServiceRequest1);

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
                .count(10)
                .build();
        List<GiveawayAssignServiceRequest> giveawayAssignServiceRequests = List.of(giveawayAssignServiceRequest);

        LocalDateTime now = LocalDateTime.now();
        String surveyTitle1 = "당신은 어떤 과일을 좋아하나요?";
        SurveyCreateServiceRequest surveyCreateServiceRequest1 = SurveyCreateServiceRequest.builder()
                .title(surveyTitle1)
                .startTime(now.minusDays(1))
                .endTime(now.plusDays(1))
                .headCount(0)
                .surveyTarget(List.of(AccountCodeType.MAN, AccountCodeType.WOMAN, AccountCodeType.TWENTIES))
                .closedHeadCount(100)
                .type(SurveyCategoryType.NORMAL)
                .questions(questionCreateServiceRequests)
                .giveaways(giveawayAssignServiceRequests)
                .build();

        String surveyTitle2 = "당신은 어떤 음료를 좋아하나요?";
        SurveyCreateServiceRequest surveyCreateServiceRequest2 = SurveyCreateServiceRequest.builder()
                .title(surveyTitle2)
                .startTime(now.minusDays(1))
                .endTime(now.plusDays(2))
                .headCount(0)
                .surveyTarget(List.of(AccountCodeType.MAN, AccountCodeType.WOMAN, AccountCodeType.TWENTIES))
                .closedHeadCount(100)
                .type(SurveyCategoryType.NORMAL)
                .questions(questionCreateServiceRequests)
                .giveaways(giveawayAssignServiceRequests)
                .build();
        String surveyTitle3 = "당신은 어떤 케익을 좋아하나요?";
        SurveyCreateServiceRequest surveyCreateServiceRequest3 = SurveyCreateServiceRequest.builder()
                .title(surveyTitle3)
                .startTime(now.minusDays(1))
                .endTime(now.plusDays(3))
                .headCount(0)
                .surveyTarget(List.of(AccountCodeType.MAN, AccountCodeType.WOMAN, AccountCodeType.TWENTIES))
                .closedHeadCount(100)
                .type(SurveyCategoryType.NORMAL)
                .questions(questionCreateServiceRequests)
                .giveaways(giveawayAssignServiceRequests)
                .build();

        SurveyResponse saveSurvey1 = surveyService.addSurvey(surveyCreateServiceRequest1);
        surveyRepository.findById(saveSurvey1.getId()).get().toOpen();
        SurveyResponse saveSurvey2 = surveyService.addSurvey(surveyCreateServiceRequest2);
        surveyRepository.findById(saveSurvey2.getId()).get().toOpen();
        SurveyResponse saveSurvey3 = surveyService.addSurvey(surveyCreateServiceRequest3);
        surveyRepository.findById(saveSurvey3.getId()).get().toOpen();

        String email = "duswo0624@naver.com";
        String password = "1234";
        AccountCreateServiceRequest accountCreateServiceRequest2 = getAccountCreateServiceRequest(
                email, password, AccountCodeType.MAN, AccountCodeType.TWENTIES, "최연재", "010-9999-8888",
                LocalDate.of(1997, Month.JUNE, 24));
        accountService.signup(accountCreateServiceRequest2);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, password);
        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        QuestionAnswerCreateServiceRequest questionAnswerCreateServiceRequest1
                = QuestionAnswerCreateServiceRequest
                .builder()
                .questionId(saveSurvey1.getQuestions().get(0).getId())
                .multipleChoiceAnswer(null)
                .shorFormAnswer("수박")
                .build();

        QuestionAnswerCreateServiceRequest questionAnswerCreateServiceRequest2
                = QuestionAnswerCreateServiceRequest
                .builder()
                .questionId(saveSurvey2.getQuestions().get(0).getId())
                .multipleChoiceAnswer(null)
                .shorFormAnswer("포카리 스웨트")
                .build();

        QuestionAnswerCreateServiceRequest questionAnswerCreateServiceRequest3
                = QuestionAnswerCreateServiceRequest
                .builder()
                .questionId(saveSurvey3.getQuestions().get(0).getId())
                .multipleChoiceAnswer(null)
                .shorFormAnswer("치즈케익")
                .build();

        questionAnswerService.addQuestionAnswer(LocalDateTime.now(), saveSurvey1.getId(), List.of(questionAnswerCreateServiceRequest1));
        questionAnswerService.addQuestionAnswer(LocalDateTime.now(), saveSurvey2.getId(), List.of(questionAnswerCreateServiceRequest2));
        questionAnswerService.addQuestionAnswer(LocalDateTime.now(), saveSurvey3.getId(), List.of(questionAnswerCreateServiceRequest3));

        List<AbstractSurveyResponse> mySurveys = surveyService.getAnsweredSurveys();
        assertThat(mySurveys.size()).isEqualTo(3);
        assertThat(mySurveys.get(0).getTitle()).isEqualTo(surveyTitle1);
        assertThat(mySurveys.get(1).getTitle()).isEqualTo(surveyTitle2);
        assertThat(mySurveys.get(2).getTitle()).isEqualTo(surveyTitle3);
    }

    @DisplayName("설문타입 , 설문시간 , 설문이력, 로그인여부등이 모두 적합해야 설문응답이 가능하다.")
    @WithMockUser(username = "writer@naver.com")
    @Test
    void checkPossibleAnswerCorrect() {
        // given
        String writerEmail = "writer@naver.com";
        AccountCreateServiceRequest accountCreateServiceRequest1 = getAccountCreateServiceRequest(
                writerEmail, "1234", AccountCodeType.MAN, AccountCodeType.TWENTIES, "최연재", "010-1234-5678",
                LocalDate.of(1997, Month.JUNE, 24));
        accountService.signup(accountCreateServiceRequest1);

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
                .count(10)
                .build();
        List<GiveawayAssignServiceRequest> giveawayAssignServiceRequests = List.of(giveawayAssignServiceRequest);

        LocalDateTime now = LocalDateTime.now();
        String surveyTitle1 = "당신은 어떤 과일을 좋아하나요?";
        SurveyCreateServiceRequest surveyCreateServiceRequest1 = SurveyCreateServiceRequest.builder()
                .title(surveyTitle1)
                .startTime(now.minusDays(1))
                .endTime(now.plusDays(1))
                .headCount(0)
                .surveyTarget(List.of(AccountCodeType.MAN, AccountCodeType.WOMAN, AccountCodeType.TWENTIES))
                .closedHeadCount(100)
                .type(SurveyCategoryType.NORMAL)
                .questions(questionCreateServiceRequests)
                .giveaways(giveawayAssignServiceRequests)
                .build();

        String surveyTitle2 = "당신은 어떤 음료를 좋아하나요?";
        SurveyCreateServiceRequest surveyCreateServiceRequest2 = SurveyCreateServiceRequest.builder()
                .title(surveyTitle2)
                .startTime(now.minusDays(1))
                .endTime(now.plusDays(2))
                .headCount(0)
                .surveyTarget(List.of(AccountCodeType.MAN, AccountCodeType.WOMAN, AccountCodeType.TWENTIES))
                .closedHeadCount(100)
                .type(SurveyCategoryType.NORMAL)
                .questions(questionCreateServiceRequests)
                .giveaways(giveawayAssignServiceRequests)
                .build();
        String surveyTitle3 = "당신은 어떤 케익을 좋아하나요?";
        SurveyCreateServiceRequest surveyCreateServiceRequest3 = SurveyCreateServiceRequest.builder()
                .title(surveyTitle3)
                .startTime(now.minusDays(1))
                .endTime(now.plusDays(3))
                .headCount(0)
                .surveyTarget(List.of(AccountCodeType.MAN, AccountCodeType.WOMAN, AccountCodeType.TWENTIES))
                .closedHeadCount(100)
                .type(SurveyCategoryType.NORMAL)
                .questions(questionCreateServiceRequests)
                .giveaways(giveawayAssignServiceRequests)
                .build();

        SurveyResponse saveSurvey = surveyService.addSurvey(surveyCreateServiceRequest1);
        surveyRepository.findById(saveSurvey.getId()).get().toOpen();

        String email = "duswo0624@naver.com";
        String password = "1234";
        AccountCreateServiceRequest accountCreateServiceRequest2 = getAccountCreateServiceRequest(
                email, password, AccountCodeType.MAN, AccountCodeType.TWENTIES, "최연재", "010-9999-8888",
                LocalDate.of(1997, Month.JUNE, 24));
        accountService.signup(accountCreateServiceRequest2);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, password);
        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertThat(surveyService.getAnswerPossible(saveSurvey.getId())).isEqualTo(
                AnswerPossibleType.CANANSWER);
    }

    @DisplayName("설문시간이전이면 설문응답이 불가능하다.")
    @WithMockUser(username = "writer@naver.com")
    @Test
    void checkPossibleAnswerTimeBefore() {
        // given
        String writerEmail = "writer@naver.com";
        AccountCreateServiceRequest accountCreateServiceRequest1 = getAccountCreateServiceRequest(
                writerEmail, "1234", AccountCodeType.MAN, AccountCodeType.TWENTIES, "최연재", "010-1234-5678",
                LocalDate.of(1997, Month.JUNE, 24));
        accountService.signup(accountCreateServiceRequest1);

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
                .count(10)
                .build();
        List<GiveawayAssignServiceRequest> giveawayAssignServiceRequests = List.of(giveawayAssignServiceRequest);

        LocalDateTime now = LocalDateTime.now();
        String surveyTitle1 = "당신은 어떤 과일을 좋아하나요?";
        SurveyCreateServiceRequest surveyCreateServiceRequest1 = SurveyCreateServiceRequest.builder()
                .title(surveyTitle1)
                .startTime(now.plusDays(1))
                .endTime(now.plusDays(2))
                .headCount(0)
                .surveyTarget(List.of(AccountCodeType.MAN, AccountCodeType.WOMAN, AccountCodeType.TWENTIES))
                .closedHeadCount(100)
                .type(SurveyCategoryType.NORMAL)
                .questions(questionCreateServiceRequests)
                .giveaways(giveawayAssignServiceRequests)
                .build();

        String surveyTitle2 = "당신은 어떤 음료를 좋아하나요?";
        SurveyCreateServiceRequest surveyCreateServiceRequest2 = SurveyCreateServiceRequest.builder()
                .title(surveyTitle2)
                .startTime(now.minusDays(1))
                .endTime(now.plusDays(2))
                .headCount(0)
                .surveyTarget(List.of(AccountCodeType.MAN, AccountCodeType.WOMAN, AccountCodeType.TWENTIES))
                .closedHeadCount(100)
                .type(SurveyCategoryType.NORMAL)
                .questions(questionCreateServiceRequests)
                .giveaways(giveawayAssignServiceRequests)
                .build();
        String surveyTitle3 = "당신은 어떤 케익을 좋아하나요?";
        SurveyCreateServiceRequest surveyCreateServiceRequest3 = SurveyCreateServiceRequest.builder()
                .title(surveyTitle3)
                .startTime(now.plusDays(1))
                .endTime(now.plusDays(3))
                .headCount(0)
                .surveyTarget(List.of(AccountCodeType.MAN, AccountCodeType.WOMAN, AccountCodeType.TWENTIES))
                .closedHeadCount(100)
                .type(SurveyCategoryType.NORMAL)
                .questions(questionCreateServiceRequests)
                .giveaways(giveawayAssignServiceRequests)
                .build();

        SurveyResponse saveSurvey = surveyService.addSurvey(surveyCreateServiceRequest1);
        surveyRepository.findById(saveSurvey.getId()).get().toOpen();

        String email = "duswo0624@naver.com";
        String password = "1234";
        AccountCreateServiceRequest accountCreateServiceRequest2 = getAccountCreateServiceRequest(
                email, password, AccountCodeType.MAN, AccountCodeType.TWENTIES, "최연재", "010-9999-8888",
                LocalDate.of(1997, Month.JUNE, 24));
        accountService.signup(accountCreateServiceRequest2);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, password);
        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertThat(surveyService.getAnswerPossible(saveSurvey.getId())).isEqualTo(
                AnswerPossibleType.TIMEBEFORE);
    }

    @DisplayName("설문시간이후이면 설문응답이 불가능하다.")
    @WithMockUser(username = "writer@naver.com")
    @Test
    void checkPossibleAnswerTimeOver() throws InterruptedException {
        // given
        String writerEmail = "writer@naver.com";
        AccountCreateServiceRequest accountCreateServiceRequest1 = getAccountCreateServiceRequest(
                writerEmail, "1234", AccountCodeType.MAN, AccountCodeType.TWENTIES, "최연재", "010-1234-5678",
                LocalDate.of(1997, Month.JUNE, 24));
        accountService.signup(accountCreateServiceRequest1);

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
                .count(10)
                .build();
        List<GiveawayAssignServiceRequest> giveawayAssignServiceRequests = List.of(giveawayAssignServiceRequest);

        LocalDateTime now = LocalDateTime.now();
        String surveyTitle1 = "당신은 어떤 과일을 좋아하나요?";
        SurveyCreateServiceRequest surveyCreateServiceRequest1 = SurveyCreateServiceRequest.builder()
                .title(surveyTitle1)
                .startTime(now.minusDays(2))
                .endTime(now.plusNanos(1000000L * 1000))
                .headCount(0)
                .surveyTarget(List.of(AccountCodeType.MAN, AccountCodeType.WOMAN, AccountCodeType.TWENTIES))
                .closedHeadCount(100)
                .type(SurveyCategoryType.NORMAL)
                .questions(questionCreateServiceRequests)
                .giveaways(giveawayAssignServiceRequests)
                .build();

        SurveyResponse saveSurvey = surveyService.addSurvey(surveyCreateServiceRequest1);
        Thread.sleep(3000);
        surveyRepository.findById(saveSurvey.getId()).get().toOpen();

        String email = "duswo0624@naver.com";
        String password = "1234";
        AccountCreateServiceRequest accountCreateServiceRequest2 = getAccountCreateServiceRequest(
                email, password, AccountCodeType.MAN, AccountCodeType.TWENTIES, "최연재", "010-9999-8888",
                LocalDate.of(1997, Month.JUNE, 24));
        accountService.signup(accountCreateServiceRequest2);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, password);
        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertThat(surveyService.getAnswerPossible(saveSurvey.getId())).isEqualTo(
                AnswerPossibleType.TIMEOVER);
    }

    @DisplayName("이미 응답한 설문은 응답이 불가능하다.")
    @WithMockUser(username = "writer@naver.com")
    @TestFactory
    Stream<DynamicTest> checkPossibleAnswerDid() throws InterruptedException {
        // given
        String writerEmail = "writer@naver.com";
        AccountCreateServiceRequest accountCreateServiceRequest1 = getAccountCreateServiceRequest(
                writerEmail, "1234", AccountCodeType.MAN, AccountCodeType.TWENTIES, "최연구", "010-1234-5678",
                LocalDate.of(1997, Month.JUNE, 24));
        accountService.signup(accountCreateServiceRequest1);

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
                .count(10)
                .build();
        List<GiveawayAssignServiceRequest> giveawayAssignServiceRequests = List.of(giveawayAssignServiceRequest);

        LocalDateTime now = LocalDateTime.now();
        String surveyTitle1 = "당신은 어떤 과일을 좋아하나요?";
        SurveyCreateServiceRequest surveyCreateServiceRequest = SurveyCreateServiceRequest.builder()
                .title(surveyTitle1)
                .startTime(now.minusDays(1))
                .endTime(now.plusDays(1))
                .headCount(0)
                .surveyTarget(List.of(AccountCodeType.MAN, AccountCodeType.WOMAN, AccountCodeType.TWENTIES))
                .closedHeadCount(100)
                .type(SurveyCategoryType.NORMAL)
                .questions(questionCreateServiceRequests)
                .giveaways(giveawayAssignServiceRequests)
                .build();




        SurveyResponse saveSurvey = surveyService.addSurvey(surveyCreateServiceRequest);
        surveyRepository.findById(saveSurvey.getId()).get().toOpen();



        return Stream.of(
                dynamicTest("테스트",()-> {
                    QuestionAnswerCreateServiceRequest questionAnswerCreateServiceRequest
                            = QuestionAnswerCreateServiceRequest
                            .builder()
                            .questionId(saveSurvey.getQuestions().get(0).getId())
                            .multipleChoiceAnswer(null)
                            .shorFormAnswer("치즈케익")
                            .build();
                    questionAnswerService.addQuestionAnswer(LocalDateTime.now(), saveSurvey.getId(), List.of(questionAnswerCreateServiceRequest));
                    AnswerPossibleType answer = surveyService.getAnswerPossible(saveSurvey.getId());
                    assertThat(answer).isEqualTo(
                            AnswerPossibleType.DIDANSWER);
                })
        );
    }

    @DisplayName("설문과 회원정보의 나이,성별타입이 맞아야 한다.")
    @WithMockUser(username = "writer@naver.com")
    @Test
    void checkPossibleAnswerMismatchType() throws InterruptedException {
        // given
        String writerEmail = "writer@naver.com";
        AccountCreateServiceRequest accountCreateServiceRequest1 = getAccountCreateServiceRequest(
                writerEmail, "1234", AccountCodeType.MAN, AccountCodeType.TWENTIES, "최연재", "010-1234-5678",
                LocalDate.of(1997, Month.JUNE, 24));
        accountService.signup(accountCreateServiceRequest1);

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
                .count(10)
                .build();
        List<GiveawayAssignServiceRequest> giveawayAssignServiceRequests = List.of(giveawayAssignServiceRequest);

        LocalDateTime now = LocalDateTime.now();
        String surveyTitle1 = "당신은 어떤 과일을 좋아하나요?";
        SurveyCreateServiceRequest surveyCreateServiceRequest1 = SurveyCreateServiceRequest.builder()
                .title(surveyTitle1)
                .startTime(now.minusDays(2))
                .endTime(now.plusNanos(1000000L * 1000))
                .headCount(0)
                .surveyTarget(List.of(AccountCodeType.MAN, AccountCodeType.TWENTIES))
                .closedHeadCount(100)
                .type(SurveyCategoryType.NORMAL)
                .questions(questionCreateServiceRequests)
                .giveaways(giveawayAssignServiceRequests)
                .build();

        SurveyResponse saveSurvey = surveyService.addSurvey(surveyCreateServiceRequest1);
        Thread.sleep(3000);
        surveyRepository.findById(saveSurvey.getId()).get().toOpen();

        String email = "duswo0624@naver.com";
        String password = "1234";
        AccountCreateServiceRequest accountCreateServiceRequest2 = getAccountCreateServiceRequest(
                email, password, AccountCodeType.WOMAN, AccountCodeType.TWENTIES, "최연재", "010-9999-8888",
                LocalDate.of(1997, Month.JUNE, 24));
        accountService.signup(accountCreateServiceRequest2);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, password);
        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertThat(surveyService.getAnswerPossible(saveSurvey.getId())).isEqualTo(
                AnswerPossibleType.TYPENOTMATCH);
    }

    @DisplayName("설문 응답 인원이 마감 인원이상이면 응답이 불가능하다.")
    @WithMockUser(username = "writer@naver.com")
    @TestFactory
    Stream<DynamicTest> checkPossibleAnswerOverCount() throws InterruptedException {
        // given
        String writerEmail = "writer@naver.com";
        AccountCreateServiceRequest accountCreateServiceRequest1 = getAccountCreateServiceRequest(
                writerEmail, "1234", AccountCodeType.MAN, AccountCodeType.TWENTIES, "최연구", "010-1234-5678",
                LocalDate.of(1997, Month.JUNE, 24));
        accountService.signup(accountCreateServiceRequest1);

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
        String surveyTitle1 = "당신은 어떤 과일을 좋아하나요?";
        SurveyCreateServiceRequest surveyCreateServiceRequest = SurveyCreateServiceRequest.builder()
                .title(surveyTitle1)
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
        surveyRepository.findById(saveSurvey.getId()).get().toOpen();

        QuestionAnswerCreateServiceRequest questionAnswerCreateServiceRequest
                = QuestionAnswerCreateServiceRequest
                .builder()
                .questionId(saveSurvey.getQuestions().get(0).getId())
                .multipleChoiceAnswer(null)
                .shorFormAnswer("치즈케익")
                .build();
        questionAnswerService.addQuestionAnswer(LocalDateTime.now(), saveSurvey.getId(), List.of(questionAnswerCreateServiceRequest));

        String email = "duswo0624@naver.com";
        String password = "1234";
        AccountCreateServiceRequest accountCreateServiceRequest2 = getAccountCreateServiceRequest(
                email, password, AccountCodeType.WOMAN, AccountCodeType.TWENTIES, "최연재", "010-9999-8888",
                LocalDate.of(1997, Month.JUNE, 24));
        accountService.signup(accountCreateServiceRequest2);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, password);
        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return Stream.of(
                dynamicTest("테스트",()-> {
                    AnswerPossibleType answer = surveyService.getAnswerPossible(saveSurvey.getId());
                    assertThat(answer).isEqualTo(
                            AnswerPossibleType.HEADFULL);
                })
        );
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