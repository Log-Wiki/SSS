package com.logwiki.specialsurveyservice.api.service.surveyresult;

import com.logwiki.specialsurveyservice.api.service.surveyresult.response.SurveyResultResponse;
import com.logwiki.specialsurveyservice.domain.account.Account;
import com.logwiki.specialsurveyservice.domain.account.AccountRepository;
import com.logwiki.specialsurveyservice.domain.survey.Survey;
import com.logwiki.specialsurveyservice.domain.survey.SurveyRepository;
import com.logwiki.specialsurveyservice.domain.surveyresult.SurveyResult;
import com.logwiki.specialsurveyservice.domain.surveyresult.SurveyResultRepository;
import com.logwiki.specialsurveyservice.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SurveyResultService {

    private final SurveyResultRepository surveyResultRepository;
    private final SurveyRepository surveyRepository;
    private final AccountRepository accountRepository;

    public SurveyResultResponse addSubmitResult(Long surveyId, String userEmail, LocalDateTime writeDateTime) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new BaseException("설문조사 PK가 올바르지 않습니다.", 3010));
        Account account = accountRepository.findOneWithAuthoritiesByEmail(userEmail)
                .orElseThrow(() -> new BaseException("존재하지 않는 유저입니다.", 2000));
        SurveyResult checkSurveyResult = surveyResultRepository.findSurveyResultByAccount_Id(account.getId());

        int submitOrder = createSubmitOrderIn(surveyId);

        if(survey.isClosed()){
            throw new BaseException("마감된 설문입니다.", 3011);
        }
        if (checkSurveyResult != null) {
            throw new BaseException("이미 응답한 설문입니다.", 3012);
        }

        boolean isWin = survey.getTargetNumbers().stream()
                .anyMatch(targetNumber -> targetNumber.getNumber() == submitOrder);
        if (isWin) {
            account.increaseWinningGiveawayCount();
        }

        account.increaseResponseSurveyCount();
        SurveyResult surveyResult = surveyResultRepository.save(SurveyResult.create(isWin, writeDateTime, submitOrder, survey,
                account));

        survey.addHeadCount();
        
        return SurveyResultResponse.of(surveyResult);

    }

    public int createSubmitOrderIn(Long surveyId) {
        return surveyResultRepository.findSubmitCountBy(surveyId) + 1;
    }
}
