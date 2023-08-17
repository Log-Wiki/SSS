package com.logwiki.specialsurveyservice.api.service.question;

import com.logwiki.specialsurveyservice.IntegrationTestSupport;
import com.logwiki.specialsurveyservice.api.controller.giveaway.request.GiveawayRequest;
import com.logwiki.specialsurveyservice.api.service.account.AccountService;
import com.logwiki.specialsurveyservice.api.service.account.request.AccountCreateServiceRequest;
import com.logwiki.specialsurveyservice.api.service.giveaway.GiveawayService;
import com.logwiki.specialsurveyservice.api.service.question.request.MultipleChoiceCreateServiceRequest;
import com.logwiki.specialsurveyservice.api.service.question.request.QuestionAnswerCreateServiceRequest;
import com.logwiki.specialsurveyservice.api.service.question.request.QuestionCreateServiceRequest;
import com.logwiki.specialsurveyservice.api.service.question.response.QuestionAnswerResponse;
import com.logwiki.specialsurveyservice.api.service.sse.SseService;
import com.logwiki.specialsurveyservice.api.service.survey.SurveyService;
import com.logwiki.specialsurveyservice.api.service.survey.request.GiveawayAssignServiceRequest;
import com.logwiki.specialsurveyservice.api.service.survey.request.SurveyCreateServiceRequest;
import com.logwiki.specialsurveyservice.api.service.survey.response.SurveyResponse;
import com.logwiki.specialsurveyservice.api.service.surveyresult.SurveyResultService;
import com.logwiki.specialsurveyservice.domain.accountcode.AccountCode;
import com.logwiki.specialsurveyservice.domain.accountcode.AccountCodeRepository;
import com.logwiki.specialsurveyservice.domain.accountcode.AccountCodeType;
import com.logwiki.specialsurveyservice.domain.authority.Authority;
import com.logwiki.specialsurveyservice.domain.authority.AuthorityRepository;
import com.logwiki.specialsurveyservice.domain.authority.AuthorityType;
import com.logwiki.specialsurveyservice.domain.giveaway.Giveaway;
import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayRepository;
import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayType;
import com.logwiki.specialsurveyservice.domain.question.Question;
import com.logwiki.specialsurveyservice.domain.question.QuestionRepository;
import com.logwiki.specialsurveyservice.domain.questionanswer.QuestionAnswerRepository;
import com.logwiki.specialsurveyservice.domain.questioncategory.QuestionCategoryType;
import com.logwiki.specialsurveyservice.domain.survey.Survey;
import com.logwiki.specialsurveyservice.domain.survey.SurveyRepository;
import com.logwiki.specialsurveyservice.domain.surveycategory.SurveyCategory;
import com.logwiki.specialsurveyservice.domain.surveycategory.SurveyCategoryRepository;
import com.logwiki.specialsurveyservice.domain.surveycategory.SurveyCategoryType;
import com.logwiki.specialsurveyservice.domain.surveytarget.SurveyTargetRepository;
import com.logwiki.specialsurveyservice.exception.BaseException;
import java.time.Month;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class QuestionAnswerServiceTest extends IntegrationTestSupport {

    @Autowired
    QuestionAnswerService questionAnswerService;
    @Autowired
    QuestionAnswerRepository questionAnswerRepository;
    @Autowired
    QuestionRepository questionRepository;
    @Autowired
    AccountService accountService;
    @Autowired
    SurveyResultService surveyResultService;
    @Autowired
    SurveyTargetRepository surveyTargetRepository;
    @Autowired
    SseService sseService;
    @Autowired
    SurveyRepository surveyRepository;
    @Autowired
    AuthorityRepository authorityRepository;
    @Autowired
    AccountCodeRepository accountCodeRepository;
    @Autowired
    GiveawayService giveawayService;
    @Autowired
    SurveyService surveyService;
    @Autowired
    GiveawayRepository giveawayRepository;
    @Autowired
    SurveyCategoryRepository surveyCategoryRepository;


    @BeforeEach
    void setUp() {
        setAuthority();
        setAccountCode();
        setSurveyCategory();
    }

    @DisplayName("존재하지 않는 설문에 설문 응답을 할 수 없다.")
    @Test
    void notFoundQuestionThrowError() {
        // given
        QuestionAnswerCreateServiceRequest questionAnswerCreateServiceRequest = QuestionAnswerCreateServiceRequest.builder()
                .questionId(1L)
                .multipleChoiceAnswer(1L)
                .build();
        List<QuestionAnswerCreateServiceRequest> QuestionAnswerCreateServiceRequests = List.of(
                questionAnswerCreateServiceRequest);
        LocalDateTime answerTime = LocalDateTime.now();
        Long invalidSurveyId = 1L;

        // when // then
        assertThatThrownBy(
                () -> questionAnswerService.addQuestionAnswer(answerTime, invalidSurveyId,
                        QuestionAnswerCreateServiceRequests))
                .isInstanceOf(BaseException.class)
                .hasMessage("없는 설문입니다.");
    }

    @DisplayName("설문 대상자가 아닐 경우 설문 응답을 할 수 없다.")
    @WithMockUser(username = "duswo0624@naver.com")
    @Test
    void addQuestionByInvalidSurveyTarget() {
        // given
        setAccount();

        QuestionCreateServiceRequest questionCreateServiceRequestByShortForm = QuestionCreateServiceRequest.builder()
                .questionNumber(1L)
                .content("좋아하는 과일을 작성해주세요.")
                .imgAddress(null)
                .type(QuestionCategoryType.SHORT_FORM)
                .build();
        List<QuestionCreateServiceRequest> questionCreateServiceRequests = List.of(
                questionCreateServiceRequestByShortForm);

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
        List<GiveawayAssignServiceRequest> giveawayAssignServiceRequests1 = List.of(
                giveawayAssignServiceRequest1);

        LocalDateTime now = LocalDateTime.now();
        SurveyCreateServiceRequest surveyCreateServiceRequest = SurveyCreateServiceRequest.builder()
                .title("당신은 어떤 과일을 좋아하나요?")
                .startTime(now.minusDays(1))
                .endTime(now.plusDays(1))
                .headCount(0)
                .surveyTarget(List.of(AccountCodeType.WOMAN, AccountCodeType.SIXTIES))
                .closedHeadCount(100)
                .type(SurveyCategoryType.INSTANT_WIN)
                .questions(questionCreateServiceRequests)
                .giveaways(giveawayAssignServiceRequests1)
                .build();

        SurveyResponse saveSurvey = surveyService.addSurvey(surveyCreateServiceRequest);
        Long surveyId = saveSurvey.getId();
        surveyRepository.findById(saveSurvey.getId()).get().toOpen();

        QuestionAnswerCreateServiceRequest questionAnswerCreateServiceRequest = QuestionAnswerCreateServiceRequest.builder()
                .questionId(1L)
                .multipleChoiceAnswer(1L)
                .build();
        List<QuestionAnswerCreateServiceRequest> QuestionAnswerCreateServiceRequests = List.of(
                questionAnswerCreateServiceRequest);

        assertThatThrownBy(
                () -> questionAnswerService.addQuestionAnswer(LocalDateTime.now(), surveyId,
                        QuestionAnswerCreateServiceRequests))
                .isInstanceOf(BaseException.class)
                .hasMessage("설문 대상자가 아닙니다.");
    }

    @DisplayName("연계질문을 고려하여 답변을 완료한다.")
    @WithMockUser(username = "duswo0624@naver.com")
    @Test
    void saveQuestionAnswer() {
        // given
        setAccount();
        MultipleChoiceCreateServiceRequest multipleChoiceCreateServiceRequest1 = MultipleChoiceCreateServiceRequest
                .builder()
                .content("사과")
                .linkNumber(2L)
                .build();
        MultipleChoiceCreateServiceRequest multipleChoiceCreateServiceRequest2 = MultipleChoiceCreateServiceRequest
                .builder()
                .content("바나나")
                .linkNumber(3L)
                .build();

        QuestionCreateServiceRequest questionCreateServiceRequestByShortForm1 = QuestionCreateServiceRequest.builder()
                .questionNumber(1L)
                .title("좋아하는 과일을 작성해주세요.")
                .content("필수로 답변해야 하는 문항입니다.")
                .imgAddress(null)
                .type(QuestionCategoryType.MULTIPLE_CHOICE)
                .essential(true)
                .multipleChoices(List.of(multipleChoiceCreateServiceRequest1,
                        multipleChoiceCreateServiceRequest2))
                .build();
        QuestionCreateServiceRequest questionCreateServiceRequestByShortForm2 = QuestionCreateServiceRequest.builder()
                .questionNumber(2L)
                .title("사과를 좋아하는 이유를 적어주세요.")
                .content("")
                .imgAddress(null)
                .type(QuestionCategoryType.SHORT_FORM)
                .essential(true)
                .multipleChoices(new ArrayList<>())
                .build();
        QuestionCreateServiceRequest questionCreateServiceRequestByShortForm3 = QuestionCreateServiceRequest.builder()
                .questionNumber(3L)
                .title("배를 좋아하는 이유를 적어주세요.")
                .content("")
                .imgAddress(null)
                .type(QuestionCategoryType.SHORT_FORM)
                .essential(true)
                .multipleChoices(new ArrayList<>())
                .build();
        QuestionCreateServiceRequest questionCreateServiceRequestByShortForm4 = QuestionCreateServiceRequest.builder()
                .questionNumber(4L)
                .title("좋아하는 음료를 작성해주세요.")
                .content("필수로 답변해야 하는 문항이 아닙니다.")
                .imgAddress(null)
                .type(QuestionCategoryType.SHORT_FORM)
                .essential(false)
                .multipleChoices(new ArrayList<>())
                .build();
        List<QuestionCreateServiceRequest> questionCreateServiceRequests = List.of(
                questionCreateServiceRequestByShortForm1, questionCreateServiceRequestByShortForm2,
                questionCreateServiceRequestByShortForm3, questionCreateServiceRequestByShortForm4);

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
        List<GiveawayAssignServiceRequest> giveawayAssignServiceRequests1 = List.of(
                giveawayAssignServiceRequest1);

        LocalDateTime now = LocalDateTime.now();
        SurveyCreateServiceRequest surveyCreateServiceRequest = SurveyCreateServiceRequest.builder()
                .title("당신은 어떤 과일을 좋아하나요?")
                .startTime(now.minusDays(1))
                .endTime(now.plusDays(1))
                .headCount(0)
                .surveyTarget(List.of(AccountCodeType.MAN, AccountCodeType.TWENTIES))
                .closedHeadCount(100)
                .type(SurveyCategoryType.INSTANT_WIN)
                .questions(questionCreateServiceRequests)
                .giveaways(giveawayAssignServiceRequests1)
                .build();

        SurveyResponse saveSurvey = surveyService.addSurvey(surveyCreateServiceRequest);
        Long surveyId = saveSurvey.getId();
        Survey survey = surveyRepository.findById(surveyId).get();
        survey.toOpen();
        Long question1Id = 0L;
        Long question2Id = 0L;
        Long question3Id = 0L;
        Long question4Id = 0L;
        Long multipleChoiceId = 0L;

        for (Question question : survey.getQuestions()) {
            if (question.getQuestionNumber() == 1) {
                question1Id = question.getId();
                if (question.getMultipleChoice().get(0).getContent().equals("사과")) {
                    multipleChoiceId = question.getMultipleChoice().get(0).getId();
                } else {
                    multipleChoiceId = question.getMultipleChoice().get(1).getId();
                }
            } else if (question.getQuestionNumber() == 2) {
                question2Id = question.getId();
            } else if (question.getQuestionNumber() == 3) {
                question3Id = question.getId();
            } else if (question.getQuestionNumber() == 4) {
                question4Id = question.getId();
            }
        }

        QuestionAnswerCreateServiceRequest questionAnswerCreateServiceRequest1 =
                QuestionAnswerCreateServiceRequest
                        .builder()
                        .questionId(question1Id)
                        .multipleChoiceAnswer(multipleChoiceId)
                        .build();

        QuestionAnswerCreateServiceRequest questionAnswerCreateServiceRequest2 =
                QuestionAnswerCreateServiceRequest
                        .builder()
                        .questionId(question2Id)
                        .shorFormAnswer("사과가 아삭하기 때문입니다.")
                        .build();

        QuestionAnswerCreateServiceRequest questionAnswerCreateServiceRequest3 =
                QuestionAnswerCreateServiceRequest
                        .builder()
                        .questionId(question4Id)
                        .shorFormAnswer("포카리 스웨트")
                        .build();

        List<QuestionAnswerCreateServiceRequest> questionAnswerCreateServiceRequests = List.of(
                questionAnswerCreateServiceRequest1, questionAnswerCreateServiceRequest2,
                questionAnswerCreateServiceRequest3);

        // when
        List<QuestionAnswerResponse> questionAnswers = questionAnswerService.addQuestionAnswer(
                LocalDateTime.now(), surveyId,
                questionAnswerCreateServiceRequests);

        // then
        assertThat(questionAnswers.size()).isEqualTo(3);
    }

    @DisplayName("체크박스인 질문에 대해서는 체크된 모든 연계질문에 답변해야 한다.")
    @WithMockUser(username = "duswo0624@naver.com")
    @Test
    void saveQuestionAnswerForCheckBoxQuestion() {
        // given
        setAccount();
        MultipleChoiceCreateServiceRequest multipleChoiceCreateServiceRequest1 = MultipleChoiceCreateServiceRequest
                .builder()
                .content("사과")
                .linkNumber(2L)
                .build();
        MultipleChoiceCreateServiceRequest multipleChoiceCreateServiceRequest2 = MultipleChoiceCreateServiceRequest
                .builder()
                .content("바나나")
                .linkNumber(3L)
                .build();

        QuestionCreateServiceRequest questionCreateServiceRequestByShortForm1 = QuestionCreateServiceRequest.builder()
                .questionNumber(1L)
                .title("좋아하는 과일을 작성해주세요.")
                .content("필수로 답변해야 하는 문항입니다.")
                .imgAddress(null)
                .type(QuestionCategoryType.CHECK_BOX)
                .essential(true)
                .multipleChoices(List.of(multipleChoiceCreateServiceRequest1,
                        multipleChoiceCreateServiceRequest2))
                .build();
        QuestionCreateServiceRequest questionCreateServiceRequestByShortForm2 = QuestionCreateServiceRequest.builder()
                .questionNumber(2L)
                .title("사과를 좋아하는 이유를 적어주세요.")
                .content("")
                .imgAddress(null)
                .type(QuestionCategoryType.SHORT_FORM)
                .essential(true)
                .multipleChoices(new ArrayList<>())
                .build();
        QuestionCreateServiceRequest questionCreateServiceRequestByShortForm3 = QuestionCreateServiceRequest.builder()
                .questionNumber(3L)
                .title("배를 좋아하는 이유를 적어주세요.")
                .content("")
                .imgAddress(null)
                .type(QuestionCategoryType.SHORT_FORM)
                .essential(true)
                .multipleChoices(new ArrayList<>())
                .build();
        QuestionCreateServiceRequest questionCreateServiceRequestByShortForm4 = QuestionCreateServiceRequest.builder()
                .questionNumber(4L)
                .title("좋아하는 음료를 작성해주세요.")
                .content("필수로 답변해야 하는 문항이 아닙니다.")
                .imgAddress(null)
                .type(QuestionCategoryType.SHORT_FORM)
                .essential(false)
                .multipleChoices(new ArrayList<>())
                .build();
        List<QuestionCreateServiceRequest> questionCreateServiceRequests = List.of(
                questionCreateServiceRequestByShortForm1, questionCreateServiceRequestByShortForm2,
                questionCreateServiceRequestByShortForm3, questionCreateServiceRequestByShortForm4);

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
        List<GiveawayAssignServiceRequest> giveawayAssignServiceRequests1 = List.of(
                giveawayAssignServiceRequest1);

        LocalDateTime now = LocalDateTime.now();
        SurveyCreateServiceRequest surveyCreateServiceRequest = SurveyCreateServiceRequest.builder()
                .title("당신은 어떤 과일을 좋아하나요?")
                .startTime(now.minusDays(1))
                .endTime(now.plusDays(1))
                .headCount(0)
                .surveyTarget(List.of(AccountCodeType.MAN, AccountCodeType.TWENTIES))
                .closedHeadCount(100)
                .type(SurveyCategoryType.INSTANT_WIN)
                .questions(questionCreateServiceRequests)
                .giveaways(giveawayAssignServiceRequests1)
                .build();

        SurveyResponse saveSurvey = surveyService.addSurvey(surveyCreateServiceRequest);
        Long surveyId = saveSurvey.getId();
        Survey survey = surveyRepository.findById(surveyId).get();
        survey.toOpen();
        Long question1Id = 0L;
        Long question2Id = 0L;
        Long question3Id = 0L;
        Long question4Id = 0L;
        Long multipleChoiceId1 = 0L;
        Long multipleChoiceId2 = 0L;

        for (Question question : survey.getQuestions()) {
            if (question.getQuestionNumber() == 1) {
                question1Id = question.getId();
                if (question.getMultipleChoice().get(0).getContent().equals("사과")) {
                    multipleChoiceId1 = question.getMultipleChoice().get(0).getId();
                    multipleChoiceId2 = question.getMultipleChoice().get(1).getId();
                } else {
                    multipleChoiceId1 = question.getMultipleChoice().get(1).getId();
                    multipleChoiceId2 = question.getMultipleChoice().get(0).getId();
                }
            } else if (question.getQuestionNumber() == 2) {
                question2Id = question.getId();
            } else if (question.getQuestionNumber() == 3) {
                question3Id = question.getId();
            } else if (question.getQuestionNumber() == 4) {
                question4Id = question.getId();
            }
        }

        QuestionAnswerCreateServiceRequest questionAnswerCreateServiceRequest1 =
                QuestionAnswerCreateServiceRequest
                        .builder()
                        .questionId(question1Id)
                        .multipleChoiceAnswer(multipleChoiceId1)
                        .build();
        QuestionAnswerCreateServiceRequest questionAnswerCreateServiceRequest2 =
                QuestionAnswerCreateServiceRequest
                        .builder()
                        .questionId(question1Id)
                        .multipleChoiceAnswer(multipleChoiceId2)
                        .build();

        QuestionAnswerCreateServiceRequest questionAnswerCreateServiceRequest3 =
                QuestionAnswerCreateServiceRequest
                        .builder()
                        .questionId(question2Id)
                        .shorFormAnswer("사과가 아삭하기 때문입니다.")
                        .build();

        QuestionAnswerCreateServiceRequest questionAnswerCreateServiceRequest4 =
                QuestionAnswerCreateServiceRequest
                        .builder()
                        .questionId(question3Id)
                        .shorFormAnswer("바나나가 달콤하기 때문입니다.")
                        .build();

        QuestionAnswerCreateServiceRequest questionAnswerCreateServiceRequest5 =
                QuestionAnswerCreateServiceRequest
                        .builder()
                        .questionId(question4Id)
                        .shorFormAnswer("포카리 스웨트")
                        .build();

        List<QuestionAnswerCreateServiceRequest> questionAnswerCreateServiceRequests = List.of(
                questionAnswerCreateServiceRequest1, questionAnswerCreateServiceRequest2,
                questionAnswerCreateServiceRequest3, questionAnswerCreateServiceRequest4,
                questionAnswerCreateServiceRequest5);

        // when
        List<QuestionAnswerResponse> questionAnswers = questionAnswerService.addQuestionAnswer(
                LocalDateTime.now(), surveyId,
                questionAnswerCreateServiceRequests);

        // then
        assertThat(questionAnswers.size()).isEqualTo(5);
    }

    @DisplayName("선택하지 않은 연계질문에 대해서는 답변을 할 수 없습니다.")
    @WithMockUser(username = "duswo0624@naver.com")
    @Test
    void saveQuestionAnswerWithInvalidLinkedQuestion() {
        // given
        setAccount();
        MultipleChoiceCreateServiceRequest multipleChoiceCreateServiceRequest1 = MultipleChoiceCreateServiceRequest
                .builder()
                .content("사과")
                .linkNumber(2L)
                .build();
        MultipleChoiceCreateServiceRequest multipleChoiceCreateServiceRequest2 = MultipleChoiceCreateServiceRequest
                .builder()
                .content("바나나")
                .linkNumber(3L)
                .build();

        QuestionCreateServiceRequest questionCreateServiceRequestByShortForm1 = QuestionCreateServiceRequest.builder()
                .questionNumber(1L)
                .title("좋아하는 과일을 작성해주세요.")
                .content("필수로 답변해야 하는 문항입니다.")
                .imgAddress(null)
                .type(QuestionCategoryType.MULTIPLE_CHOICE)
                .essential(true)
                .multipleChoices(List.of(multipleChoiceCreateServiceRequest1,
                        multipleChoiceCreateServiceRequest2))
                .build();
        QuestionCreateServiceRequest questionCreateServiceRequestByShortForm2 = QuestionCreateServiceRequest.builder()
                .questionNumber(2L)
                .title("사과를 좋아하는 이유를 적어주세요.")
                .content("")
                .imgAddress(null)
                .type(QuestionCategoryType.SHORT_FORM)
                .essential(true)
                .multipleChoices(new ArrayList<>())
                .build();
        QuestionCreateServiceRequest questionCreateServiceRequestByShortForm3 = QuestionCreateServiceRequest.builder()
                .questionNumber(3L)
                .title("배를 좋아하는 이유를 적어주세요.")
                .content("")
                .imgAddress(null)
                .type(QuestionCategoryType.SHORT_FORM)
                .essential(true)
                .multipleChoices(new ArrayList<>())
                .build();
        QuestionCreateServiceRequest questionCreateServiceRequestByShortForm4 = QuestionCreateServiceRequest.builder()
                .questionNumber(4L)
                .title("좋아하는 음료를 작성해주세요.")
                .content("필수로 답변해야 하는 문항이 아닙니다.")
                .imgAddress(null)
                .type(QuestionCategoryType.SHORT_FORM)
                .essential(false)
                .multipleChoices(new ArrayList<>())
                .build();
        List<QuestionCreateServiceRequest> questionCreateServiceRequests = List.of(
                questionCreateServiceRequestByShortForm1, questionCreateServiceRequestByShortForm2,
                questionCreateServiceRequestByShortForm3, questionCreateServiceRequestByShortForm4);

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
        List<GiveawayAssignServiceRequest> giveawayAssignServiceRequests1 = List.of(
                giveawayAssignServiceRequest1);

        LocalDateTime now = LocalDateTime.now();
        SurveyCreateServiceRequest surveyCreateServiceRequest = SurveyCreateServiceRequest.builder()
                .title("당신은 어떤 과일을 좋아하나요?")
                .startTime(now.minusDays(1))
                .endTime(now.plusDays(1))
                .headCount(0)
                .surveyTarget(List.of(AccountCodeType.MAN, AccountCodeType.TWENTIES))
                .closedHeadCount(100)
                .type(SurveyCategoryType.INSTANT_WIN)
                .questions(questionCreateServiceRequests)
                .giveaways(giveawayAssignServiceRequests1)
                .build();

        SurveyResponse saveSurvey = surveyService.addSurvey(surveyCreateServiceRequest);
        Long surveyId = saveSurvey.getId();
        Survey survey = surveyRepository.findById(surveyId).get();
        survey.toOpen();
        Long question1Id = survey.getQuestions().get(0).getId();
        Long question2Id = survey.getQuestions().get(1).getId();
        Long question3Id = survey.getQuestions().get(2).getId();
        Long question4Id = survey.getQuestions().get(3).getId();

        QuestionAnswerCreateServiceRequest questionAnswerCreateServiceRequest1 =
                QuestionAnswerCreateServiceRequest
                        .builder()
                        .questionId(question1Id)
                        .multipleChoiceAnswer(1L)
                        .build();

        QuestionAnswerCreateServiceRequest questionAnswerCreateServiceRequest2 =
                QuestionAnswerCreateServiceRequest
                        .builder()
                        .questionId(question2Id)
                        .shorFormAnswer("사과가 아삭하기 때문입니다.")
                        .build();

        QuestionAnswerCreateServiceRequest questionAnswerCreateServiceRequest3 =
                QuestionAnswerCreateServiceRequest
                        .builder()
                        .questionId(question3Id)
                        .shorFormAnswer("바나나가 달콤하기 때문입니다.")
                        .build();

        QuestionAnswerCreateServiceRequest questionAnswerCreateServiceRequest4 =
                QuestionAnswerCreateServiceRequest
                        .builder()
                        .questionId(question4Id)
                        .shorFormAnswer("포카리 스웨트")
                        .build();

        List<QuestionAnswerCreateServiceRequest> questionAnswerCreateServiceRequests = List.of(
                questionAnswerCreateServiceRequest1, questionAnswerCreateServiceRequest2,
                questionAnswerCreateServiceRequest3, questionAnswerCreateServiceRequest4);

        // when // then
        assertThatThrownBy(
                () -> questionAnswerService.addQuestionAnswer(LocalDateTime.now(), surveyId,
                        questionAnswerCreateServiceRequests))
                .isInstanceOf(BaseException.class)
                .hasMessage("연계질문으로 다른 답변을 선택했으므로 답변을 하면 안되는 답변이 존재합니다.");
    }

    @DisplayName("연계된 질문에 대해서는 모두 답변을 해야한다.")
    @WithMockUser(username = "duswo0624@naver.com")
    @Test
    void saveAllQuestionForLinkedQuestion() {
        // given
        setAccount();
        MultipleChoiceCreateServiceRequest multipleChoiceCreateServiceRequest1 = MultipleChoiceCreateServiceRequest
                .builder()
                .content("사과")
                .linkNumber(2L)
                .build();
        MultipleChoiceCreateServiceRequest multipleChoiceCreateServiceRequest2 = MultipleChoiceCreateServiceRequest
                .builder()
                .content("바나나")
                .linkNumber(3L)
                .build();

        QuestionCreateServiceRequest questionCreateServiceRequestByShortForm1 = QuestionCreateServiceRequest.builder()
                .questionNumber(1L)
                .title("좋아하는 과일을 작성해주세요.")
                .content("필수로 답변해야 하는 문항입니다.")
                .imgAddress(null)
                .type(QuestionCategoryType.MULTIPLE_CHOICE)
                .essential(true)
                .multipleChoices(List.of(multipleChoiceCreateServiceRequest1,
                        multipleChoiceCreateServiceRequest2))
                .build();
        QuestionCreateServiceRequest questionCreateServiceRequestByShortForm2 = QuestionCreateServiceRequest.builder()
                .questionNumber(2L)
                .title("사과를 좋아하는 이유를 적어주세요.")
                .content("")
                .imgAddress(null)
                .type(QuestionCategoryType.SHORT_FORM)
                .essential(true)
                .multipleChoices(new ArrayList<>())
                .build();
        QuestionCreateServiceRequest questionCreateServiceRequestByShortForm3 = QuestionCreateServiceRequest.builder()
                .questionNumber(3L)
                .title("배를 좋아하는 이유를 적어주세요.")
                .content("")
                .imgAddress(null)
                .type(QuestionCategoryType.SHORT_FORM)
                .essential(true)
                .multipleChoices(new ArrayList<>())
                .build();
        QuestionCreateServiceRequest questionCreateServiceRequestByShortForm4 = QuestionCreateServiceRequest.builder()
                .questionNumber(4L)
                .title("좋아하는 음료를 작성해주세요.")
                .content("필수로 답변해야 하는 문항이 아닙니다.")
                .imgAddress(null)
                .type(QuestionCategoryType.SHORT_FORM)
                .essential(false)
                .multipleChoices(new ArrayList<>())
                .build();
        List<QuestionCreateServiceRequest> questionCreateServiceRequests = List.of(
                questionCreateServiceRequestByShortForm1, questionCreateServiceRequestByShortForm2,
                questionCreateServiceRequestByShortForm3, questionCreateServiceRequestByShortForm4);

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
        List<GiveawayAssignServiceRequest> giveawayAssignServiceRequests1 = List.of(
                giveawayAssignServiceRequest1);

        LocalDateTime now = LocalDateTime.now();
        SurveyCreateServiceRequest surveyCreateServiceRequest = SurveyCreateServiceRequest.builder()
                .title("당신은 어떤 과일을 좋아하나요?")
                .startTime(now.minusDays(1))
                .endTime(now.plusDays(1))
                .headCount(0)
                .surveyTarget(List.of(AccountCodeType.MAN, AccountCodeType.TWENTIES))
                .closedHeadCount(100)
                .type(SurveyCategoryType.INSTANT_WIN)
                .questions(questionCreateServiceRequests)
                .giveaways(giveawayAssignServiceRequests1)
                .build();

        SurveyResponse saveSurvey = surveyService.addSurvey(surveyCreateServiceRequest);
        Long surveyId = saveSurvey.getId();
        Survey survey = surveyRepository.findById(surveyId).get();
        survey.toOpen();
        Long question1Id = 0L;
        Long question2Id = 0L;
        Long question3Id = 0L;
        Long question4Id = 0L;
        Long multipleChoiceId = 0L;

        for (Question question : survey.getQuestions()) {
            if (question.getQuestionNumber() == 1) {
                question1Id = question.getId();
                if (question.getMultipleChoice().get(0).getContent().equals("사과")) {
                    multipleChoiceId = question.getMultipleChoice().get(0).getId();
                } else {
                    multipleChoiceId = question.getMultipleChoice().get(1).getId();
                }
            } else if (question.getQuestionNumber() == 2) {
                question2Id = question.getId();
            } else if (question.getQuestionNumber() == 3) {
                question3Id = question.getId();
            } else if (question.getQuestionNumber() == 4) {
                question4Id = question.getId();
            }
        }

        QuestionAnswerCreateServiceRequest questionAnswerCreateServiceRequest1 =
                QuestionAnswerCreateServiceRequest
                        .builder()
                        .questionId(question1Id)
                        .multipleChoiceAnswer(multipleChoiceId)
                        .build();

        QuestionAnswerCreateServiceRequest questionAnswerCreateServiceRequest4 =
                QuestionAnswerCreateServiceRequest
                        .builder()
                        .questionId(question4Id)
                        .shorFormAnswer("포카리 스웨트")
                        .build();

        List<QuestionAnswerCreateServiceRequest> questionAnswerCreateServiceRequests = List.of(
                questionAnswerCreateServiceRequest1, questionAnswerCreateServiceRequest4);

        // when // then
        assertThatThrownBy(
                () -> questionAnswerService.addQuestionAnswer(LocalDateTime.now(), surveyId,
                        questionAnswerCreateServiceRequests))
                .isInstanceOf(BaseException.class)
                .hasMessage("모든 문항에 답변을 해야합니다.");
    }

    @DisplayName("필수로 답을 해야 하는 문항에 대해서는 모두 답변을 해야한다.")
    @WithMockUser(username = "duswo0624@naver.com")
    @Test
    void saveQuestionAnswerWithInvalidQuestionNumber() {
        // given
        setAccount();

        QuestionCreateServiceRequest questionCreateServiceRequestByShortForm1 = QuestionCreateServiceRequest.builder()
                .questionNumber(1L)
                .title("좋아하는 과일을 작성해주세요.")
                .content("필수로 답변해야 하는 문항입니다.")
                .imgAddress(null)
                .type(QuestionCategoryType.MULTIPLE_CHOICE)
                .essential(true)
                .multipleChoices(new ArrayList<>())
                .build();
        QuestionCreateServiceRequest questionCreateServiceRequestByShortForm2 = QuestionCreateServiceRequest.builder()
                .questionNumber(4L)
                .title("좋아하는 음료를 작성해주세요.")
                .content("필수로 답변해야 하는 문항입니다.")
                .imgAddress(null)
                .type(QuestionCategoryType.SHORT_FORM)
                .essential(true)
                .multipleChoices(new ArrayList<>())
                .build();
        List<QuestionCreateServiceRequest> questionCreateServiceRequests = List.of(
                questionCreateServiceRequestByShortForm1, questionCreateServiceRequestByShortForm2);

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
        List<GiveawayAssignServiceRequest> giveawayAssignServiceRequests1 = List.of(
                giveawayAssignServiceRequest1);

        LocalDateTime now = LocalDateTime.now();
        SurveyCreateServiceRequest surveyCreateServiceRequest = SurveyCreateServiceRequest.builder()
                .title("당신은 어떤 과일을 좋아하나요?")
                .startTime(now.minusDays(1))
                .endTime(now.plusDays(1))
                .headCount(0)
                .surveyTarget(List.of(AccountCodeType.MAN, AccountCodeType.TWENTIES))
                .closedHeadCount(100)
                .type(SurveyCategoryType.INSTANT_WIN)
                .questions(questionCreateServiceRequests)
                .giveaways(giveawayAssignServiceRequests1)
                .build();

        SurveyResponse saveSurvey = surveyService.addSurvey(surveyCreateServiceRequest);
        Long surveyId = saveSurvey.getId();
        Survey survey = surveyRepository.findById(surveyId).get();
        survey.toOpen();
        Long question1Id = 0L;
        Long question2Id = 0L;

        for (Question question : survey.getQuestions()) {
            if (question.getQuestionNumber() == 1) {
                question1Id = question.getId();
            } else if (question.getQuestionNumber() == 2) {
                question2Id = question.getId();
            }
        }

        QuestionAnswerCreateServiceRequest questionAnswerCreateServiceRequest1 =
                QuestionAnswerCreateServiceRequest
                        .builder()
                        .questionId(question1Id)
                        .shorFormAnswer("사과")
                        .build();

        List<QuestionAnswerCreateServiceRequest> questionAnswerCreateServiceRequests = List.of(
                questionAnswerCreateServiceRequest1);

        // when // then
        assertThatThrownBy(
                () -> questionAnswerService.addQuestionAnswer(LocalDateTime.now(), surveyId,
                        questionAnswerCreateServiceRequests))
                .isInstanceOf(BaseException.class)
                .hasMessage("모든 문항에 답변을 해야합니다.");
    }

    @DisplayName("없는 문항에 대해서 답변을 할 수 없다.")
    @WithMockUser(username = "duswo0624@naver.com")
    @Test
    void cannotSaveAnswerForNotExistQuestion() {
        // given
        setAccount();

        QuestionCreateServiceRequest questionCreateServiceRequestByShortForm1 = QuestionCreateServiceRequest.builder()
                .questionNumber(1L)
                .title("좋아하는 과일을 작성해주세요.")
                .content("필수로 답변해야 하는 문항입니다.")
                .imgAddress(null)
                .type(QuestionCategoryType.MULTIPLE_CHOICE)
                .essential(true)
                .multipleChoices(new ArrayList<>())
                .build();

        List<QuestionCreateServiceRequest> questionCreateServiceRequests = List.of(
                questionCreateServiceRequestByShortForm1);

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
        List<GiveawayAssignServiceRequest> giveawayAssignServiceRequests1 = List.of(
                giveawayAssignServiceRequest1);

        LocalDateTime now = LocalDateTime.now();
        SurveyCreateServiceRequest surveyCreateServiceRequest = SurveyCreateServiceRequest.builder()
                .title("당신은 어떤 과일을 좋아하나요?")
                .startTime(now.minusDays(1))
                .endTime(now.plusDays(1))
                .headCount(0)
                .surveyTarget(List.of(AccountCodeType.MAN, AccountCodeType.TWENTIES))
                .closedHeadCount(100)
                .type(SurveyCategoryType.INSTANT_WIN)
                .questions(questionCreateServiceRequests)
                .giveaways(giveawayAssignServiceRequests1)
                .build();

        SurveyResponse saveSurvey = surveyService.addSurvey(surveyCreateServiceRequest);
        Long surveyId = saveSurvey.getId();
        Survey survey = surveyRepository.findById(surveyId).get();
        survey.toOpen();
        Long question1Id = survey.getQuestions().get(0).getId();
        Long invalidQuestionId = 100L;

        QuestionAnswerCreateServiceRequest questionAnswerCreateServiceRequest1 =
                QuestionAnswerCreateServiceRequest
                        .builder()
                        .questionId(question1Id)
                        .multipleChoiceAnswer(1L)
                        .build();

        QuestionAnswerCreateServiceRequest questionAnswerCreateServiceRequest2 =
                QuestionAnswerCreateServiceRequest
                        .builder()
                        .questionId(invalidQuestionId)
                        .shorFormAnswer("없는 질문에 대한 답변입니다.")
                        .build();

        List<QuestionAnswerCreateServiceRequest> questionAnswerCreateServiceRequests = List.of(
                questionAnswerCreateServiceRequest1, questionAnswerCreateServiceRequest2);

        // when // then
        assertThatThrownBy(
                () -> questionAnswerService.addQuestionAnswer(LocalDateTime.now(), surveyId,
                        questionAnswerCreateServiceRequests))
                .isInstanceOf(BaseException.class)
                .hasMessage("없는 문항에 답변을 할 수 없습니다.");
    }

    private void setAccount() {
        AccountCreateServiceRequest accountCreateServiceRequest = AccountCreateServiceRequest.builder()
                .email("duswo0624@naver.com")
                .password("1234")
                .gender(AccountCodeType.MAN)
                .age(AccountCodeType.TWENTIES)
                .name("최연재")
                .phoneNumber("010-1234-5678")
                .birthday(LocalDate.of(1997, Month.JUNE, 24))
                .build();
        accountService.signup(accountCreateServiceRequest);
    }


    private void setAuthority() {
        Authority userAuthority = Authority.builder()
                .type(AuthorityType.ROLE_USER)
                .build();

        authorityRepository.save(userAuthority);
    }

    private void setAccountCode() {
        List<AccountCodeType> accountCodeTypes = List.of(AccountCodeType.MAN, AccountCodeType.WOMAN,
                AccountCodeType.UNDER_TEENS,
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