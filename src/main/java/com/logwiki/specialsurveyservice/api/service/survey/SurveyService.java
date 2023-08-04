package com.logwiki.specialsurveyservice.api.service.survey;


import com.logwiki.specialsurveyservice.api.service.account.AccountService;
import com.logwiki.specialsurveyservice.api.service.survey.request.GiveawayAssignServiceRequest;
import com.logwiki.specialsurveyservice.api.service.survey.request.SurveyCreateServiceRequest;
import com.logwiki.specialsurveyservice.api.service.survey.response.AbstractSurveyResponse;
import com.logwiki.specialsurveyservice.api.service.survey.response.SurveyResponse;
import com.logwiki.specialsurveyservice.api.service.targetnumber.TargetNumberService;
import com.logwiki.specialsurveyservice.api.service.targetnumber.request.TargetNumberCreateServiceRequest;
import com.logwiki.specialsurveyservice.domain.account.Account;
import com.logwiki.specialsurveyservice.domain.accountcode.AccountCode;
import com.logwiki.specialsurveyservice.domain.accountcode.AccountCodeRepository;
import com.logwiki.specialsurveyservice.domain.accountcode.AccountCodeType;
import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayRepository;
import com.logwiki.specialsurveyservice.domain.survey.Survey;
import com.logwiki.specialsurveyservice.domain.survey.SurveyRepository;
import com.logwiki.specialsurveyservice.domain.surveycategory.SurveyCategory;
import com.logwiki.specialsurveyservice.domain.surveycategory.SurveyCategoryRepository;
import com.logwiki.specialsurveyservice.domain.surveycategory.SurveyCategoryType;
import com.logwiki.specialsurveyservice.domain.surveygiveaway.SurveyGiveaway;
import com.logwiki.specialsurveyservice.domain.surveyresult.SurveyResult;
import com.logwiki.specialsurveyservice.domain.surveyresult.SurveyResultRepository;
import com.logwiki.specialsurveyservice.domain.surveytarget.SurveyTarget;
import com.logwiki.specialsurveyservice.domain.targetnumber.TargetNumber;
import com.logwiki.specialsurveyservice.exception.BaseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SurveyService {

    private final SurveyRepository surveyRepository;
    private final AccountService accountService;
    private final GiveawayRepository giveawayRepository;
    private final TargetNumberService targetNumberService;
    private final AccountCodeRepository accountCodeRepository;
    private final SurveyCategoryRepository surveyCategoryRepository;
    private final SurveyResultRepository surveyResultRepository;

    public SurveyResponse addSurvey(SurveyCreateServiceRequest dto) {
        Account account = accountService.getCurrentAccountBySecurity();

        checkTimeValidate(dto);

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
        sortGiveawaysByPrice(surveyGiveaways);
        survey.addSurveyGiveaways(surveyGiveaways);

        TargetNumberCreateServiceRequest targetNumberCreateServiceRequest = TargetNumberCreateServiceRequest.create(
                dto.getClosedHeadCount(), giveawayAssignServiceRequests, survey);
        List<TargetNumber> targetNumbers = targetNumberService.createTargetNumbers(
                targetNumberCreateServiceRequest);
        survey.addTargetNumbers(targetNumbers);
        surveyRepository.save(survey);

        account.increaseCreateSurveyCount();

        return SurveyResponse.from(survey);
    }

    private static void checkTimeValidate(SurveyCreateServiceRequest dto) {
        if (LocalDateTime.now().isAfter(dto.getEndTime())) {
            throw new BaseException("설문 마감시간은 현재 시간보다 커야합니다.", 3017);
        } else if (dto.getEndTime().isBefore(dto.getStartTime())) {
            throw new BaseException("설문 마감시간은 시작 시간보다 커야합니다.", 3018);
        }
    }

    private void sortGiveawaysByPrice(List<SurveyGiveaway> surveyGiveaways) {
        surveyGiveaways
                .sort(Comparator.comparing((SurveyGiveaway sg) -> sg.getGiveaway().getPrice()).reversed());
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

    public List<AbstractSurveyResponse> getRecommendNormalSurveyForAnonymous() {
        List<Survey> surveys = surveyRepository.findRecommendSurveyForAnonymous(SurveyCategoryType.NORMAL.toString());

        sortByEndTime(surveys);

        return surveys.stream()
                .map(survey
                        -> AbstractSurveyResponse.from(survey, accountService.getUserNameById(survey.getWriter())))
                .collect(Collectors.toList());
    }

    public List<AbstractSurveyResponse> getRecommendInstantSurveyForAnonymous() {
        List<Survey> surveys = surveyRepository.findRecommendSurveyForAnonymous(SurveyCategoryType.INSTANT_WIN.toString());

        sortByWinningPercent(surveys);

        return surveys.stream()
                .map(survey
                        -> AbstractSurveyResponse.from(survey, accountService.getUserNameById(survey.getWriter())))
                .collect(Collectors.toList());
    }

    public List<AbstractSurveyResponse> getRecommendShortTimeSurveyForAnonymous() {
        List<Survey> surveys = surveyRepository.findRecommendSurveyForAnonymous();

        sortByRequiredTimeForSurvey(surveys);

        return surveys.stream()
                .map(survey
                        -> AbstractSurveyResponse.from(survey, accountService.getUserNameById(survey.getWriter())))
                .collect(Collectors.toList());
    }


    public List<AbstractSurveyResponse> getRecommendNormalSurveyForUser() {
        List<Survey> surveys = getRecommendSurveysBySurveyCategoryType(SurveyCategoryType.NORMAL);

        sortByEndTime(surveys);

        return surveys.stream()
                .map(survey
                        -> AbstractSurveyResponse.from(survey, accountService.getUserNameById(survey.getWriter())))
                .collect(Collectors.toList());
    }

    public List<AbstractSurveyResponse> getRecommendInstantSurveyForUser() {
        List<Survey> surveys = getRecommendSurveysBySurveyCategoryType(SurveyCategoryType.INSTANT_WIN);

        sortByWinningPercent(surveys);

        return surveys.stream()
                .map(survey
                        -> AbstractSurveyResponse.from(survey, accountService.getUserNameById(survey.getWriter())))
                .collect(Collectors.toList());
    }

    public List<AbstractSurveyResponse> getRecommendShortTimeSurveyForUser() {
        List<Survey> surveys = getAllRecommendSurveys();

        sortByRequiredTimeForSurvey(surveys);

        return surveys.stream()
                .map(survey
                        -> AbstractSurveyResponse.from(survey, accountService.getUserNameById(survey.getWriter())))
                .collect(Collectors.toList());
    }

    private List<Survey> getRecommendSurveysBySurveyCategoryType(SurveyCategoryType surveyCategoryType) {
        Account account = accountService.getCurrentAccountBySecurity();
        Long genderId = accountCodeRepository.findAccountCodeByType(account.getGender())
                .orElseThrow(() -> new BaseException("성별 코드가 올바르지 않습니다.", 2004))
                .getId();
        Long ageId = accountCodeRepository.findAccountCodeByType(account.getAge())
                .orElseThrow(() -> new BaseException("나이 코드가 올바르지 않습니다.", 2005))
                .getId();

        return surveyRepository.findRecommendSurvey(surveyCategoryType.toString(),
                genderId, ageId);
    }

    private List<Survey> getAllRecommendSurveys() {
        Account account = accountService.getCurrentAccountBySecurity();
        Long genderId = accountCodeRepository.findAccountCodeByType(account.getGender())
                .orElseThrow(() -> new BaseException("성별 코드가 올바르지 않습니다.", 2004))
                .getId();
        Long ageId = accountCodeRepository.findAccountCodeByType(account.getAge())
                .orElseThrow(() -> new BaseException("나이 코드가 올바르지 않습니다.", 2005))
                .getId();

        return surveyRepository.findRecommendSurvey(genderId, ageId);
    }

    private void sortByEndTime(List<Survey> surveys) {
        surveys.sort((survey1, survey2) -> {
            LocalDateTime survey1EndTime = survey1.getEndTime();
            LocalDateTime survey2EndTime = survey2.getEndTime();
            return survey1EndTime.compareTo(survey2EndTime);
        });
    }

    private void sortByWinningPercent(List<Survey> surveys) {
        surveys.sort((survey1, survey2) -> {
            int survey1GiveawayCount = survey1.getTotalGiveawayCount();
            int survey2GiveawayCount = survey2.getTotalGiveawayCount();
            double survey1WinningPercent =
                    (double) survey1GiveawayCount / survey1.getClosedHeadCount();
            double survey2WinningPercent =
                    (double) survey2GiveawayCount / survey2.getClosedHeadCount();
            return Double.compare(survey2WinningPercent, survey1WinningPercent);
        });
    }

    private void sortByRequiredTimeForSurvey(List<Survey> surveys) {
        surveys.sort(Comparator.comparingInt(Survey::getRequiredTimeInSeconds));
    }

    public SurveyResponse getSurvey(Long surveyId) {
        return SurveyResponse.from(surveyRepository.findById(surveyId)
                .orElseThrow(() -> new BaseException("없는 설문입니다.", 3005)));
    }

    public List<AbstractSurveyResponse> getMySurveys() {
        Account account = accountService.getCurrentAccountBySecurity();
        List<Survey> mySurveys = surveyRepository.findAllByWriter(account.getId());

        return mySurveys.stream()
                .map(survey
                        -> AbstractSurveyResponse.from(survey, accountService.getUserNameById(survey.getWriter())))
                .collect(Collectors.toList());
    }

    public List<AbstractSurveyResponse> getAnsweredSurveys() {
        Account account = accountService.getCurrentAccountBySecurity();
        List<SurveyResult> surveyResults = surveyResultRepository.findSurveyResultsByAccount_Id(
                account.getId());

        List<Survey> surveys = surveyResults.stream()
                .map(SurveyResult::getSurvey)
                .toList();

        return surveys.stream()
                .map(survey
                        -> AbstractSurveyResponse.from(survey, accountService.getUserNameById(survey.getWriter())))
                .collect(Collectors.toList());
    }

}
