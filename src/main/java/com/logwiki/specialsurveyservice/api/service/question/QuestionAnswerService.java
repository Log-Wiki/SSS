package com.logwiki.specialsurveyservice.api.service.question;


import com.logwiki.specialsurveyservice.api.service.question.request.QuestionAnswerCreateServiceRequest;
import com.logwiki.specialsurveyservice.api.service.question.response.QuestionAnswerCreateServiceResponse;
import com.logwiki.specialsurveyservice.domain.account.Account;
import com.logwiki.specialsurveyservice.domain.account.AccountRepository;
import com.logwiki.specialsurveyservice.domain.questionanswer.QuestionAnswerRepository;
import com.logwiki.specialsurveyservice.exception.BaseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class QuestionAnswerService {

    private final QuestionAnswerRepository questionAnswerRepository;
    private final AccountRepository accountRepository;

    public QuestionAnswerCreateServiceResponse addQuestionAnswer(
            QuestionAnswerCreateServiceRequest dto) {
        Account account = accountRepository.findOneWithAuthoritiesByEmail(dto.getUserEmail())
                .orElseThrow(() -> new BaseException("없는 유저입니다.", 1001));
        // 설문 만들기전 임시로 문항 체크를 뺌
//        Question question = questionAnswerRepository.findById(dto.getQuestionNumber())
//                .orElseThrow(() -> new BaseException("없는 문항입니다.", 2004)).getQuestion();
        return new QuestionAnswerCreateServiceResponse(
                questionAnswerRepository.save(dto.toEntity(null, account)));
    }
}
