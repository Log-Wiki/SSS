package com.logwiki.specialsurveyservice.api.service.survey;

import com.logwiki.specialsurveyservice.IntegrationTestSupport;
import com.logwiki.specialsurveyservice.api.controller.giveaway.request.GiveawayRequest;
import com.logwiki.specialsurveyservice.api.service.account.AccountService;
import com.logwiki.specialsurveyservice.api.service.account.request.AccountCreateServiceRequest;
import com.logwiki.specialsurveyservice.api.service.giveaway.GiveawayService;
import com.logwiki.specialsurveyservice.api.service.question.request.MultipleChoiceCreateServiceRequest;
import com.logwiki.specialsurveyservice.api.service.question.request.QuestionCreateServiceRequest;
import com.logwiki.specialsurveyservice.api.service.survey.request.GiveawayAssignServiceRequest;
import com.logwiki.specialsurveyservice.api.service.survey.request.SurveyCreateServiceRequest;
import com.logwiki.specialsurveyservice.api.service.survey.response.SurveyResponse;
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
import com.logwiki.specialsurveyservice.domain.surveycategory.SurveyCategoryType;
import com.logwiki.specialsurveyservice.exception.BaseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    @BeforeEach
    void setUp() {
        setAuthority();
        setAccountCode();
    }

    @DisplayName("설문 이름, 시작 시간, 마감 시간, 설문 인원, 설문 마감 인원, 설문 타입, 질문 목록, 당첨 상품 목록, 설문 대상자를 이용하여 설문을 등록한다.")
    @Test
    void addSurvey() {
        // given
        String email = "duswo0624@naver.com";
        String password = "1234";
        AccountCodeType gender = AccountCodeType.MAN;
        AccountCodeType age = AccountCodeType.TWENTIES;
        String name = "최연재";
        String phoneNumber = "010-1234-5678";
        LocalDate birthday = LocalDate.of(1997, Month.JUNE, 24);
        AccountCreateServiceRequest accountCreateServiceRequest = AccountCreateServiceRequest.builder()
                .email(email)
                .password(password)
                .gender(gender)
                .age(age)
                .name(name)
                .phoneNumber(phoneNumber)
                .birthday(birthday)
                .build();
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
                .giveawayType(giveawayType)
                .name("스타벅스 아메리카노")
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
        SurveyResponse saveSurvey = surveyService.addSurvey(email, surveyCreateServiceRequest);

        // then
        assertThat(saveSurvey).isNotNull();
        assertThat(saveSurvey).extracting("title", "startTime", "endTime")
                .contains(title, startTime, endTime);
    }

    @DisplayName("설문을 작성하기 위해서는 email로 회원(작성자)를 조회할 수 있어야 한다.")
    @Test
    void addSurveyWithInvalidUser() {
        // given
        String email = "InvalidEmail@amenable.com";
        SurveyCreateServiceRequest surveyCreateServiceRequest = SurveyCreateServiceRequest.builder()
                .title("어떤 외국어를 배우고 싶습니까?")
                .startTime(LocalDateTime.now().minusDays(1))
                .endTime(LocalDateTime.now().plusDays(1))
                .headCount(0)
                .surveyTarget(List.of(AccountCodeType.MAN, AccountCodeType.WOMAN))
                .closedHeadCount(100)
                .build();

        // when // then
        assertThatThrownBy(() -> surveyService.addSurvey(email, surveyCreateServiceRequest))
                .isInstanceOf(BaseException.class)
                .hasMessage("존재하지 않는 유저입니다.");
    }

    @DisplayName("존재하지 않는 나이성별 코드로는 설문을 작성할 수 없다.")
    @Test
    void abc() {
        // given
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
                .giveawayType(GiveawayType.COFFEE)
                .name("스타벅스 아메리카노")
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
        assertThatThrownBy(() -> surveyService.addSurvey(email, surveyCreateServiceRequest))
                .isInstanceOf(BaseException.class)
                .hasMessage("없는 나이,성별 코드 입니다.");
    }

    @DisplayName("설문을 제작할 때는 등록된 당첨 상품을 사용해야 한다.")
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
        AccountCreateServiceRequest accountCreateServiceRequest = AccountCreateServiceRequest.builder()
                .email(email)
                .password(password)
                .gender(gender)
                .age(age)
                .name(name)
                .phoneNumber(phoneNumber)
                .birthday(birthday)
                .build();
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
                .giveawayType(giveawayType)
                .name("스타벅스 아메리카노")
                .count(10)
                .build();
        List<GiveawayAssignServiceRequest> giveawayAssignServiceRequests = List.of(giveawayAssignServiceRequest);

        String title = "당신은 어떤 과일을 좋아하나요?";
        SurveyCategoryType surveyCategoryType = SurveyCategoryType.INSTANT_WIN;
        int closedHeadCount = 100;
        SurveyCreateServiceRequest surveyCreateServiceRequest = SurveyCreateServiceRequest.builder()
                .title(title)
                .startTime(LocalDateTime.of(2023, 7, 28, 0, 0))
                .endTime(LocalDateTime.of(2023, 7, 30, 0, 0))
                .headCount(50)
                .closedHeadCount(closedHeadCount)
                .type(surveyCategoryType)
                .surveyTarget(new ArrayList<>())
                .questions(questionCreateServiceRequests)
                .giveaways(giveawayAssignServiceRequests)
                .build();

        // when // then
        assertThatThrownBy(() -> surveyService.addSurvey(email, surveyCreateServiceRequest))
                .isInstanceOf(BaseException.class)
                .hasMessage("등록되어 있지 않은 당첨 상품을 포함하고 있습니다.");
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
        for(AccountCodeType accountCodeType : accountCodeTypes) {
            accountCodeRepository.save(AccountCode.builder()
                    .type(accountCodeType)
                    .build());
        }
    }
}