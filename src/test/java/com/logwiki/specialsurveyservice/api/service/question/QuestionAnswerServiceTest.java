package com.logwiki.specialsurveyservice.api.service.question;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.logwiki.specialsurveyservice.api.service.question.request.QuestionAnswerCreateServiceRequest;
import com.logwiki.specialsurveyservice.domain.account.Account;
import com.logwiki.specialsurveyservice.domain.account.AccountRepository;
import com.logwiki.specialsurveyservice.domain.authority.Authority;
import com.logwiki.specialsurveyservice.domain.authority.AuthorityType;
import com.logwiki.specialsurveyservice.domain.gender.Gender;
import com.logwiki.specialsurveyservice.domain.questionanswer.QuestionAnswerRepository;
import com.logwiki.specialsurveyservice.exception.BaseException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class QuestionAnswerServiceTest {

    @InjectMocks
    private QuestionAnswerService questionAnswerService;

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private QuestionAnswerRepository questionAnswerRepository;

    @DisplayName("없는 문항에 응답을 했을 경우 에러를 반환")
    @Test
    @Disabled("설문 추가후 변경")
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
                Gender.MALE,
                "최연재",
                "010-1234-5678",
                LocalDate.of(1997, 6, 24),
                authorities
        );
        QuestionAnswerCreateServiceRequest dto = QuestionAnswerCreateServiceRequest.builder()
                .userEmail("asd@naver.com")
                .questionNumber(1L)
                .build();

        // when
        when(questionAnswerRepository.findById(any()))
                .thenReturn(Optional.ofNullable(null));
        when(accountRepository.findOneWithAuthoritiesByEmail(any()))
                .thenReturn(Optional.ofNullable(account));

        // then
        Assertions.assertThrows(BaseException.class, () -> {
            questionAnswerService.addQuestionAnswer(dto);
        });
    }
}