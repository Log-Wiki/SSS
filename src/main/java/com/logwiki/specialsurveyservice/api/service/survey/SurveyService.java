package com.logwiki.specialsurveyservice.api.service.survey;


import com.logwiki.specialsurveyservice.api.controller.sse.response.SurveyResponseResult;
import com.logwiki.specialsurveyservice.api.service.giveaway.GiveawayService;
import com.logwiki.specialsurveyservice.api.service.survey.request.GiveawayAssignServiceRequest;
import com.logwiki.specialsurveyservice.api.service.survey.request.SurveyCreateServiceRequest;
import com.logwiki.specialsurveyservice.api.service.survey.response.SurveyDetailgetServiceResponse;
import com.logwiki.specialsurveyservice.api.service.survey.response.SurveyResponse;
import com.logwiki.specialsurveyservice.api.service.targetnumber.TargetNumberService;
import com.logwiki.specialsurveyservice.api.service.targetnumber.request.TargetNumberCreateServiceRequest;
import com.logwiki.specialsurveyservice.domain.account.Account;
import com.logwiki.specialsurveyservice.domain.account.AccountRepository;
import com.logwiki.specialsurveyservice.domain.accountcode.AccountCode;
import com.logwiki.specialsurveyservice.domain.accountcode.AccountCodeRepository;
import com.logwiki.specialsurveyservice.domain.accountcode.AccountCodeType;
import com.logwiki.specialsurveyservice.domain.giveaway.Giveaway;
import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayRepository;
import com.logwiki.specialsurveyservice.domain.survey.Survey;
import com.logwiki.specialsurveyservice.domain.survey.SurveyRepository;
import com.logwiki.specialsurveyservice.domain.surveycategory.SurveyCategoryType;
import com.logwiki.specialsurveyservice.domain.surveygiveaway.SurveyGiveaway;
import com.logwiki.specialsurveyservice.domain.surveyresult.SurveyResult;
import com.logwiki.specialsurveyservice.domain.surveytarget.SurveyTarget;
import com.logwiki.specialsurveyservice.domain.targetnumber.TargetNumber;
import com.logwiki.specialsurveyservice.domain.targetnumber.TargetNumberRepository;
import com.logwiki.specialsurveyservice.exception.BaseException;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SurveyService {

    private final SurveyRepository surveyRepository;

    private final AccountRepository accountRepository;

    private final GiveawayRepository giveawayRepository;

    private final TargetNumberService targetNumberService;

    private final AccountCodeRepository accountCodeRepository;

    private final GiveawayService giveawayService;

    private final TargetNumberRepository targetNumberRepository;


    public SurveyResponse addSurvey(String userEmail, SurveyCreateServiceRequest dto) {
        Account account = accountRepository.findOneWithAuthoritiesByEmail(userEmail)
                .orElseThrow(() -> new BaseException("존재하지 않는 유저입니다.", 2000));

        Survey survey = dto.toEntity(account.getId());

        for (AccountCodeType accountCodeType : dto.getSurveyTarget()) {
            AccountCode accountCode = accountCodeRepository.findAccountCodeByType(accountCodeType).orElseThrow(
                    () -> new BaseException("없는 나이,성별 코드 입니다.", 3007)
            );

            SurveyTarget surveyTarget = SurveyTarget.builder()
                    .survey(survey)
                    .accountCode(accountCode)
                    .build();
            survey.addSurveyTarget(surveyTarget);
        }

        List<GiveawayAssignServiceRequest> giveawayAssignServiceRequests = dto.getGiveaways();
        List<SurveyGiveaway> surveyGiveaways = getSurveyGiveaways(survey,
                giveawayAssignServiceRequests);
        survey.addSurveyGiveaways(surveyGiveaways);

        TargetNumberCreateServiceRequest targetNumberCreateServiceRequest = TargetNumberCreateServiceRequest.create(
                dto.getClosedHeadCount(), giveawayAssignServiceRequests, survey);
        List<TargetNumber> targetNumbers = targetNumberService.createTargetNumbers(
                targetNumberCreateServiceRequest);
        survey.addTargetNumbers(targetNumbers);
        surveyRepository.save(survey);
        return SurveyResponse.from(survey);
    }

    private List<SurveyGiveaway> getSurveyGiveaways(Survey survey,
            List<GiveawayAssignServiceRequest> giveawayAssignServiceRequests) {

        return giveawayAssignServiceRequests.stream()
                .map(giveaway -> SurveyGiveaway.create(giveaway.getCount(), survey,
                        giveawayRepository.findById(giveaway.getId())
                                .orElseThrow(
                                        () -> new BaseException("등록되어 있지 않은 당첨 상품을 포함하고 있습니다.",
                                                5003))))
                .collect(Collectors.toList());
    }


    public int getSurveyGiveawayCount(Survey survey) {
        List<SurveyGiveaway> surveyGiveaways = survey.getSurveyGiveaways();
        int giveawayNum = 0;
        for (SurveyGiveaway sg : surveyGiveaways) {
            giveawayNum += sg.getCount();
        }
        return  giveawayNum;
    }

    public double getSurveyWinRate(Survey survey) {
        int giveawayNum = this.getSurveyGiveawayCount(survey);
        double winRate;
        if(survey.getSurveyCategory().getType().equals(SurveyCategoryType.NORMAL)) {
            if (giveawayNum >= survey.getHeadCount()) {
                winRate = 100.0;
            } else {
                winRate = (double)giveawayNum / survey.getHeadCount();
            }
        }
        else {
            winRate = (double)giveawayNum / survey.getClosedHeadCount();
        }

        return  winRate;
    }

    public SurveyDetailgetServiceResponse getSurveyDetail(Long surveyId) {
        Optional<Survey> targetSurveyOptional =  surveyRepository.findById(surveyId);
        if(targetSurveyOptional.isEmpty()) {
            throw new BaseException("없는 설문입니다." , 3005);
        }
        Survey targetSurvey = targetSurveyOptional.get();
        List<SurveyResponseResult> surveyResponseResults = new ArrayList<>();

        String repGiveawayName = giveawayService.getRepGiveaway(targetSurvey).getName();
        if(targetSurvey.getSurveyResults() != null) {
            for (SurveyResult surveyResult : targetSurvey.getSurveyResults()) {
                String giveawayName;
                boolean isWin = false;
                Optional<TargetNumber> tn = targetNumberRepository.findFirstBySurveyAndNumber(
                        targetSurvey, surveyResult.getSubmitOrder());
                if (tn.isPresent()) {
                    isWin = true;
                    giveawayName = tn.get().getGiveaway().getName();
                } else {
                    giveawayName = repGiveawayName;
                }

                surveyResponseResults.add(new SurveyResponseResult(
                                surveyResult.getEndTime()
                                , surveyResult.getAccount().getName()
                                , giveawayName
                                , isWin
                        )
                );
            }
        }

        double winRate = this.getSurveyWinRate(targetSurvey);

        List<SurveyGiveaway> surveyGiveaways = targetSurvey.getSurveyGiveaways();
        List<Giveaway> giveaways = new ArrayList<>();
        for(SurveyGiveaway surveyGiveaway : surveyGiveaways) {
            giveaways.add(surveyGiveaway.getGiveaway());
        }
        return SurveyDetailgetServiceResponse.builder()
                .surveyCategoryType(targetSurvey.getSurveyCategory().getType())
                .title(targetSurvey.getTitle())
                .headCount(targetSurvey.getHeadCount())
                .closedHeadCount(targetSurvey.getClosedHeadCount())
                .surveyResponseResults(surveyResponseResults)
                .startTime(targetSurvey.getStartTime())
                .endTime(targetSurvey.getEndTime())
                .writer(targetSurvey.getWriter())
                .writerName(accountRepository.getReferenceById(targetSurvey.getWriter()).getName())
                .winRate(winRate)
                .estimateTime(0)
                .questionCount(targetSurvey.getQuestions().size())
                .giveaways(giveaways)
                .build();
    }
}
