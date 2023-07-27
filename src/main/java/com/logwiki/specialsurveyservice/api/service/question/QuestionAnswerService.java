package com.logwiki.specialsurveyservice.api.service.question;


import com.logwiki.specialsurveyservice.api.service.question.request.QuestionAnswerCreateServiceRequest;
import com.logwiki.specialsurveyservice.api.service.question.response.QuestionAnswerCreateServiceResponse;
import com.logwiki.specialsurveyservice.api.service.surveyresult.SurveyResultService;
import com.logwiki.specialsurveyservice.domain.account.Account;
import com.logwiki.specialsurveyservice.domain.account.AccountRepository;
import com.logwiki.specialsurveyservice.domain.question.Question;
import com.logwiki.specialsurveyservice.domain.question.QuestionRepository;
import com.logwiki.specialsurveyservice.domain.questionanswer.QuestionAnswer;
import com.logwiki.specialsurveyservice.domain.questionanswer.QuestionAnswerRepository;
import com.logwiki.specialsurveyservice.exception.BaseException;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionAnswerService {

    private final QuestionAnswerRepository questionAnswerRepository;
    private final QuestionRepository questionRepository;
    private final AccountRepository accountRepository;
    private final SurveyResultService surveyResultService;

    @Transactional
    public List<QuestionAnswerCreateServiceResponse> addQuestionAnswer(
            LocalDateTime writeDate,
            Long surveyId,
            String userEmail,
            List<QuestionAnswerCreateServiceRequest> dto) {

        Account account = accountRepository.findOneWithAuthoritiesByEmail(userEmail)
                .orElseThrow(() -> new BaseException("없는 유저입니다.", 1001));
        List<Question> questions = questionRepository.findBySurveyId(surveyId).orElseThrow(
                () -> new BaseException("없는 설문입니다.", 2005));

        List<QuestionAnswerCreateServiceResponse> result = new ArrayList<>();

        for (QuestionAnswerCreateServiceRequest answer : dto) {
            boolean notFoundQuestion = true;
            for (Question question : questions) {
                if (question.getId().equals(answer.getQuestionId())) {
                    QuestionAnswer questionAnswer = answer.toEntity(question, account);
                    questionAnswer.setWriteDate(writeDate);
                    questionAnswerRepository.save(questionAnswer);
                    result.add(new QuestionAnswerCreateServiceResponse(questionAnswer));
                    notFoundQuestion = false;
                }
            }
            if (notFoundQuestion) {
                throw new BaseException("없는 문항에 답변을 할 수 없습니다.", 3000);
            }
        }

        surveyResultService.addSubmitResult(surveyId, userEmail, writeDate);

        return result;
    }
}
