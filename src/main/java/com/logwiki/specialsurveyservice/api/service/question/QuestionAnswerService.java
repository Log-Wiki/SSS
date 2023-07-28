package com.logwiki.specialsurveyservice.api.service.question;


import com.logwiki.specialsurveyservice.api.service.question.request.QuestionAnswerCreateServiceRequest;
import com.logwiki.specialsurveyservice.api.service.question.response.QuestionAnswerResponse;
import com.logwiki.specialsurveyservice.api.service.surveyresult.SurveyResultService;
import com.logwiki.specialsurveyservice.domain.account.Account;
import com.logwiki.specialsurveyservice.domain.account.AccountRepository;
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
    private final SurveyTargetRepository surveyTargetRepository;
    private final AccountCodeRepository accountCodeRepository;

    @Transactional
    public List<QuestionAnswerResponse> addQuestionAnswer(
            LocalDateTime writeDate,
            Long surveyId,
            String userEmail,
            List<QuestionAnswerCreateServiceRequest> dto) {

        Account account = accountRepository.findOneWithAuthoritiesByEmail(userEmail)
                .orElseThrow(() -> new BaseException("없는 유저입니다.", 1001));
        List<Question> questions = questionRepository.findBySurveyId(surveyId).orElseThrow(
                () -> new BaseException("없는 설문입니다.", 2005));

        List<AccountCodeType> accountGenderAgeType = new ArrayList<>();
        accountGenderAgeType.add(account.getGender());
        accountGenderAgeType.add(account.getAge());

        isTarget(accountGenderAgeType, surveyId);
        List<QuestionAnswerResponse> result = new ArrayList<>();

        if (questions.size() != dto.size()) {
            throw new BaseException("모든 문항에 답변을 해야합니다.", 3007);
        }

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
                throw new BaseException("없는 문항에 답변을 할 수 없습니다.", 3000);
            }
        }

        surveyResultService.addSubmitResult(surveyId, userEmail, writeDate);

        return result;
    }

    private void isTarget(List<AccountCodeType> accountGenderAgeType, Long surveyId) {
        List<SurveyTarget> surveyTargets = surveyTargetRepository.findSurveyTargetBySurvey_Id(surveyId);
        List<AccountCodeType> accountCodeTypes = new ArrayList<>();
        for (SurveyTarget surveyTarget : surveyTargets) {
            accountCodeTypes.add(surveyTarget.getAccountCode().getType());
        }
        for (AccountCodeType accountCodeType : accountGenderAgeType) {
            if (accountCodeTypes.contains(accountCodeType)) {
                continue;
            }
            throw new BaseException("설문 대상자가 아닙니다.", 3002);
        }
    }
}
