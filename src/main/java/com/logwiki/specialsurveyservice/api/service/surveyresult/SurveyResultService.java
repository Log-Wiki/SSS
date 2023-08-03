package com.logwiki.specialsurveyservice.api.service.surveyresult;

import com.logwiki.specialsurveyservice.api.service.account.AccountService;
import com.logwiki.specialsurveyservice.api.service.survey.response.SurveyResponse;
import com.logwiki.specialsurveyservice.api.service.surveyresult.response.MyGiveawayResponse;
import com.logwiki.specialsurveyservice.domain.account.Account;
import com.logwiki.specialsurveyservice.domain.survey.Survey;
import com.logwiki.specialsurveyservice.domain.survey.SurveyRepository;
import com.logwiki.specialsurveyservice.domain.surveycategory.SurveyCategoryType;
import com.logwiki.specialsurveyservice.domain.surveyresult.SurveyResult;
import com.logwiki.specialsurveyservice.domain.surveyresult.SurveyResultRepository;
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

    public void addSubmitResult(Long surveyId, LocalDateTime writeDateTime) {
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

        boolean isWin = false;
        if (survey.getSurveyCategory().getType().equals(SurveyCategoryType.INSTANT_WIN)) {
            isWin = survey.getTargetNumbers().stream()
                    .anyMatch(targetNumber -> targetNumber.getNumber() == submitOrder);
            if (isWin) {
                account.increaseWinningGiveawayCount();
            }
        }

        account.increaseResponseSurveyCount();
        surveyResultRepository.save(SurveyResult.create(isWin, writeDateTime, submitOrder, survey,
                account));

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
                .map(surveyResult -> MyGiveawayResponse.of(surveyResult,
                        targetNumberRepository.findTargetNumberByNumberAndSurvey_Id(
                                surveyResult.getSubmitOrder(),
                                surveyResult.getSurvey().getId()).getGiveaway()))
                .toList();
    }

    public List<SurveyResponse> getAnsweredSurveys() {
        Account account = accountService.getCurrentAccountBySecurity();
        List<SurveyResult> surveyResults = surveyResultRepository.findSurveyResultsByAccount_Id(
                account.getId());

        List<Survey> surveys = surveyResults.stream()
                .map(SurveyResult::getSurvey)
                .toList();

        return surveys.stream()
                .map(SurveyResponse::from)
                .toList();
    }
}
