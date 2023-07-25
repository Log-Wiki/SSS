package com.logwiki.specialsurveyservice.api.service.question;


import com.logwiki.specialsurveyservice.api.service.question.request.QuestionAnswerCreateServiceRequest;
import com.logwiki.specialsurveyservice.domain.account.Account;
import com.logwiki.specialsurveyservice.domain.account.AccountRepository;
import com.logwiki.specialsurveyservice.domain.question.Question;
import com.logwiki.specialsurveyservice.domain.question.QuestionRepository;
import com.logwiki.specialsurveyservice.domain.questionanswer.QuestionAnswerRepository;
import com.logwiki.specialsurveyservice.exception.BaseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class QuestionAnswerService {

    private final QuestionAnswerRepository questionAnswerRepository;
    private final QuestionRepository questionRepository;
    private final AccountRepository accountRepository;

    public Long addQuestionAnswer(
            Long surveyId,
            String userEmail,
            List<QuestionAnswerCreateServiceRequest> dto) {
        Account account = accountRepository.findOneWithAuthoritiesByEmail(userEmail)
                .orElseThrow(() -> new BaseException("없는 유저입니다.", 1001));
        // 설문 만들기전 임시로 문항 체크를 뺌
        List<Question> questions = questionRepository.findBySurveyId(surveyId).orElseThrow(
                () -> new BaseException("없는 설문입니다.", 2005));
//        Question question = questionAnswerRepository.findById(dto.getQuestionNumber())
//                .orElseThrow(() -> new BaseException("없는 문항입니다.", 2004)).getQuestion();
//        QuestionAnswerCreateServiceResponse(questionAnswerRepository.save(dto.toEntity(null, account)));
        return 1L;

    }
}
