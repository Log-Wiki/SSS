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
import org.mockito.Mock;
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
    @Mock
    private AccountCodeRepository accountCodeRepository;

    @BeforeEach
    void setUp() {
        setAuthority();
    }

    @DisplayName("설문 이름, 시작 시간, 마감 시간, 설문 인원, 설문 마감 인원, 설문 타입, 질문 목록, 당첨 상품 목록을 이용하여 설문을 등록한다.")
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
        List<Long> targets = new ArrayList<>();
        SurveyCreateServiceRequest surveyCreateServiceRequest = SurveyCreateServiceRequest.builder()
                .title(title)
                .startTime(LocalDateTime.of(2023, 7, 28, 0, 0))
                .endTime(LocalDateTime.of(2023, 7, 30, 0, 0))
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
//        assertThat(saveSurvey).extracting("title", "closedHeadCount")
//                .contains(title, closedHeadCount);
//
//        assertThat(saveSurvey.getQuestions().size()).isEqualTo(questionCreateServiceRequests.size());
//        assertThat(saveSurvey.getSurveyGiveaways().size()).isEqualTo(giveawayAssignServiceRequests.size());
//        assertThat(saveSurvey.getTargetNumbers().size()).isEqualTo(giveawayAssignServiceRequests.stream()
//                .mapToInt(GiveawayAssignServiceRequest -> giveawayAssignServiceRequest.getCount())
//                .sum());
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
}