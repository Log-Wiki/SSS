package com.logwiki.specialsurveyservice.api.service.question;


import com.logwiki.specialsurveyservice.api.service.account.AccountService;
import com.logwiki.specialsurveyservice.api.service.question.request.QuestionAnswerCreateServiceRequest;
import com.logwiki.specialsurveyservice.api.service.question.response.QuestionAnswerResponse;
import com.logwiki.specialsurveyservice.api.service.surveyresult.SurveyResultService;
import com.logwiki.specialsurveyservice.domain.account.Account;
import com.logwiki.specialsurveyservice.domain.accountcode.AccountCodeRepository;
import com.logwiki.specialsurveyservice.domain.accountcode.AccountCodeType;
import com.logwiki.specialsurveyservice.domain.question.Question;
import com.logwiki.specialsurveyservice.domain.question.QuestionRepository;
import com.logwiki.specialsurveyservice.domain.questionanswer.QuestionAnswer;
import com.logwiki.specialsurveyservice.domain.questionanswer.QuestionAnswerRepository;
import com.logwiki.specialsurveyservice.domain.surveytarget.SurveyTarget;
import com.logwiki.specialsurveyservice.domain.surveytarget.SurveyTargetRepository;
import com.logwiki.specialsurveyservice.exception.BaseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionAnswerService {

    private final QuestionAnswerRepository questionAnswerRepository;
    private final QuestionRepository questionRepository;
    private final AccountService accountService;
    private final SurveyResultService surveyResultService;
    private final SurveyTargetRepository surveyTargetRepository;
    private final AccountCodeRepository accountCodeRepository;

    @Transactional
    public List<QuestionAnswerResponse> addQuestionAnswer(
            LocalDateTime writeDate,
            Long surveyId,
            List<QuestionAnswerCreateServiceRequest> dto) {
        Account account = accountService.getCurrentAccountBySecurity();
        List<Question> questions = findQuestionsBySurveyId(surveyId);

        checkIsTarget(account, surveyId);
        checkAnsweredAllQuestions(questions, dto);

        surveyResultService.addSubmitResult(surveyId, writeDate);
        return saveQuestionAnswer(writeDate, account, questions, dto);
    }

    private List<Question> findQuestionsBySurveyId(Long surveyId) {
        return questionRepository.findBySurveyId(surveyId).orElseThrow(
                () -> new BaseException("없는 설문입니다.", 3005));
    }

    private void checkIsTarget(Account account, Long surveyId) {
        List<AccountCodeType> accountGenderAgeType = new ArrayList<>();
        accountGenderAgeType.add(account.getGender());
        accountGenderAgeType.add(account.getAge());

        List<SurveyTarget> surveyTargets = surveyTargetRepository.findSurveyTargetBySurvey_Id(surveyId);
        List<AccountCodeType> accountCodeTypes = new ArrayList<>();
        for (SurveyTarget surveyTarget : surveyTargets) {
            accountCodeTypes.add(surveyTarget.getAccountCode().getType());
        }
        for (AccountCodeType accountCodeType : accountGenderAgeType) {
            if (accountCodeTypes.contains(accountCodeType)) {
                continue;
            }
            throw new BaseException("설문 대상자가 아닙니다.", 3003);
        }
    }

    private void checkAnsweredAllQuestions(List<Question> questions, List<QuestionAnswerCreateServiceRequest> dto) {
        if (questions.size() > dto.size()) {
            throw new BaseException("모든 문항에 답변을 해야합니다.", 3001);
        }
    }

    private List<QuestionAnswerResponse> saveQuestionAnswer(
            LocalDateTime writeDate,
            Account account,
            List<Question> questions,
            List<QuestionAnswerCreateServiceRequest> dto) {

        List<QuestionAnswerResponse> result = new ArrayList<>();
        for (Question question : questions) {
            boolean notFoundQuestion = true;
            for (QuestionAnswerCreateServiceRequest answer : dto) {
                if (question.getId().equals(answer.getQuestionId())) {
                    QuestionAnswer questionAnswer = answer.toEntity(question, account);
                    questionAnswer.setWriteDate(writeDate);
                    questionAnswerRepository.save(questionAnswer);
                    result.add(QuestionAnswerResponse.from(questionAnswer));
                    notFoundQuestion = false;
                }
            }
            if (notFoundQuestion) {
                throw new BaseException("없는 문항에 답변을 할 수 없습니다.", 3002);
            }
        }
        return result;
    }
}
