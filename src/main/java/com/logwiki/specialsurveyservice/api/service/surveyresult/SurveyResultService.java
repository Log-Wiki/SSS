package com.logwiki.specialsurveyservice.api.service.surveyresult;

import com.logwiki.specialsurveyservice.api.service.surveyresult.response.SurveyResultResponse;
import com.logwiki.specialsurveyservice.domain.account.Account;
import com.logwiki.specialsurveyservice.domain.account.AccountRepository;
import com.logwiki.specialsurveyservice.domain.survey.Survey;
import com.logwiki.specialsurveyservice.domain.survey.SurveyRepository;
import com.logwiki.specialsurveyservice.domain.surveyresult.SurveyResult;
import com.logwiki.specialsurveyservice.domain.surveyresult.SurveyResultRepository;
import com.logwiki.specialsurveyservice.exception.BaseException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SurveyResultService {

    private final SurveyResultRepository surveyResultRepository;
    private final SurveyRepository surveyRepository;
    private final AccountRepository accountRepository;

    public SurveyResultResponse addSubmitResult(Long surveyId, String userEmail, LocalDateTime writeDateTime) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new BaseException("설문조사 PK가 올바르지 않습니다.", 1000));
        Account account = accountRepository.findOneWithAuthoritiesByEmail(userEmail)
                .orElseThrow(() -> new BaseException("없는 유저입니다.", 1001));
        SurveyResult checkSurveyResult = surveyResultRepository.findSurveyResultByAccount_Id(account.getId());

        int submitOrder = createSubmitOrderIn(surveyId);

        if (survey.getClosedHeadCount() < submitOrder || writeDateTime.isAfter(survey.getEndTime())) {
            throw new BaseException("마감된 설문입니다.", 3002);
        }
        if (checkSurveyResult != null) {
            throw new BaseException("이미 응답한 설문입니다.", 3004);
        }

        boolean isWin = survey.getTargetNumbers().stream()
                .anyMatch(targetNumber -> targetNumber.getNumber() == submitOrder);
        if (isWin) {
            account.increaseWinningGiveawayCount();
        }

        account.increaseResponseSurveyCount();
        SurveyResult surveyResult = surveyResultRepository.save(SurveyResult.create(isWin, writeDateTime, submitOrder, survey,
                account));
        return SurveyResultResponse.of(surveyResult);

    }

    public int createSubmitOrderIn(Long surveyId) {
        return surveyResultRepository.findSubmitCountBy(surveyId) + 1;
    }
}
