package com.logwiki.specialsurveyservice.api.service.question;


import com.logwiki.specialsurveyservice.api.service.account.AccountService;
import com.logwiki.specialsurveyservice.api.service.question.request.QuestionAnswerCreateServiceRequest;
import com.logwiki.specialsurveyservice.api.service.question.response.QuestionAnswerResponse;
import com.logwiki.specialsurveyservice.api.service.surveyresult.SurveyResultService;
import com.logwiki.specialsurveyservice.domain.account.Account;
import com.logwiki.specialsurveyservice.domain.accountcode.AccountCodeType;
import com.logwiki.specialsurveyservice.domain.multiplechoice.MultipleChoice;
import com.logwiki.specialsurveyservice.domain.question.Question;
import com.logwiki.specialsurveyservice.domain.question.QuestionRepository;
import com.logwiki.specialsurveyservice.domain.questionanswer.QuestionAnswer;
import com.logwiki.specialsurveyservice.domain.questionanswer.QuestionAnswerRepository;
import com.logwiki.specialsurveyservice.domain.questioncategory.QuestionCategoryType;
import com.logwiki.specialsurveyservice.domain.surveyresult.SurveyResult;
import com.logwiki.specialsurveyservice.domain.surveytarget.SurveyTarget;
import com.logwiki.specialsurveyservice.domain.surveytarget.SurveyTargetRepository;
import com.logwiki.specialsurveyservice.exception.BaseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QuestionAnswerService {

    private final QuestionAnswerRepository questionAnswerRepository;
    private final QuestionRepository questionRepository;
    private final AccountService accountService;
    private final SurveyResultService surveyResultService;
    private final SurveyTargetRepository surveyTargetRepository;

    @Transactional
    public List<QuestionAnswerResponse> addQuestionAnswer(
            LocalDateTime answerDateTime,
            Long surveyId,
            List<QuestionAnswerCreateServiceRequest> dto) {
        Account account = accountService.getCurrentAccountBySecurity();
        List<Question> questions = findQuestionsBySurveyId(surveyId);

        checkIsTarget(account, surveyId);
        checkAnsweredAllQuestions(questions, dto);

        SurveyResult surveyResult = surveyResultService.addSubmitResult(surveyId, answerDateTime);
        surveyResultService.sendResultToSSE(surveyId, surveyResult, surveyResult.getSubmitOrder());
        return saveQuestionAnswer(answerDateTime, account, questions, dto);
    }

    private List<Question> findQuestionsBySurveyId(Long surveyId) {
        List<Question> questions = questionRepository.findBySurveyId(surveyId);
        if (questions.size() == 0) {
            throw new BaseException("없는 설문입니다.", 3005);
        }
        return questions;
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
        int minimumQuestionAnswerCnt = questions.size();
        Map<Long, Boolean> checkLinkNumber = new HashMap<>();
        for (Question question : questions) {
            if (question.isEssential()) {
                minimumQuestionAnswerCnt -= 1;
                continue;
            }
            if (question.getType() == QuestionCategoryType.MULTIPLE_CHOICE ||
                    question.getType() == QuestionCategoryType.CHECK_BOX) {
                minimumQuestionAnswerCnt -= getMinusAnswerCnt(question.getMultipleChoice(), checkLinkNumber);
            }
        }
        if (minimumQuestionAnswerCnt > dto.size()) {
            throw new BaseException("모든 문항에 답변을 해야합니다.", 3001);
        }
    }

    private int getMinusAnswerCnt(List<MultipleChoice> multipleChoices, Map<Long, Boolean> checkLinkNumber) {
        final int DEFAULT_ANSWER_CNT = 1;
        int curLinkQuestionCnt = 0;
        for (MultipleChoice multipleChoice : multipleChoices) {
            Long curLinkNumber = multipleChoice.getLinkNumber();
            if (curLinkNumber != 0 &&
                    checkLinkNumber.getOrDefault(curLinkNumber, true)) {
                curLinkQuestionCnt += 1;
                checkLinkNumber.put(curLinkNumber, false);
            }
        }
        return Math.max(curLinkQuestionCnt - DEFAULT_ANSWER_CNT, 0);
    }

    private List<QuestionAnswerResponse> saveQuestionAnswer(
            LocalDateTime answerDateTime,
            Account account,
            List<Question> questions,
            List<QuestionAnswerCreateServiceRequest> dto) {

        List<QuestionAnswerResponse> result = new ArrayList<>();

        for (QuestionAnswerCreateServiceRequest answer : dto) {
            boolean notFoundQuestion = true;
            for (Question question : questions) {
                if (question.getId().equals(answer.getQuestionId())) {
                    QuestionAnswer questionAnswer = answer.toEntity(question, account);
                    questionAnswer.setAnswerDateTime(answerDateTime);
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
