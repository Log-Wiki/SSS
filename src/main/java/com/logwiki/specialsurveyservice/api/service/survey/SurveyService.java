package com.logwiki.specialsurveyservice.api.service.survey;


import com.logwiki.specialsurveyservice.api.controller.sse.response.SurveyAnswerResponse;
import com.logwiki.specialsurveyservice.api.service.giveaway.GiveawayService;
import com.logwiki.specialsurveyservice.api.service.survey.request.GiveawayAssignServiceRequest;
import com.logwiki.specialsurveyservice.api.service.survey.request.SurveyCreateServiceRequest;
import com.logwiki.specialsurveyservice.api.service.survey.response.SurveyDetailResponse;
import com.logwiki.specialsurveyservice.api.service.survey.response.SurveyResponse;
import com.logwiki.specialsurveyservice.api.service.targetnumber.TargetNumberService;
import com.logwiki.specialsurveyservice.api.service.targetnumber.request.TargetNumberCreateServiceRequest;
import com.logwiki.specialsurveyservice.api.utils.SecurityUtil;
import com.logwiki.specialsurveyservice.domain.account.Account;
import com.logwiki.specialsurveyservice.domain.account.AccountRepository;
import com.logwiki.specialsurveyservice.domain.accountcode.AccountCode;
import com.logwiki.specialsurveyservice.domain.accountcode.AccountCodeRepository;
import com.logwiki.specialsurveyservice.domain.accountcode.AccountCodeType;
import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayRepository;
import com.logwiki.specialsurveyservice.domain.survey.Survey;
import com.logwiki.specialsurveyservice.domain.survey.SurveyRepository;
import com.logwiki.specialsurveyservice.domain.surveycategory.SurveyCategory;
import com.logwiki.specialsurveyservice.domain.surveycategory.SurveyCategoryRepository;
import com.logwiki.specialsurveyservice.domain.surveycategory.SurveyCategoryType;
import com.logwiki.specialsurveyservice.domain.surveycategory.SurveyCategoryType;
import com.logwiki.specialsurveyservice.domain.surveygiveaway.SurveyGiveaway;
import com.logwiki.specialsurveyservice.domain.surveyresult.SurveyResult;
import com.logwiki.specialsurveyservice.domain.surveytarget.SurveyTarget;
import com.logwiki.specialsurveyservice.domain.targetnumber.TargetNumber;
import com.logwiki.specialsurveyservice.domain.targetnumber.TargetNumberRepository;
import com.logwiki.specialsurveyservice.exception.BaseException;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
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
    private final SurveyCategoryRepository surveyCategoryRepository;

    private final GiveawayService giveawayService;

    private final TargetNumberRepository targetNumberRepository;

    private static final double MAXPROBABILITY = 100.0;

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

        SurveyCategory surveyCategoryByType = surveyCategoryRepository.findSurveyCategoryByType(
                dto.getType());
        survey.addSurveyCategory(surveyCategoryByType);

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

    public List<SurveyResponse> getRecommendNormalSurvey() {
        List<Survey> surveys = getRecommendSurveys(SurveyCategoryType.NORMAL);

        sortByEndTime(surveys);
        return surveys.stream()
                .map(SurveyResponse::from)
                .collect(Collectors.toList());
    }

    public List<SurveyResponse> getRecommendInstantSurvey() {
        List<Survey> surveys = getRecommendSurveys(SurveyCategoryType.INSTANT_WIN);

        sortByWinningPercent(surveys);

        return surveys.stream()
                .map(SurveyResponse::from)
                .collect(Collectors.toList());
    }

    private List<Survey> getRecommendSurveys(SurveyCategoryType surveyCategoryType) {
        Account account = SecurityUtil.getCurrentUsername()
                .flatMap(accountRepository::findOneWithAuthoritiesByEmail)
                .orElseThrow(() -> new BaseException("존재하지 않는 유저입니다.", 2000));
        Long genderId = accountCodeRepository.findAccountCodeByType(account.getGender())
                .orElseThrow(() -> new BaseException("성별 코드가 올바르지 않습니다.", 2004))
                .getId();
        Long ageId = accountCodeRepository.findAccountCodeByType(account.getAge())
                .orElseThrow(() -> new BaseException("나이 코드가 올바르지 않습니다.", 2005))
                .getId();

        return surveyRepository.findRecommendSurvey(surveyCategoryType.toString(),
                genderId, ageId);
    }

    private static void sortByEndTime(List<Survey> surveys) {
        surveys.sort((survey1, survey2) -> {
            LocalDateTime survey1EndTime = survey1.getEndTime();
            LocalDateTime survey2EndTime = survey2.getEndTime();
            return survey1EndTime.compareTo(survey2EndTime);
        });
    }

    private static void sortByWinningPercent(List<Survey> surveys) {
        surveys.sort((survey1, survey2) -> {
            int survey1GiveawayCount = survey1.getSurveyGiveaways().stream()
                    .mapToInt(SurveyGiveaway::getCount)
                    .sum();
            int survey2GiveawayCount = survey2.getSurveyGiveaways().stream()
                    .mapToInt(SurveyGiveaway::getCount)
                    .sum();
            float survey1WinningPercent =
                    (float) survey1GiveawayCount / survey1.getClosedHeadCount();
            float survey2WinningPercent =
                    (float) survey2GiveawayCount / survey2.getClosedHeadCount();
            return Float.compare(survey2WinningPercent, survey1WinningPercent);
        });
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
                winRate = MAXPROBABILITY;
            } else {
                winRate = (double)giveawayNum / survey.getHeadCount();
            }
        }
        else {
            winRate = (double)giveawayNum / survey.getClosedHeadCount();
        }

        return  winRate;
    }

    public SurveyDetailResponse getSurveyDetail(Long surveyId) {
        Optional<Survey> targetSurveyOptional =  surveyRepository.findById(surveyId);
        if(targetSurveyOptional.isEmpty()) {
            throw new BaseException("없는 설문입니다." , 3005);
        }
        Survey targetSurvey = targetSurveyOptional.get();
        List<SurveyAnswerResponse> surveyResponseResults = new ArrayList<>();

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

                surveyResponseResults.add(new SurveyAnswerResponse(
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
        List<String> giveawayNames = new ArrayList<>();
        for(SurveyGiveaway surveyGiveaway : surveyGiveaways) {
            giveawayNames.add(surveyGiveaway.getGiveaway().getName());
        }
        Optional<Account> writerAccount =  accountRepository.findById(targetSurvey.getWriter());
        if(writerAccount.isEmpty()){
            throw new BaseException("설문 작성자가 존재하지 않습니다.", 3013);
        }
        return SurveyDetailResponse.of(targetSurvey,surveyResponseResults,winRate,giveawayNames,writerAccount.get().getName());
    }
}
