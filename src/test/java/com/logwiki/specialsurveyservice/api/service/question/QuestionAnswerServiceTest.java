package com.logwiki.specialsurveyservice.api.service.question;

import com.logwiki.specialsurveyservice.api.service.question.request.QuestionAnswerCreateServiceRequest;
import com.logwiki.specialsurveyservice.domain.account.Account;
import com.logwiki.specialsurveyservice.domain.account.AccountRepository;
import com.logwiki.specialsurveyservice.domain.accountcode.AccountCodeType;
import com.logwiki.specialsurveyservice.domain.authority.Authority;
import com.logwiki.specialsurveyservice.domain.authority.AuthorityType;
import com.logwiki.specialsurveyservice.domain.question.QuestionRepository;
import com.logwiki.specialsurveyservice.domain.questionanswer.QuestionAnswerRepository;
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

    @InjectMocks
    private QuestionAnswerService questionAnswerService;

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private QuestionAnswerRepository questionAnswerRepository;
    @Mock
    private QuestionRepository questionRepository;

    @DisplayName("없는 설문에 응답을 했을 경우 에러를 반환")
    @Test
    void notFoundQuestionThrowError() {
        // given
        Authority authority = Authority.builder()
                .type(AuthorityType.ROLE_USER)
                .build();
        List<Authority> authorities = List.of(authority);

        String email = "duswo0624@naver.com";
        Account account = Account.create(
                email,
                "1234",
                AccountCodeType.MAN,
                AccountCodeType.TWENTIES,
                "최연재",
                "010-1234-5678",
                LocalDate.of(1997, 6, 24),
                authorities
        );
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
}