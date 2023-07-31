package com.logwiki.specialsurveyservice.api.service.question;

import com.logwiki.specialsurveyservice.api.service.question.request.QuestionAnswerCreateServiceRequest;
import com.logwiki.specialsurveyservice.domain.account.Account;
import com.logwiki.specialsurveyservice.domain.account.AccountRepository;
import com.logwiki.specialsurveyservice.domain.accountcode.AccountCode;
import com.logwiki.specialsurveyservice.domain.accountcode.AccountCodeType;
import com.logwiki.specialsurveyservice.domain.authority.Authority;
import com.logwiki.specialsurveyservice.domain.authority.AuthorityType;
import com.logwiki.specialsurveyservice.domain.question.Question;
import com.logwiki.specialsurveyservice.domain.question.QuestionRepository;
import com.logwiki.specialsurveyservice.domain.questionanswer.QuestionAnswerRepository;
import com.logwiki.specialsurveyservice.domain.questioncategory.QuestionCategoryType;
import com.logwiki.specialsurveyservice.domain.surveytarget.SurveyTarget;
import com.logwiki.specialsurveyservice.domain.surveytarget.SurveyTargetRepository;
import com.logwiki.specialsurveyservice.exception.BaseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuestionAnswerServiceTest {

    private static final String EMAIL = "duswo0624@naver.com";
    @InjectMocks
    private QuestionAnswerService questionAnswerService;

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private QuestionAnswerRepository questionAnswerRepository;
    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private SurveyTargetRepository surveyTargetRepository;

    @DisplayName("없는 설문에 응답을 했을 경우 에러를 반환")
    @Test
    void notFoundQuestionThrowError() {
        // given
        Account account = getAccount();
        List<QuestionAnswerCreateServiceRequest> dtoList = new ArrayList<>();
        QuestionAnswerCreateServiceRequest dto = QuestionAnswerCreateServiceRequest.builder()
                .questionId(1L)
                .multipleChoiceAnswer(1L)
                .build();
        dtoList.add(dto);
        LocalDateTime nowDate = LocalDateTime.now();
        Long surveyId = 1L;
        String useEmail = "user@naver.com";

        // when
        when(questionRepository.findBySurveyId(any()))
                .thenReturn(Optional.ofNullable(null));
        when(accountRepository.findOneWithAuthoritiesByEmail(any()))
                .thenReturn(Optional.ofNullable(account));

        // then
        Assertions.assertThrows(BaseException.class, () -> {
            questionAnswerService.addQuestionAnswer(nowDate, surveyId
                    , useEmail, dtoList);
        });
    }

    @DisplayName("문항의 개수보다 문항 답변 개수가 작을경우 에러를 반환.")
    @Test
    void needAllAnswer() {
        // given

        Account account = getAccount();

        List<QuestionAnswerCreateServiceRequest> dtoList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            QuestionAnswerCreateServiceRequest dto = QuestionAnswerCreateServiceRequest.builder()
                    .questionId(1L)
                    .multipleChoiceAnswer(1L)
                    .build();
            dtoList.add(dto);
        }
        List<Question> questions = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            Question question = Question.builder()
                    .questionNumber(Long.parseLong(String.valueOf(i)))
                    .content("문제" + String.valueOf(i))
                    .type(QuestionCategoryType.MULTIPLE_CHOICE)
                    .multipleChoice(new ArrayList<>())
                    .build();
            questions.add(question);
        }

        List<SurveyTarget> surveyTargets = new ArrayList<>();
        AccountCode accountCode1 = AccountCode.builder()
                .type(AccountCodeType.MAN)
                .build();
        SurveyTarget surveyTarget1 = SurveyTarget.builder()
                .accountCode(accountCode1)
                .build();
        AccountCode accountCode2 = AccountCode.builder()
                .type(AccountCodeType.TWENTIES)
                .build();
        SurveyTarget surveyTarget2 = SurveyTarget.builder()
                .accountCode(accountCode2)
                .build();
        surveyTargets.add(surveyTarget1);
        surveyTargets.add(surveyTarget2);


        LocalDateTime nowDate = LocalDateTime.now();
        Long surveyId = 1L;
        String useEmail = "user@naver.com";

        // when
        when(questionRepository.findBySurveyId(any()))
                .thenReturn(Optional.of(questions));
        when(accountRepository.findOneWithAuthoritiesByEmail(any()))
                .thenReturn(Optional.ofNullable(account));
        when(surveyTargetRepository.findSurveyTargetBySurvey_Id(any()))
                .thenReturn(surveyTargets);

        // then
        Assertions.assertThrows(BaseException.class, () -> {
            questionAnswerService.addQuestionAnswer(nowDate, surveyId
                    , useEmail, dtoList);
        });
    }

    @DisplayName("설문 답변은 대상자만 가능하다.")
    @Test
    void essentialIsSurveyTarget() {
        // given

        Account account = getAccount();

        List<QuestionAnswerCreateServiceRequest> dtoList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            QuestionAnswerCreateServiceRequest dto = QuestionAnswerCreateServiceRequest.builder()
                    .questionId(1L)
                    .multipleChoiceAnswer(1L)
                    .build();
            dtoList.add(dto);
        }
        List<Question> questions = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            Question question = Question.builder()
                    .questionNumber(Long.parseLong(String.valueOf(i)))
                    .content("문제" + String.valueOf(i))
                    .type(QuestionCategoryType.MULTIPLE_CHOICE)
                    .multipleChoice(new ArrayList<>())
                    .build();
            questions.add(question);
        }

        List<SurveyTarget> surveyTargets = new ArrayList<>();
        AccountCode accountCode1 = AccountCode.builder()
                .type(AccountCodeType.MAN)
                .build();
        SurveyTarget surveyTarget1 = SurveyTarget.builder()
                .accountCode(accountCode1)
                .build();
        surveyTargets.add(surveyTarget1);


        LocalDateTime nowDate = LocalDateTime.now();
        Long surveyId = 1L;
        String useEmail = "user@naver.com";

        // when
        when(questionRepository.findBySurveyId(any()))
                .thenReturn(Optional.of(questions));
        when(accountRepository.findOneWithAuthoritiesByEmail(any()))
                .thenReturn(Optional.ofNullable(account));
        when(surveyTargetRepository.findSurveyTargetBySurvey_Id(any()))
                .thenReturn(surveyTargets);

        // then
        Assertions.assertThrows(BaseException.class, () -> {
            questionAnswerService.addQuestionAnswer(nowDate, surveyId
                    , useEmail, dtoList);
        });
    }

    private Account getAccount() {
        Authority authority = Authority.builder()
                .type(AuthorityType.ROLE_USER)
                .build();
        List<Authority> authorities = List.of(authority);
        return Account.create(
                EMAIL,
                "1234",
                AccountCodeType.MAN,
                AccountCodeType.TWENTIES,
                "최연재",
                "010-1234-5678",
                LocalDate.of(1997, 6, 24),
                authorities
        );
    }
}