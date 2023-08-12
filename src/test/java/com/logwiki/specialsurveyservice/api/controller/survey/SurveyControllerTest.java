package com.logwiki.specialsurveyservice.api.controller.survey;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.logwiki.specialsurveyservice.ControllerTestSupport;
import com.logwiki.specialsurveyservice.api.controller.question.request.MultipleChoiceCreateRequest;
import com.logwiki.specialsurveyservice.api.controller.question.request.QuestionCreateRequest;
import com.logwiki.specialsurveyservice.api.controller.survey.request.GiveawayAssignRequest;
import com.logwiki.specialsurveyservice.api.controller.survey.request.SurveyCreateRequest;
import com.logwiki.specialsurveyservice.api.service.giveaway.response.GiveawayResponse;
import com.logwiki.specialsurveyservice.api.service.giveaway.response.SurveyGiveawayResponse;
import com.logwiki.specialsurveyservice.api.service.question.response.MultipleChoiceResponse;
import com.logwiki.specialsurveyservice.api.service.question.response.QuestionAnswerStatisticsResponse;
import com.logwiki.specialsurveyservice.api.service.question.response.QuestionResponse;
import com.logwiki.specialsurveyservice.api.service.sse.response.SurveyAnswerResponse;
import com.logwiki.specialsurveyservice.api.service.survey.response.AbstractSurveyResponse;
import com.logwiki.specialsurveyservice.api.service.survey.response.StatisticsSurveyResponse;
import com.logwiki.specialsurveyservice.api.service.survey.response.SurveyResponse;
import com.logwiki.specialsurveyservice.domain.accountcode.AccountCodeType;
import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayType;
import com.logwiki.specialsurveyservice.domain.questioncategory.QuestionCategoryType;
import com.logwiki.specialsurveyservice.domain.survey.AnswerPossibleType;
import com.logwiki.specialsurveyservice.domain.surveycategory.SurveyCategoryType;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

class SurveyControllerTest extends ControllerTestSupport {

    @DisplayName("설문 질문과 당첨 상품을 이용하여 설문을 등록한다.")
    @WithMockUser
    @Test
    void surveyAdd() throws Exception {
        // given
        String multipleChoice1Content = "사과";
        Long multipleChoice1LinkNumber = 0L;
        MultipleChoiceCreateRequest multipleChoiceCreateRequest1 = MultipleChoiceCreateRequest
                .builder()
                .content(multipleChoice1Content)
                .linkNumber(multipleChoice1LinkNumber)
                .build();

        String multipleChoice2Content = "배";
        Long multipleChoice2LinkNumber = 0L;
        MultipleChoiceCreateRequest multipleChoiceCreateRequest2 = MultipleChoiceCreateRequest
                .builder()
                .content(multipleChoice2Content)
                .linkNumber(multipleChoice2LinkNumber)
                .build();

        Long question1Number = 1L;
        String question1Title = "당신은 어떤 과일을 좋아하십니까?";
        QuestionCategoryType question1QuestionCategoryType = QuestionCategoryType.MULTIPLE_CHOICE;
        QuestionCreateRequest questionCreateRequest1 = QuestionCreateRequest
                .builder()
                .questionNumber(question1Number)
                .title(question1Title)
                .content(null)
                .imgAddress(null)
                .type(question1QuestionCategoryType)
                .multipleChoices(List.of(multipleChoiceCreateRequest1, multipleChoiceCreateRequest2))
                .essential(true)
                .build();

        Long question2Number = 2L;
        String question2Title = "당신은 어떤 음료를 좋아하십니까?";
        QuestionCategoryType question2QuestionCategoryType = QuestionCategoryType.SHORT_FORM;
        QuestionCreateRequest questionCreateRequest2 = QuestionCreateRequest
                .builder()
                .questionNumber(question2Number)
                .title(question2Title)
                .content(null)
                .imgAddress(null)
                .type(question2QuestionCategoryType)
                .multipleChoices(null)
                .essential(true)
                .build();

        Long giveawayId = 1L;
        int giveawayCount = 10;
        GiveawayAssignRequest giveawayAssignRequest = GiveawayAssignRequest
                .builder()
                .id(giveawayId)
                .count(giveawayCount)
                .build();

        String surveyTitle = "당신은 어떤 종류의 과일과 음료를 좋아하나요?";
        LocalDateTime surveyStartTime = LocalDateTime.now().plusDays(1);
        LocalDateTime surveyEndTime = LocalDateTime.now().plusDays(3);
        int headCount = 0;
        int closedHeadCount = 100;
        SurveyCategoryType surveyCategoryType = SurveyCategoryType.INSTANT_WIN;
        List<AccountCodeType> surveyTarget = List.of(AccountCodeType.MAN, AccountCodeType.WOMAN, AccountCodeType.TWENTIES);
        List<QuestionCreateRequest> questionCreateRequests = List.of(questionCreateRequest1, questionCreateRequest2);
        List<GiveawayAssignRequest> giveawayAssignRequests = List.of(giveawayAssignRequest);
        SurveyCreateRequest surveyCreateRequest = SurveyCreateRequest
                .builder()
                .title(surveyTitle)
                .img(null)
                .content(null)
                .startTime(surveyStartTime)
                .endTime(surveyEndTime)
                .headCount(headCount)
                .closedHeadCount(closedHeadCount)
                .type(surveyCategoryType)
                .surveyTarget(surveyTarget)
                .questions(questionCreateRequests)
                .giveaways(giveawayAssignRequests)
                .build();

        MultipleChoiceResponse multipleChoiceResponse1 = MultipleChoiceResponse
                .builder()
                .id(1L)
                .content(multipleChoice1Content)
                .linkNumber(multipleChoice1LinkNumber)
                .build();

        MultipleChoiceResponse multipleChoiceResponse2 = MultipleChoiceResponse
                .builder()
                .id(1L)
                .content(multipleChoice2Content)
                .linkNumber(multipleChoice2LinkNumber)
                .build();

        QuestionResponse questionResponse1 = QuestionResponse
                .builder()
                .id(1L)
                .questionNumber(question1Number)
                .title(question1Title)
                .content(null)
                .imgAddress(null)
                .essential(true)
                .type(question1QuestionCategoryType)
                .multipleChoices(List.of(multipleChoiceResponse1, multipleChoiceResponse2))
                .build();

        QuestionResponse questionResponse2 = QuestionResponse
                .builder()
                .id(1L)
                .questionNumber(question2Number)
                .title(question2Title)
                .content(null)
                .imgAddress(null)
                .essential(true)
                .type(question2QuestionCategoryType)
                .multipleChoices(null)
                .build();

        GiveawayType giveawayType = GiveawayType.COFFEE;
        String giveawayName = "스타벅스 아이스 아메리카노";
        int giveawayPrice = 4500;
        GiveawayResponse giveawayResponse = GiveawayResponse
                .builder()
                .id(1L)
                .giveawayType(giveawayType)
                .name(giveawayName)
                .price(giveawayPrice)
                .build();

        SurveyGiveawayResponse surveyGiveawayResponse = SurveyGiveawayResponse
                .builder()
                .id(1L)
                .count(giveawayCount)
                .giveawayResponse(giveawayResponse)
                .build();

        Long surveyResponseId = 1L;
        SurveyResponse surveyResponse = SurveyResponse
                .builder()
                .id(surveyResponseId)
                .title(surveyTitle)
                .content(null)
                .img(null)
                .startTime(surveyStartTime)
                .endTime(surveyEndTime)
                .headCount(headCount)
                .closedHeadCount(closedHeadCount)
                .writer(1L)
                .totalGiveawayCount(giveawayAssignRequests.size())
                .requiredTimeInSeconds(30)
                .closed(true)
                .winningPercent(10D)
                .surveyCategoryType(surveyCategoryType)
                .questions(List.of(questionResponse1, questionResponse2))
                .surveyGiveaways(List.of(surveyGiveawayResponse))
                .surveyTarget(surveyTarget)
                .build();

        when(surveyService.addSurvey(any())).thenReturn(surveyResponse);

        // when // then
        mockMvc.perform(
                        post("/api/survey")
                                .content(objectMapper.writeValueAsString(surveyCreateRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.response.title").value(surveyTitle))
                .andExpect(jsonPath("$.response.headCount").value(headCount))
                .andExpect(jsonPath("$.response.closedHeadCount").value(closedHeadCount));
    }

    @DisplayName("로그인 되지 않은 익명의 사용자에게 익명 사용자를 위한 일반 설문을 추천한다.")
    @WithMockUser
    @Test
    void getRecommendNormalSurveyForAnonymous() throws Exception {
        // given
        GiveawayResponse giveawayResponse = GiveawayResponse
                .builder()
                .id(1L)
                .giveawayType(GiveawayType.COFFEE)
                .price(4500)
                .build();
        SurveyGiveawayResponse surveyGiveawayResponse = SurveyGiveawayResponse
                .builder()
                .count(10)
                .giveawayResponse(giveawayResponse)
                .build();

        String surveyTitle = "다들 안녕하신가요?";
        String surveyContent = "항상 행복하세요.";
        SurveyCategoryType surveyCategoryType = SurveyCategoryType.NORMAL;
        AbstractSurveyResponse abstractSurveyResponse = AbstractSurveyResponse
                .builder()
                .id(1L)
                .title(surveyTitle)
                .content(surveyContent)
                .img(null)
                .surveyCategoryType(surveyCategoryType)
                .surveyTarget(List.of(AccountCodeType.MAN, AccountCodeType.WOMAN, AccountCodeType.TWENTIES))
                .writerName("최연재")
                .winningPercent(10D)
                .requiredTimeInSeconds(180)
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(3))
                .headCount(10)
                .closedHeadCount(100)
                .questionCount(5)
                .winHeadCount(2)
                .surveyGiveaways(List.of(surveyGiveawayResponse))
                .build();

        when(surveyService.getRecommendNormalSurveyForAnonymous()).thenReturn(List.of(abstractSurveyResponse));

        // when // then
        mockMvc.perform(
                        get("/api/survey/recommend/normal/anonymous")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.response[0].title").value(surveyTitle))
                .andExpect(jsonPath("$.response[0].content").value(surveyContent));
    }

    @DisplayName("로그인 되지 않은 익명의 사용자에게 익명 사용자를 위한 즉시 당첨 설문을 추천한다.")
    @WithMockUser
    @Test
    void getRecommendInstantSurveyForAnonymous() throws Exception {
        // given
        GiveawayResponse giveawayResponse = GiveawayResponse
                .builder()
                .id(1L)
                .giveawayType(GiveawayType.COFFEE)
                .price(4500)
                .build();
        SurveyGiveawayResponse surveyGiveawayResponse = SurveyGiveawayResponse
                .builder()
                .count(10)
                .giveawayResponse(giveawayResponse)
                .build();

        String surveyTitle = "비오는 날에 무엇을 하시나요?";
        String surveyContent = "카눈 태풍에 조심하세요";
        SurveyCategoryType surveyCategoryType = SurveyCategoryType.NORMAL;
        AbstractSurveyResponse abstractSurveyResponse = AbstractSurveyResponse
                .builder()
                .id(1L)
                .title(surveyTitle)
                .content(surveyContent)
                .img(null)
                .surveyCategoryType(surveyCategoryType)
                .surveyTarget(List.of(AccountCodeType.MAN, AccountCodeType.WOMAN, AccountCodeType.TWENTIES))
                .writerName("최연재")
                .winningPercent(10D)
                .requiredTimeInSeconds(180)
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(3))
                .headCount(10)
                .closedHeadCount(100)
                .questionCount(5)
                .winHeadCount(2)
                .surveyGiveaways(List.of(surveyGiveawayResponse))
                .build();

        when(surveyService.getRecommendInstantSurveyForAnonymous()).thenReturn(List.of(abstractSurveyResponse));

        // when // then
        mockMvc.perform(
                        get("/api/survey/recommend/instant/anonymous")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.response[0].title").value(surveyTitle))
                .andExpect(jsonPath("$.response[0].content").value(surveyContent));
    }

    @DisplayName("로그인 되지 않은 익명의 사용자에게 익명 사용자를 위한 쇼트 타임 설문을 추천한다.")
    @WithMockUser
    @Test
    void getRecommendShortTimeSurveyForAnonymous() throws Exception {
        // given
        GiveawayResponse giveawayResponse = GiveawayResponse
                .builder()
                .id(1L)
                .giveawayType(GiveawayType.COFFEE)
                .price(4500)
                .build();
        SurveyGiveawayResponse surveyGiveawayResponse = SurveyGiveawayResponse
                .builder()
                .count(10)
                .giveawayResponse(giveawayResponse)
                .build();

        String surveyTitle = "2023 여름이 많이 덥나요?";
        String surveyContent = "이 또한 지나갈 것입니다.";
        SurveyCategoryType surveyCategoryType = SurveyCategoryType.NORMAL;
        AbstractSurveyResponse abstractSurveyResponse = AbstractSurveyResponse
                .builder()
                .id(1L)
                .title(surveyTitle)
                .content(surveyContent)
                .img(null)
                .surveyCategoryType(surveyCategoryType)
                .surveyTarget(List.of(AccountCodeType.MAN, AccountCodeType.WOMAN, AccountCodeType.TWENTIES))
                .writerName("최연재")
                .winningPercent(10D)
                .requiredTimeInSeconds(180)
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(3))
                .headCount(10)
                .closedHeadCount(100)
                .questionCount(5)
                .winHeadCount(2)
                .surveyGiveaways(List.of(surveyGiveawayResponse))
                .build();

        when(surveyService.getRecommendShortTimeSurveyForAnonymous()).thenReturn(List.of(abstractSurveyResponse));

        // when // then
        mockMvc.perform(
                        get("/api/survey/recommend/time/anonymous")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.response[0].title").value(surveyTitle))
                .andExpect(jsonPath("$.response[0].content").value(surveyContent));
    }

    @DisplayName("로그인된 사용자에게 일반(타임어택) 설문을 추천한다.")
    @WithMockUser
    @Test
    void getRecommendNormalSurveyForUser() throws Exception {
        // given
        GiveawayResponse giveawayResponse = GiveawayResponse
                .builder()
                .id(1L)
                .giveawayType(GiveawayType.COFFEE)
                .price(4500)
                .build();
        SurveyGiveawayResponse surveyGiveawayResponse = SurveyGiveawayResponse
                .builder()
                .count(10)
                .giveawayResponse(giveawayResponse)
                .build();

        String surveyTitle = "어떤 노래를 좋아하시나요?";
        String surveyContent = "발라드, 팝송, 힙합, 클래식, ...";
        SurveyCategoryType surveyCategoryType = SurveyCategoryType.NORMAL;
        AbstractSurveyResponse abstractSurveyResponse = AbstractSurveyResponse
                .builder()
                .id(1L)
                .title(surveyTitle)
                .content(surveyContent)
                .img(null)
                .surveyCategoryType(surveyCategoryType)
                .surveyTarget(List.of(AccountCodeType.MAN, AccountCodeType.WOMAN, AccountCodeType.TWENTIES))
                .writerName("최연재")
                .winningPercent(10D)
                .requiredTimeInSeconds(180)
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(3))
                .headCount(10)
                .closedHeadCount(100)
                .questionCount(5)
                .winHeadCount(2)
                .surveyGiveaways(List.of(surveyGiveawayResponse))
                .build();

        when(surveyService.getRecommendNormalSurveyForUser()).thenReturn(List.of(abstractSurveyResponse));

        // when // then
        mockMvc.perform(
                        get("/api/survey/recommend/normal/user")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.response[0].title").value(surveyTitle))
                .andExpect(jsonPath("$.response[0].content").value(surveyContent));
    }

    @DisplayName("로그인된 사용자에게 즉시 당첨 설문을 추천한다.")
    @WithMockUser
    @Test
    void getRecommendInstantSurveyForUser() throws Exception {
        // given
        GiveawayResponse giveawayResponse = GiveawayResponse
                .builder()
                .id(1L)
                .giveawayType(GiveawayType.COFFEE)
                .price(4500)
                .build();
        SurveyGiveawayResponse surveyGiveawayResponse = SurveyGiveawayResponse
                .builder()
                .count(10)
                .giveawayResponse(giveawayResponse)
                .build();

        String surveyTitle = "어떤 나라에 여행을 가고 싶은신가요?";
        String surveyContent = "프랑스, 이탈리아, 스페인, ...";
        SurveyCategoryType surveyCategoryType = SurveyCategoryType.NORMAL;
        AbstractSurveyResponse abstractSurveyResponse = AbstractSurveyResponse
                .builder()
                .id(1L)
                .title(surveyTitle)
                .content(surveyContent)
                .img(null)
                .surveyCategoryType(surveyCategoryType)
                .surveyTarget(List.of(AccountCodeType.MAN, AccountCodeType.WOMAN, AccountCodeType.TWENTIES))
                .writerName("최연재")
                .winningPercent(10D)
                .requiredTimeInSeconds(180)
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(3))
                .headCount(10)
                .closedHeadCount(100)
                .questionCount(5)
                .winHeadCount(2)
                .surveyGiveaways(List.of(surveyGiveawayResponse))
                .build();

        when(surveyService.getRecommendInstantSurveyForUser()).thenReturn(List.of(abstractSurveyResponse));

        // when // then
        mockMvc.perform(
                        get("/api/survey/recommend/instant/user")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.response[0].title").value(surveyTitle))
                .andExpect(jsonPath("$.response[0].content").value(surveyContent));
    }

    @DisplayName("로그인된 사용자에게 쇼트 타임 설문을 추천한다.")
    @WithMockUser
    @Test
    void getRecommendShortTimeSurveyForUser() throws Exception {
        // given
        GiveawayResponse giveawayResponse = GiveawayResponse
                .builder()
                .id(1L)
                .giveawayType(GiveawayType.COFFEE)
                .price(4500)
                .build();
        SurveyGiveawayResponse surveyGiveawayResponse = SurveyGiveawayResponse
                .builder()
                .count(10)
                .giveawayResponse(giveawayResponse)
                .build();

        String surveyTitle = "어떤 커피를 좋아하시나요?";
        String surveyContent = "산미 VS 다크";
        SurveyCategoryType surveyCategoryType = SurveyCategoryType.NORMAL;
        AbstractSurveyResponse abstractSurveyResponse = AbstractSurveyResponse
                .builder()
                .id(1L)
                .title(surveyTitle)
                .content(surveyContent)
                .img(null)
                .surveyCategoryType(surveyCategoryType)
                .surveyTarget(List.of(AccountCodeType.MAN, AccountCodeType.WOMAN, AccountCodeType.TWENTIES))
                .writerName("최연재")
                .winningPercent(10D)
                .requiredTimeInSeconds(180)
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(3))
                .headCount(10)
                .closedHeadCount(100)
                .questionCount(5)
                .winHeadCount(2)
                .surveyGiveaways(List.of(surveyGiveawayResponse))
                .build();

        when(surveyService.getRecommendShortTimeSurveyForUser()).thenReturn(List.of(abstractSurveyResponse));

        // when // then
        mockMvc.perform(
                        get("/api/survey/recommend/time/user")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.response[0].title").value(surveyTitle))
                .andExpect(jsonPath("$.response[0].content").value(surveyContent));
    }

    @DisplayName("설문 ID를 이용하여 설문 디테일 페이지에서 사용할 설문의 추상적인 정보를 가져온다.")
    @WithMockUser
    @Test
    void surveyDetail() throws Exception {
        // given
        GiveawayResponse giveawayResponse = GiveawayResponse
                .builder()
                .id(1L)
                .giveawayType(GiveawayType.COFFEE)
                .price(4500)
                .build();
        SurveyGiveawayResponse surveyGiveawayResponse = SurveyGiveawayResponse
                .builder()
                .count(10)
                .giveawayResponse(giveawayResponse)
                .build();

        Long surveyId = 1L;
        String surveyTitle = "어떤 커피를 좋아하시나요?";
        String surveyContent = "산미 VS 다크";
        SurveyCategoryType surveyCategoryType = SurveyCategoryType.NORMAL;
        AbstractSurveyResponse abstractSurveyResponse = AbstractSurveyResponse
                .builder()
                .id(surveyId)
                .title(surveyTitle)
                .content(surveyContent)
                .img(null)
                .surveyCategoryType(surveyCategoryType)
                .surveyTarget(List.of(AccountCodeType.MAN, AccountCodeType.WOMAN, AccountCodeType.TWENTIES))
                .writerName("최연재")
                .winningPercent(10D)
                .requiredTimeInSeconds(180)
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(3))
                .headCount(10)
                .closedHeadCount(100)
                .questionCount(5)
                .winHeadCount(2)
                .surveyGiveaways(List.of(surveyGiveawayResponse))
                .build();

        when(surveyService.getSurveyDetail(surveyId)).thenReturn(abstractSurveyResponse);

        // when // then
        mockMvc.perform(
                        get("/api/survey/detail/{surveyId}", surveyId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.response.title").value(surveyTitle))
                .andExpect(jsonPath("$.response.content").value(surveyContent));
    }

    @DisplayName("설문 ID를 이용하여 설문 디테일 페이지에서 사용할 사용자들의 응답결과 값을 가져온다.")
    @WithMockUser
    @Test
    void getSurveyAnswerLogs() throws Exception {
        // given
        String surveyAnswerName1 = "최연재";
        int surveySubmitOrder1 = 1;
        SurveyAnswerResponse surveyAnswerResponse1 = SurveyAnswerResponse
                .builder()
                .answerTime(LocalDateTime.now().minusHours(2))
                .name(surveyAnswerName1)
                .giveAwayName("스타벅스 아이스 아메리카노")
                .isWin(true)
                .submitOrder(surveySubmitOrder1)
                .surveyCategoryType(SurveyCategoryType.NORMAL)
                .build();

        String surveyAnswerName2 = "최연재";
        int surveySubmitOrder2 = 1;
        SurveyAnswerResponse surveyAnswerResponse2 = SurveyAnswerResponse
                .builder()
                .answerTime(LocalDateTime.now().minusHours(1))
                .name(surveyAnswerName2)
                .giveAwayName("스타벅스 아이스 아메리카노")
                .isWin(true)
                .submitOrder(surveySubmitOrder2)
                .surveyCategoryType(SurveyCategoryType.NORMAL)
                .build();

        Long surveyId = 1L;
        when(surveyService.getSurveyAnswers(surveyId)).thenReturn(List.of(surveyAnswerResponse1, surveyAnswerResponse2));

        // when // then
        mockMvc.perform(
                        get("/api/survey/answer/log/{surveyId}", surveyId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.response[0].name").value(surveyAnswerName1))
                .andExpect(jsonPath("$.response[0].submitOrder").value(surveySubmitOrder1))
                .andExpect(jsonPath("$.response[1].name").value(surveyAnswerName2))
                .andExpect(jsonPath("$.response[1].submitOrder").value(surveySubmitOrder2));
    }

    @DisplayName("설문 ID를 이용하여 설문 응답 페이지를 위한 설문 상세 정보를 가져온다.")
    @WithMockUser
    @Test
    void getSurvey() throws Exception {
        // given
        String multipleChoice1Content = "사과";
        Long multipleChoice1LinkNumber = 0L;

        String multipleChoice2Content = "배";
        Long multipleChoice2LinkNumber = 0L;

        Long question1Number = 1L;
        String question1Title = "당신은 어떤 과일을 좋아하십니까?";
        QuestionCategoryType question1QuestionCategoryType = QuestionCategoryType.MULTIPLE_CHOICE;

        Long question2Number = 2L;
        String question2Title = "당신은 어떤 음료를 좋아하십니까?";
        QuestionCategoryType question2QuestionCategoryType = QuestionCategoryType.SHORT_FORM;

        int giveawayCount = 10;

        String surveyTitle = "당신은 어떤 종류의 과일과 음료를 좋아하나요?";
        LocalDateTime surveyStartTime = LocalDateTime.now().plusDays(1);
        LocalDateTime surveyEndTime = LocalDateTime.now().plusDays(3);
        int headCount = 0;
        int closedHeadCount = 100;
        SurveyCategoryType surveyCategoryType = SurveyCategoryType.INSTANT_WIN;
        List<AccountCodeType> surveyTarget = List.of(AccountCodeType.MAN, AccountCodeType.WOMAN, AccountCodeType.TWENTIES);

        MultipleChoiceResponse multipleChoiceResponse1 = MultipleChoiceResponse
                .builder()
                .id(1L)
                .content(multipleChoice1Content)
                .linkNumber(multipleChoice1LinkNumber)
                .build();

        MultipleChoiceResponse multipleChoiceResponse2 = MultipleChoiceResponse
                .builder()
                .id(1L)
                .content(multipleChoice2Content)
                .linkNumber(multipleChoice2LinkNumber)
                .build();

        QuestionResponse questionResponse1 = QuestionResponse
                .builder()
                .id(1L)
                .questionNumber(question1Number)
                .title(question1Title)
                .content(null)
                .imgAddress(null)
                .essential(true)
                .type(question1QuestionCategoryType)
                .multipleChoices(List.of(multipleChoiceResponse1, multipleChoiceResponse2))
                .build();

        QuestionResponse questionResponse2 = QuestionResponse
                .builder()
                .id(1L)
                .questionNumber(question2Number)
                .title(question2Title)
                .content(null)
                .imgAddress(null)
                .essential(true)
                .type(question2QuestionCategoryType)
                .multipleChoices(null)
                .build();

        GiveawayType giveawayType = GiveawayType.COFFEE;
        String giveawayName = "스타벅스 아이스 아메리카노";
        int giveawayPrice = 4500;
        GiveawayResponse giveawayResponse = GiveawayResponse
                .builder()
                .id(1L)
                .giveawayType(giveawayType)
                .name(giveawayName)
                .price(giveawayPrice)
                .build();

        SurveyGiveawayResponse surveyGiveawayResponse = SurveyGiveawayResponse
                .builder()
                .id(1L)
                .count(giveawayCount)
                .giveawayResponse(giveawayResponse)
                .build();

        Long surveyId = 1L;
        SurveyResponse surveyResponse = SurveyResponse
                .builder()
                .id(surveyId)
                .title(surveyTitle)
                .content(null)
                .img(null)
                .startTime(surveyStartTime)
                .endTime(surveyEndTime)
                .headCount(headCount)
                .closedHeadCount(closedHeadCount)
                .writer(1L)
                .totalGiveawayCount(giveawayCount)
                .requiredTimeInSeconds(30)
                .closed(true)
                .winningPercent(10D)
                .surveyCategoryType(surveyCategoryType)
                .questions(List.of(questionResponse1, questionResponse2))
                .surveyGiveaways(List.of(surveyGiveawayResponse))
                .surveyTarget(surveyTarget)
                .build();

        when(surveyService.getSurvey(surveyId)).thenReturn(surveyResponse);

        // when // then
        mockMvc.perform(
                        get("/api/survey/{surveyId}", surveyId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.response.title").value(surveyTitle))
                .andExpect(jsonPath("$.response.headCount").value(headCount))
                .andExpect(jsonPath("$.response.closedHeadCount").value(closedHeadCount));
    }

    @DisplayName("자신이 제작한 설문 목록을 가져온다.")
    @WithMockUser
    @Test
    void getWritingSurveys() throws Exception {
        // given
        GiveawayResponse giveawayResponse = GiveawayResponse
                .builder()
                .id(1L)
                .giveawayType(GiveawayType.COFFEE)
                .price(4500)
                .build();
        SurveyGiveawayResponse surveyGiveawayResponse = SurveyGiveawayResponse
                .builder()
                .count(10)
                .giveawayResponse(giveawayResponse)
                .build();

        Long surveyId = 1L;
        String surveyTitle = "다들 안녕하신가요?";
        String surveyContent = "Be Happy!!";
        SurveyCategoryType surveyCategoryType = SurveyCategoryType.NORMAL;
        AbstractSurveyResponse abstractSurveyResponse = AbstractSurveyResponse
                .builder()
                .id(surveyId)
                .title(surveyTitle)
                .content(surveyContent)
                .img(null)
                .surveyCategoryType(surveyCategoryType)
                .surveyTarget(List.of(AccountCodeType.MAN, AccountCodeType.WOMAN, AccountCodeType.TWENTIES))
                .writerName("최연재")
                .winningPercent(10D)
                .requiredTimeInSeconds(180)
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(3))
                .headCount(10)
                .closedHeadCount(100)
                .questionCount(5)
                .winHeadCount(2)
                .surveyGiveaways(List.of(surveyGiveawayResponse))
                .build();

        when(surveyService.getMySurveys()).thenReturn(List.of(abstractSurveyResponse));

        // when // then
        mockMvc.perform(
                        get("/api/survey/writing")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.response[0].title").value(surveyTitle))
                .andExpect(jsonPath("$.response[0].content").value(surveyContent));
    }

    @DisplayName("자신이 응답한 설문 목록을 가져온다.")
    @WithMockUser
    @Test
    void getAnsweredSurveys() throws Exception {
        // given
        GiveawayResponse giveawayResponse = GiveawayResponse
                .builder()
                .id(1L)
                .giveawayType(GiveawayType.COFFEE)
                .price(4500)
                .build();
        SurveyGiveawayResponse surveyGiveawayResponse = SurveyGiveawayResponse
                .builder()
                .count(10)
                .giveawayResponse(giveawayResponse)
                .build();

        Long surveyId = 1L;
        String surveyTitle = "다들 안녕하신가요?";
        String surveyContent = "Be Happy!!";
        SurveyCategoryType surveyCategoryType = SurveyCategoryType.NORMAL;
        AbstractSurveyResponse abstractSurveyResponse = AbstractSurveyResponse
                .builder()
                .id(surveyId)
                .title(surveyTitle)
                .content(surveyContent)
                .img(null)
                .surveyCategoryType(surveyCategoryType)
                .surveyTarget(List.of(AccountCodeType.MAN, AccountCodeType.WOMAN, AccountCodeType.TWENTIES))
                .writerName("최연재")
                .winningPercent(10D)
                .requiredTimeInSeconds(180)
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(3))
                .headCount(10)
                .closedHeadCount(100)
                .questionCount(5)
                .winHeadCount(2)
                .surveyGiveaways(List.of(surveyGiveawayResponse))
                .build();

        when(surveyService.getAnsweredSurveys()).thenReturn(List.of(abstractSurveyResponse));

        // when // then
        mockMvc.perform(
                        get("/api/survey/answered")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.response[0].title").value(surveyTitle))
                .andExpect(jsonPath("$.response[0].content").value(surveyContent));
    }

    @DisplayName("설문 응답이 가능한지 검사한다.")
    @WithMockUser
    @Test
    void surveyCheckAnswerPossible() throws Exception {
        Long surveyId = 1L;
        // given
        when(surveyService.getAnswerPossible(surveyId)).thenReturn(AnswerPossibleType.CANANSWER);

        // when // then
        mockMvc.perform(
                        get("/api/survey/possible/" + surveyId)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("설문 통계를 가져온다.")
    @WithMockUser
    @Test
    void getStatistics() throws Exception {
        // given
        String multipleChoice1Content = "사과";
        Long multipleChoice1LinkNumber = 0L;

        String multipleChoice2Content = "배";
        Long multipleChoice2LinkNumber = 0L;

        Long question1Number = 1L;
        String question1Title = "당신은 어떤 과일을 좋아하십니까?";
        QuestionCategoryType question1QuestionCategoryType = QuestionCategoryType.MULTIPLE_CHOICE;

        Long question2Number = 2L;
        String question2Title = "당신은 어떤 음료를 좋아하십니까?";
        QuestionCategoryType question2QuestionCategoryType = QuestionCategoryType.SHORT_FORM;

        int giveawayCount = 10;

        String surveyTitle = "당신은 어떤 종류의 과일과 음료를 좋아하나요?";
        LocalDateTime surveyStartTime = LocalDateTime.now().plusDays(1);
        LocalDateTime surveyEndTime = LocalDateTime.now().plusDays(3);
        int headCount = 0;
        int closedHeadCount = 100;
        SurveyCategoryType surveyCategoryType = SurveyCategoryType.INSTANT_WIN;
        List<AccountCodeType> surveyTarget = List.of(AccountCodeType.MAN, AccountCodeType.WOMAN, AccountCodeType.TWENTIES);

        MultipleChoiceResponse multipleChoiceResponse1 = MultipleChoiceResponse
                .builder()
                .id(1L)
                .content(multipleChoice1Content)
                .linkNumber(multipleChoice1LinkNumber)
                .build();

        MultipleChoiceResponse multipleChoiceResponse2 = MultipleChoiceResponse
                .builder()
                .id(1L)
                .content(multipleChoice2Content)
                .linkNumber(multipleChoice2LinkNumber)
                .build();

        QuestionResponse questionResponse1 = QuestionResponse
                .builder()
                .id(1L)
                .questionNumber(question1Number)
                .title(question1Title)
                .content(null)
                .imgAddress(null)
                .essential(true)
                .type(question1QuestionCategoryType)
                .multipleChoices(List.of(multipleChoiceResponse1, multipleChoiceResponse2))
                .build();

        QuestionResponse questionResponse2 = QuestionResponse
                .builder()
                .id(1L)
                .questionNumber(question2Number)
                .title(question2Title)
                .content(null)
                .imgAddress(null)
                .essential(true)
                .type(question2QuestionCategoryType)
                .multipleChoices(null)
                .build();

        GiveawayType giveawayType = GiveawayType.COFFEE;
        String giveawayName = "스타벅스 아이스 아메리카노";
        int giveawayPrice = 4500;
        GiveawayResponse giveawayResponse = GiveawayResponse
                .builder()
                .id(1L)
                .giveawayType(giveawayType)
                .name(giveawayName)
                .price(giveawayPrice)
                .build();

        SurveyGiveawayResponse surveyGiveawayResponse = SurveyGiveawayResponse
                .builder()
                .id(1L)
                .count(giveawayCount)
                .giveawayResponse(giveawayResponse)
                .build();

        QuestionAnswerStatisticsResponse questionAnswerStatisticsResponse1
                = QuestionAnswerStatisticsResponse
                .builder()
                .questionId(1L)
                .questionNumber(1L)
                .questionCategoryType(questionResponse1.getType())
                .answers(List.of("1", "2", "3", "3"))
                .build();

        QuestionAnswerStatisticsResponse questionAnswerStatisticsResponse2
                = QuestionAnswerStatisticsResponse
                .builder()
                .questionId(2L)
                .questionNumber(2L)
                .questionCategoryType(questionResponse2.getType())
                .answers(List.of("포카리 스웨트", "파워에이드", "코카콜라", "사이다"))
                .build();

        Long surveyId = 1L;
        StatisticsSurveyResponse statisticsSurveyResponse = StatisticsSurveyResponse
                .builder()
                .id(surveyId)
                .title(surveyTitle)
                .content(null)
                .startTime(surveyStartTime)
                .endTime(surveyEndTime)
                .img(null)
                .headCount(headCount)
                .closedHeadCount(closedHeadCount)
                .writerName("최연재")
                .totalGiveawayCount(giveawayCount)
                .requiredTimeInSeconds(30)
                .closed(true)
                .surveyCategoryType(surveyCategoryType)
                .questions(List.of(questionResponse1, questionResponse2))
                .surveyGiveaways(List.of(surveyGiveawayResponse))
                .surveyTarget(surveyTarget)
                .questionAnswers(List.of(questionAnswerStatisticsResponse1, questionAnswerStatisticsResponse2))
                .build();

        when(surveyService.getStatistics(surveyId)).thenReturn(statisticsSurveyResponse);

        // when // then
        mockMvc.perform(
                        get("/api/survey/statistics/{surveyId}", surveyId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.response.title").value(surveyTitle))
                .andExpect(jsonPath("$.response.headCount").value(headCount))
                .andExpect(jsonPath("$.response.closedHeadCount").value(closedHeadCount))
                .andExpect(jsonPath("$.response.questionAnswers.size()").value(2))
                .andExpect(jsonPath("$.response.questionAnswers[0].questionCategoryType").value(questionResponse1.getType().toString()))
                .andExpect(jsonPath("$.response.questionAnswers[1].questionCategoryType").value(questionResponse2.getType().toString()));
    }
}