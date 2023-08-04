package com.logwiki.specialsurveyservice.api.controller.question;

import com.logwiki.specialsurveyservice.ControllerTestSupport;
import com.logwiki.specialsurveyservice.api.controller.question.request.QuestionAnswerCreateRequest;
import com.logwiki.specialsurveyservice.api.controller.question.request.QuestionAnswersCreateRequest;
import com.logwiki.specialsurveyservice.api.service.question.response.QuestionAnswerResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class QuestionControllerTest extends ControllerTestSupport {

    @Nested
    @DisplayName("설문 응답 테스트")
    class QuestionAnswerTest {
        @Nested
        @DisplayName("설문 응답 성공 테스트")
        class success {
            @DisplayName("설문 번호와 설문 답변을 통해 설문에 응답을 한다.")
            @WithMockUser
            @Test
            void answerSurvey() throws Exception {
                // given
                List<QuestionAnswerResponse> questionAnswerResponses = new ArrayList<>();
                QuestionAnswerResponse questionAnswerResponse = QuestionAnswerResponse.builder()
                        .id(1L)
                        .multipleChoiceAnswer(3L)
                        .writer(1L)
                        .writeDate(LocalDateTime.now())
                        .build();
                questionAnswerResponses.add(questionAnswerResponse);

                QuestionAnswerCreateRequest questionAnswerCreateRequest = QuestionAnswerCreateRequest.builder()
                        .questionId(1L)
                        .multipleChoiceAnswer(3L)
                        .build();
                List<QuestionAnswerCreateRequest> answers = new ArrayList<>();
                answers.add(questionAnswerCreateRequest);
                QuestionAnswersCreateRequest questionAnswersCreateRequest = QuestionAnswersCreateRequest.builder()
                        .answers(answers)
                        .build();
                long surveyId = 1L;

                // when // then
                when(questionAnswerService.addQuestionAnswer(any(), any(), any())).thenReturn(questionAnswerResponses);

                mockMvc.perform(
                                post("/api/question/answers")
                                        .param("surveyId", Long.toString(surveyId))
                                        .content(objectMapper.writeValueAsString(questionAnswersCreateRequest))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .with(csrf())
                        )
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").value("true"))
                        .andExpect(jsonPath("$.response[0].id").value(1))
                        .andExpect(jsonPath("$.response[0].multipleChoiceAnswer").value(3));
            }

        }

        @Nested
        @DisplayName("설문 응답 실패 테스트")
        class fail {

            @DisplayName("설문에 답변이 없으면 실패를 반환한다.")
            @WithMockUser
            @Test
            void notContainAnswer() throws Exception {
                // given
                List<QuestionAnswerResponse> questionAnswerResponses = new ArrayList<>();
                QuestionAnswerResponse questionAnswerResponse = QuestionAnswerResponse.builder()
                        .id(1L)
                        .multipleChoiceAnswer(3L)
                        .writer(1L)
                        .writeDate(LocalDateTime.now())
                        .build();
                questionAnswerResponses.add(questionAnswerResponse);

                QuestionAnswerCreateRequest questionAnswerCreateRequest = QuestionAnswerCreateRequest.builder()
                        .questionId(1L)
                        .build();
                List<QuestionAnswerCreateRequest> answers = new ArrayList<>();
                answers.add(questionAnswerCreateRequest);
                QuestionAnswersCreateRequest questionAnswersCreateRequest = QuestionAnswersCreateRequest.builder()
                        .answers(answers)
                        .build();
                long surveyId = 1L;

                // when // then
                when(questionAnswerService.addQuestionAnswer(any(), any(), any())).thenReturn(questionAnswerResponses);

                mockMvc.perform(
                                post("/api/question/answers")
                                        .param("surveyId", Long.toString(surveyId))
                                        .content(objectMapper.writeValueAsString(questionAnswersCreateRequest))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .with(csrf())
                        )
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").value("false"))
                        .andExpect(jsonPath("$.apiError.status").value(3007));
            }

            @DisplayName("한 문항에 주관식과 객관식 모두 답변을하면 실패를 반환한다.")
            @WithMockUser
            @Test
            void canNotAnswerShorFormMultipleChoiceSameTime() throws Exception {
                // given
                List<QuestionAnswerResponse> questionAnswerResponses = new ArrayList<>();
                QuestionAnswerResponse questionAnswerResponse = QuestionAnswerResponse.builder()
                        .id(1L)
                        .multipleChoiceAnswer(3L)
                        .writer(1L)
                        .writeDate(LocalDateTime.now())
                        .build();
                questionAnswerResponses.add(questionAnswerResponse);

                QuestionAnswerCreateRequest questionAnswerCreateRequest = QuestionAnswerCreateRequest.builder()
                        .questionId(1L)
                        .multipleChoiceAnswer(3L)
                        .shortFormAnswer("answer")
                        .build();
                List<QuestionAnswerCreateRequest> answers = new ArrayList<>();
                answers.add(questionAnswerCreateRequest);
                QuestionAnswersCreateRequest questionAnswersCreateRequest = QuestionAnswersCreateRequest.builder()
                        .answers(answers)
                        .build();
                long surveyId = 1L;

                // when // then
                when(questionAnswerService.addQuestionAnswer(any(), any(), any())).thenReturn(questionAnswerResponses);

                mockMvc.perform(
                                post("/api/question/answers")
                                        .param("surveyId", Long.toString(surveyId))
                                        .content(objectMapper.writeValueAsString(questionAnswersCreateRequest))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .with(csrf())
                        )
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").value("false"))
                        .andExpect(jsonPath("$.apiError.status").value(3006));

            }

            @DisplayName("문항 답변에 문제번호는 필수다.")
            @WithMockUser
            @Test
            void needQuestionNumber() throws Exception {
                // given
                List<QuestionAnswerResponse> questionAnswerResponses = new ArrayList<>();
                QuestionAnswerResponse questionAnswerResponse = QuestionAnswerResponse.builder()
                        .id(1L)
                        .multipleChoiceAnswer(3L)
                        .writer(1L)
                        .writeDate(LocalDateTime.now())
                        .build();
                questionAnswerResponses.add(questionAnswerResponse);

                QuestionAnswerCreateRequest questionAnswerCreateRequest = QuestionAnswerCreateRequest.builder()
                        .build();
                List<QuestionAnswerCreateRequest> answers = new ArrayList<>();
                answers.add(questionAnswerCreateRequest);
                QuestionAnswersCreateRequest questionAnswersCreateRequest = QuestionAnswersCreateRequest.builder()
                        .answers(answers)
                        .build();
                long surveyId = 1L;

                // when // then
                when(questionAnswerService.addQuestionAnswer(any(), any(), any())).thenReturn(questionAnswerResponses);

                mockMvc.perform(
                                post("/api/question/answers")
                                        .param("surveyId", Long.toString(surveyId))
                                        .content(objectMapper.writeValueAsString(questionAnswersCreateRequest))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .with(csrf())
                        )
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").value("false"))
                        .andExpect(jsonPath("$.apiError.status").value(1000));
            }
        }
    }


}