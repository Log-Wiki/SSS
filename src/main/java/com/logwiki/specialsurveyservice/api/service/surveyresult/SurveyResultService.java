package com.logwiki.specialsurveyservice.api.service.surveyresult;

import com.logwiki.specialsurveyservice.api.controller.surveyresult.response.SurveyResultResponse;
import com.logwiki.specialsurveyservice.api.service.account.AccountService;
import com.logwiki.specialsurveyservice.api.service.surveyresult.response.MyGiveawayResponse;
import com.logwiki.specialsurveyservice.api.service.surveyresult.response.ResultPageResponse;
import com.logwiki.specialsurveyservice.domain.account.Account;
import com.logwiki.specialsurveyservice.domain.giveaway.Giveaway;
import com.logwiki.specialsurveyservice.domain.survey.Survey;
import com.logwiki.specialsurveyservice.domain.survey.SurveyRepository;
import com.logwiki.specialsurveyservice.domain.surveycategory.SurveyCategoryType;
import com.logwiki.specialsurveyservice.domain.surveyresult.SurveyResult;
import com.logwiki.specialsurveyservice.domain.surveyresult.SurveyResultRepository;
import com.logwiki.specialsurveyservice.domain.targetnumber.TargetNumber;
import com.logwiki.specialsurveyservice.domain.targetnumber.TargetNumberRepository;
import com.logwiki.specialsurveyservice.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SurveyResultService {

    private final SurveyResultRepository surveyResultRepository;
    private final SurveyRepository surveyRepository;
    private final AccountService accountService;
    private final TargetNumberRepository targetNumberRepository;
    private final static boolean DEFAULT_WIN = false;

    public void addSubmitResult(Long surveyId, LocalDateTime answerDateTime) {
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

        if (survey.getSurveyCategory().getType().equals(SurveyCategoryType.INSTANT_WIN)) {
            if (survey.getTargetNumbers().stream()
                    .anyMatch(targetNumber -> targetNumber.getNumber() == submitOrder)) {
                account.increaseWinningGiveawayCount();
                surveyResult.winSurvey();
            }
        }

        account.increaseResponseSurveyCount();

        surveyResultRepository.save(surveyResult);


        survey.addHeadCount();
    }

    public int createSubmitOrderIn(Long surveyId) {
        return surveyResultRepository.findSubmitCountBy(surveyId) + 1;
    }

    public List<MyGiveawayResponse> getMyGiveaways() {
        Account account = accountService.getCurrentAccountBySecurity();
        List<SurveyResult> surveyResults = surveyResultRepository.findSurveyResultsByAccount_Id(
                account.getId());

        List<SurveyResult> winSurveyResults = surveyResults.stream()
                .filter(SurveyResult::isWin)
                .toList();

        return winSurveyResults.stream()
                .map(surveyResult -> MyGiveawayResponse.of(
                        surveyResult,
                        targetNumberRepository.findTargetNumberByNumberAndSurvey_Id(
                                surveyResult.getSubmitOrder(),
                                surveyResult.getSurvey().getId()).getGiveaway(),
                        accountService.getUserNameById(surveyResult.getSurvey().getWriter())))
                .toList();
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

        TargetNumber targetNumber = targetNumberRepository.findTargetNumberByNumberAndSurvey_Id(surveyResult.getSubmitOrder(), surveyId);

        if (targetNumber == null) {
            return ResultPageResponse.builder()
                    .isWin(false)
                    .build();
        }
        Giveaway giveaway = targetNumber.getGiveaway();
        return ResultPageResponse.builder()
                .isWin(true)
                .giveawayType(giveaway.getGiveawayType())
                .giveawayName(giveaway.getName())
                .build();
    }

    public SurveyResultResponse patchSurveyResult(Long surveyId) {
        Account account = accountService.getCurrentAccountBySecurity();
        Survey survey = surveyRepository.findById(surveyId).orElseThrow(() ->
                new BaseException("없는 설문입니다.", 3005));
        if (LocalDateTime.now().isBefore(survey.getEndTime())) {
            throw new BaseException("마감되지 않은 설문은 결과를 확인할수 없습니다.", 3016);
        }

        SurveyResult surveyResult = surveyResultRepository.findSurveyResultBySurvey_IdAndAccount_Id(surveyId, account.getId());

        if (surveyResult == null) {
            throw new BaseException("미응답 설문입니다.", 3014);
        }

        surveyResult.checkResult();
        return SurveyResultResponse.from(surveyResult);
    }
}
