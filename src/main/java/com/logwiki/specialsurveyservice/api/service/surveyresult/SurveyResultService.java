package com.logwiki.specialsurveyservice.api.service.surveyresult;

import com.logwiki.specialsurveyservice.api.controller.surveyresult.response.SurveyResultResponse;
import com.logwiki.specialsurveyservice.api.service.account.AccountService;
import com.logwiki.specialsurveyservice.api.service.surveyresult.response.ResultPageResponse;
import com.logwiki.specialsurveyservice.domain.account.Account;
import com.logwiki.specialsurveyservice.domain.accountsurvey.AccountSurvey;
import com.logwiki.specialsurveyservice.domain.giveaway.Giveaway;
import com.logwiki.specialsurveyservice.domain.survey.Survey;
import com.logwiki.specialsurveyservice.domain.survey.SurveyRepository;
import com.logwiki.specialsurveyservice.domain.surveycategory.SurveyCategoryType;
import com.logwiki.specialsurveyservice.domain.surveygiveaway.SurveyGiveaway;
import com.logwiki.specialsurveyservice.domain.surveyresult.SurveyResult;
import com.logwiki.specialsurveyservice.domain.surveyresult.SurveyResultRepository;
import com.logwiki.specialsurveyservice.domain.targetnumber.TargetNumber;
import com.logwiki.specialsurveyservice.domain.targetnumber.TargetNumberRepository;
import com.logwiki.specialsurveyservice.exception.BaseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SurveyResultService {

    private final SurveyResultRepository surveyResultRepository;
    private final SurveyRepository surveyRepository;
    private final AccountService accountService;
    private final TargetNumberRepository targetNumberRepository;

    private final static boolean DEFAULT_WIN = false;
    private final static double DEFAULT_PROBABILITY = 0;

    public SurveyResult addSubmitResult(Long surveyId, LocalDateTime answerDateTime) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new BaseException("설문조사 PK가 올바르지 않습니다.", 3010));
        Account account = accountService.getCurrentAccountBySecurity();
        SurveyResult checkSurveyResult = surveyResultRepository.findSurveyResultBySurvey_IdAndAccount_Id(survey.getId(), account.getId());

        int submitOrder = createSubmitOrderIn(surveyId);

        if (survey.isClosed()) {
            throw new BaseException("마감된 설문입니다.", 3011);
        }
        if (checkSurveyResult != null) {
            throw new BaseException("이미 응답한 설문입니다.", 3012);
        }

        SurveyResult surveyResult = SurveyResult.create(DEFAULT_WIN, answerDateTime, submitOrder, survey,
                account);

        if (survey.getTargetNumbers().stream()
                .anyMatch(targetNumber -> targetNumber.getNumber() == submitOrder)) {
            if (survey.getSurveyCategory().getType().equals(SurveyCategoryType.INSTANT_WIN)) {
                account.increaseWinningGiveawayCount();
            }
            surveyResult.winSurvey();
        }

        account.increaseResponseSurveyCount();
        log.info("당첨여부 [{}][{}][{}]", survey.getId(), account.getEmail(), surveyResult.isWin());

        surveyResultRepository.save(surveyResult);

        survey.addHeadCount();
        survey.addSurveyResult(surveyResult); // TODO 트랜잭션 때문에 값이 바로 들어가지 않는건가요? SurveyServiceTest의 getSurveyAnswers()와 연관된 문제

        AccountSurvey accountSurvey = AccountSurvey
                .builder()
                .survey(survey)
                .account(account)
                .build();
        account.addAccountSurvey(accountSurvey);

        return surveyResult;
    }

    public int createSubmitOrderIn(Long surveyId) {
        return surveyResultRepository.findSubmitCountBy(surveyId) + 1;
    }

    public ResultPageResponse getSurveyResult(Long surveyId) {
        Account account = accountService.getCurrentAccountBySecurity();

        Survey survey = surveyRepository.findById(surveyId).orElseThrow(() ->
                new BaseException("없는 설문입니다.", 3005));

        if (survey.getSurveyCategory().getType().equals(SurveyCategoryType.NORMAL)) {
            throw new BaseException("즉시 당첨만 확인이 가능합니다.", 3015);
        }

        SurveyResult surveyResult = surveyResultRepository.findSurveyResultBySurvey_IdAndAccount_Id(surveyId, account.getId());
        if (surveyResult == null) {
            throw new BaseException("미응답 설문입니다.", 3014);
        }

        surveyResult.checkResult();

        TargetNumber targetNumber = targetNumberRepository.findTargetNumberByNumberAndSurvey_Id(surveyResult.getSubmitOrder(), surveyId);

        if (targetNumber == null) {
            surveyResult.checkResult();
            return ResultPageResponse.builder()
                    .isWin(surveyResult.isWin())
                    .build();
        }

        Giveaway giveaway = targetNumber.getGiveaway();
        List<SurveyGiveaway> surveyGiveaway = survey.getSurveyGiveaways();
        double probability = DEFAULT_PROBABILITY;

        for (SurveyGiveaway value : surveyGiveaway) {
            if (value.getGiveaway().getId().equals(giveaway.getId())) {
                probability = (double) survey.getHeadCount() / (double) value.getCount();
                break;
            }
        }

        return ResultPageResponse.builder()
                .isWin(surveyResult.isWin())
                .giveawayType(giveaway.getGiveawayType())
                .giveawayName(giveaway.getName())
                .probability(probability)
                .build();
    }

    public SurveyResultResponse patchSurveyResult(Long surveyId) {
        Account account = accountService.getCurrentAccountBySecurity();
        Survey survey = surveyRepository.findById(surveyId).orElseThrow(() ->
                new BaseException("없는 설문입니다.", 3005));

        if (survey.getSurveyCategory().getType().equals(SurveyCategoryType.INSTANT_WIN)) {
            throw new BaseException("마이페이지 당첨 결과는 노말 타입만 확인이 가능합니다.", 3019);
        }

        if (survey.isClosed()) {

            SurveyResult surveyResult = surveyResultRepository.findSurveyResultBySurvey_IdAndAccount_Id(surveyId, account.getId());

            if (surveyResult == null) {
                throw new BaseException("미응답 설문입니다.", 3014);
            }

            surveyResult.checkResult();
            if (surveyResult.isWin()) {
                account.increaseWinningGiveawayCount();
            }
            return SurveyResultResponse.from(surveyResult);

        }

        throw new BaseException("마감되지 않은 설문은 결과를 확인할수 없습니다.", 3016);
    }
}
