package com.logwiki.specialsurveyservice.api.service.question;


import com.logwiki.specialsurveyservice.api.controller.sse.response.SurveyAnswerResponse;
import com.logwiki.specialsurveyservice.api.service.giveaway.GiveawayService;
import com.logwiki.specialsurveyservice.api.service.question.request.QuestionAnswerCreateServiceRequest;
import com.logwiki.specialsurveyservice.api.service.question.response.QuestionAnswerResponse;
import com.logwiki.specialsurveyservice.api.service.sse.SseConnectService;
import com.logwiki.specialsurveyservice.api.service.survey.SurveyService;
import com.logwiki.specialsurveyservice.api.service.surveyresult.SurveyResultService;
import com.logwiki.specialsurveyservice.api.service.surveyresult.response.SurveyResultResponse;
import com.logwiki.specialsurveyservice.domain.account.Account;
import com.logwiki.specialsurveyservice.domain.account.AccountRepository;
import com.logwiki.specialsurveyservice.domain.accountcode.AccountCodeRepository;
import com.logwiki.specialsurveyservice.domain.accountcode.AccountCodeType;
import com.logwiki.specialsurveyservice.domain.question.Question;
import com.logwiki.specialsurveyservice.domain.question.QuestionRepository;
import com.logwiki.specialsurveyservice.domain.questionanswer.QuestionAnswer;
import com.logwiki.specialsurveyservice.domain.questionanswer.QuestionAnswerRepository;
import com.logwiki.specialsurveyservice.domain.survey.Survey;
import com.logwiki.specialsurveyservice.domain.survey.SurveyRepository;
import com.logwiki.specialsurveyservice.domain.surveycategory.SurveyCategoryType;
import com.logwiki.specialsurveyservice.domain.surveytarget.SurveyTarget;
import com.logwiki.specialsurveyservice.domain.surveytarget.SurveyTargetRepository;
import com.logwiki.specialsurveyservice.domain.targetnumber.TargetNumber;
import com.logwiki.specialsurveyservice.domain.targetnumber.TargetNumberRepository;
import com.logwiki.specialsurveyservice.exception.BaseException;
import jakarta.transaction.Transactional;
import java.util.Optional;
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
    private final AccountRepository accountRepository;
    private final SurveyService surveyService;
    private final SurveyResultService surveyResultService;
    private final SseConnectService sseConnectService;
    private final SurveyRepository surveyRepository;
    private final TargetNumberRepository targetNumberRepository;
    private final GiveawayService giveawayService;
    private final SurveyTargetRepository surveyTargetRepository;
    private final AccountCodeRepository accountCodeRepository;

    @Transactional
    public List<QuestionAnswerResponse> addQuestionAnswer(
            LocalDateTime writeDate,
            Long surveyId,
            String userEmail,
            List<QuestionAnswerCreateServiceRequest> dto) {
        Account account = findAccountByEmail(userEmail);
        List<Question> questions = findQuestionsBySurveyId(surveyId);
        
        checkIsTarget(account, surveyId);
        checkAnsweredAllQuestions(questions, dto);

        SurveyResultResponse surveyResultResponse = surveyResultService.addSubmitResult(surveyId, userEmail, writeDate);

        Survey targetSurvey = surveyRepository.getReferenceById(surveyId);

        if(targetSurvey.getSurveyCategory().getType().equals(SurveyCategoryType.NORMAL)) {
            double percentage = surveyService.getSurveyWinRate(targetSurvey);
            sseConnectService.refreshSurveyProbability(surveyId, String.valueOf(percentage));
        }

        String giveawayName;
        Boolean isWin = false;
        if(targetSurvey.getSurveyCategory().getType().equals(SurveyCategoryType.INSTANT_WIN)){
            isWin = surveyResultResponse.getIsWin();
        }
        Optional<TargetNumber> targetNumber = targetNumberRepository.findFirstBySurveyAndNumber(
                targetSurvey,
                surveyResultResponse.getSubmitOrder());
        if (targetNumber.isPresent()) {
            giveawayName = targetNumber.get().getGiveaway().getName();
        } else {
            giveawayName = giveawayService.getRepGiveaway(targetSurvey).getName();
        }
        sseConnectService.refreshSurveyFinisher(surveyId, SurveyAnswerResponse.builder()
                .answerTime(writeDate)
                .giveAwayName(giveawayName)
                .isWin(isWin)
                .name(account.getName()).build());
        return saveQuestionAnswer(writeDate, account, questions, dto);
    }

    private Account findAccountByEmail(String userEmail) {
        return accountRepository.findOneWithAuthoritiesByEmail(userEmail)
                .orElseThrow(() -> new BaseException("존재하지 않는 유저입니다.", 2000));
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
