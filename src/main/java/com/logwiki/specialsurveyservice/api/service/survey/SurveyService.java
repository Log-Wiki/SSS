package com.logwiki.specialsurveyservice.api.service.survey;


import com.logwiki.specialsurveyservice.api.service.account.AccountService;
import com.logwiki.specialsurveyservice.api.service.question.response.QuestionAnswerStatisticsResponse;
import com.logwiki.specialsurveyservice.api.service.sse.response.SurveyAnswerResponse;
import com.logwiki.specialsurveyservice.api.service.survey.request.GiveawayAssignServiceRequest;
import com.logwiki.specialsurveyservice.api.service.survey.request.SurveyCreateServiceRequest;
import com.logwiki.specialsurveyservice.api.service.survey.response.AbstractSurveyResponse;
import com.logwiki.specialsurveyservice.api.service.survey.response.StatisticsSurveyResponse;
import com.logwiki.specialsurveyservice.api.service.survey.response.SurveyResponse;
import com.logwiki.specialsurveyservice.api.service.targetnumber.TargetNumberService;
import com.logwiki.specialsurveyservice.api.service.targetnumber.request.TargetNumberCreateServiceRequest;
import com.logwiki.specialsurveyservice.domain.account.Account;
import com.logwiki.specialsurveyservice.domain.account.AccountRepository;
import com.logwiki.specialsurveyservice.domain.accountcode.AccountCode;
import com.logwiki.specialsurveyservice.domain.accountcode.AccountCodeRepository;
import com.logwiki.specialsurveyservice.domain.accountcode.AccountCodeType;
import com.logwiki.specialsurveyservice.domain.accountsurvey.AccountSurvey;
import com.logwiki.specialsurveyservice.domain.accountsurvey.AccountSurveyRepository;
import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayRepository;
import com.logwiki.specialsurveyservice.domain.question.Question;
import com.logwiki.specialsurveyservice.domain.questionanswer.QuestionAnswer;
import com.logwiki.specialsurveyservice.domain.questionanswer.QuestionAnswerRepository;
import com.logwiki.specialsurveyservice.domain.questioncategory.QuestionCategoryType;
import com.logwiki.specialsurveyservice.domain.survey.AnswerPossibleType;
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
import com.logwiki.specialsurveyservice.domain.targetnumber.TargetNumberRepository;
import com.logwiki.specialsurveyservice.exception.BaseException;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Comparator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
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
    private final TargetNumberRepository targetNumberRepository;
    private final AccountRepository accountRepository;
    private final AccountSurveyRepository accountSurveyRepository;
    private final QuestionAnswerRepository questionAnswerRepository;

    private static final String LOSEPRODUCT = "꽝";
    private static final boolean HIDDEN_BOOLEAN_RESULT = false;
    private static final String HIDDEN_GIVEAWAY_NAME_RESULT = LOSEPRODUCT;

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

        List<Survey> surveysWithoutAlreadyAnswered = deleteAlreadyAnsweredSurvey(surveys);
        sortByEndTime(surveysWithoutAlreadyAnswered);

        return surveysWithoutAlreadyAnswered.stream()
                .map(survey
                        -> AbstractSurveyResponse.from(survey, accountService.getUserNameById(survey.getWriter())))
                .collect(Collectors.toList());
    }

    public List<AbstractSurveyResponse> getRecommendInstantSurveyForUser() {
        List<Survey> surveys = getRecommendSurveysBySurveyCategoryType(SurveyCategoryType.INSTANT_WIN);

        List<Survey> surveysWithoutAlreadyAnswered = deleteAlreadyAnsweredSurvey(surveys);
        sortByWinningPercent(surveysWithoutAlreadyAnswered);

        return surveysWithoutAlreadyAnswered.stream()
                .map(survey
                        -> AbstractSurveyResponse.from(survey, accountService.getUserNameById(survey.getWriter())))
                .collect(Collectors.toList());
    }

    public List<AbstractSurveyResponse> getRecommendShortTimeSurveyForUser() {
        List<Survey> surveys = getAllRecommendSurveys();

        List<Survey> surveysWithoutAlreadyAnswered = deleteAlreadyAnsweredSurvey(surveys);
        sortByRequiredTimeForSurvey(surveysWithoutAlreadyAnswered);

        return surveysWithoutAlreadyAnswered.stream()
                .map(survey
                        -> AbstractSurveyResponse.from(survey, accountService.getUserNameById(survey.getWriter())))
                .collect(Collectors.toList());
    }

    private List<Survey> deleteAlreadyAnsweredSurvey(List<Survey> surveys) {
        Account account = accountService.getCurrentAccountBySecurity();

        List<AccountSurvey> accountSurveys = accountSurveyRepository.findAllByAccountId(account.getId());
        List<Long> answeredSurveyIds = accountSurveys.stream()
                .map(accountSurvey -> accountSurvey.getSurvey().getId())
                .collect(Collectors.toList());

        return surveys.stream()
                .filter(survey -> !answeredSurveyIds.contains(survey.getId()))
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

    public List<SurveyAnswerResponse> getSurveyAnswers(Long surveyId) {
        Survey survey = surveyRepository.findById(surveyId).orElseThrow(() -> new BaseException("없는 설문입니다.",3005));

        List<SurveyAnswerResponse> surveyResponseResults = new ArrayList<>();
        for(SurveyResult surveyResult : survey.getSurveyResults()) {

            String giveawayName = LOSEPRODUCT;
            boolean isWin = false;

            TargetNumber targetNumber = targetNumberRepository.findTargetNumberByNumberAndSurvey_Id(
                    surveyResult.getSubmitOrder(),
                    surveyResult.getSurvey().getId());
            if (targetNumber != null) {
                isWin = true;
                giveawayName = targetNumber.getGiveaway().getName();
            }

            if (survey.getSurveyCategory().getType().equals(SurveyCategoryType.NORMAL)) {
                if(!survey.isClosed()) {
                    isWin = HIDDEN_BOOLEAN_RESULT;
                    giveawayName = HIDDEN_GIVEAWAY_NAME_RESULT;
                }
            }

            surveyResponseResults.add(SurveyAnswerResponse.from(surveyResult,giveawayName,isWin));
        }

        return surveyResponseResults;
    }
    public AbstractSurveyResponse getSurveyDetail(Long surveyId) {
        Optional<Survey> targetSurveyOptional =  surveyRepository.findById(surveyId);

        if(targetSurveyOptional.isEmpty()) {
            throw new BaseException("없는 설문입니다." , 3005);
        }
        Survey targetSurvey = targetSurveyOptional.get();
        List<SurveyGiveaway> surveyGiveaways = targetSurvey.getSurveyGiveaways();
        List<String> giveawayNames = new ArrayList<>();
        for(SurveyGiveaway surveyGiveaway : surveyGiveaways) {
            giveawayNames.add(surveyGiveaway.getGiveaway().getName());
        }

        Optional<Account> writerAccount =  accountRepository.findById(targetSurvey.getWriter());
        if(writerAccount.isEmpty()){
            throw new BaseException("설문 작성자가 존재하지 않습니다.", 3013);
        }
        return AbstractSurveyResponse.from(targetSurvey, writerAccount.get().getName());
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

    public boolean checkPastHistory(Account account, Long surveyId) {
        SurveyResult checkSurveyResult = surveyResultRepository.findSurveyResultBySurvey_IdAndAccount_Id(surveyId, account.getId());
        if(checkSurveyResult != null) {
            return false;
        }
        return true;
    }
    public boolean checkType(Account account , Long surveyId) {

        Long genderId = accountCodeRepository.findAccountCodeByType(account.getGender())
                .orElseThrow(() -> new BaseException("성별 코드가 올바르지 않습니다.", 2004))
                .getId();
        Long ageId = accountCodeRepository.findAccountCodeByType(account.getAge())
                .orElseThrow(() -> new BaseException("나이 코드가 올바르지 않습니다.", 2005))
                .getId();
        int checks = surveyRepository.checkSurveyPossible(surveyId,genderId,ageId);
        if(checks == 0) {
            return false;
        }
        return true;
    }
    public boolean checkTimeBefore(SurveyResponse surveyResponse, LocalDateTime currentTime) {
        if(surveyResponse.getStartTime().isAfter(currentTime)) {
            return false;
        }
        return true;
    }
    public boolean checkTimeOver(SurveyResponse surveyResponse, LocalDateTime currentTime) {
        if(surveyResponse.getEndTime().isBefore(currentTime)) {
            return false;
        }
        return true;
    }
    public AnswerPossibleType getAnswerPossible(Long surveyId) {
        Account account = accountService.getCurrentAccountBySecurity();

        SurveyResponse surveyResponse = this.getSurvey(surveyId);
        Survey survey = surveyRepository.findById(surveyId).get();

        LocalDateTime currentTIme = LocalDateTime.now();

        if(checkType(account , surveyId) == false) {
            return AnswerPossibleType.TYPENOTMATCH;
        }
        if(checkPastHistory(account,surveyId) == false) {
            return AnswerPossibleType.DIDANSWER;
        }
        if(checkTimeBefore(surveyResponse,currentTIme) == false){
            return AnswerPossibleType.TIMEBEFORE;
        }
        if(checkTimeOver(surveyResponse,currentTIme) == false) {
            return AnswerPossibleType.TIMEOVER;
        }
        if(survey.getHeadCount() >= survey.getClosedHeadCount()) {
            return AnswerPossibleType.HEADFULL;
        }

        return AnswerPossibleType.CANANSWER;
    }

    public StatisticsSurveyResponse getStatistics(Long surveyId) {

        Survey survey = surveyRepository.findById(surveyId).orElseThrow(() -> new BaseException("없는 설문입니다.", 3005));
        String writerName = accountService.getUserNameById(survey.getWriter());
        Account account = accountService.getCurrentAccountBySecurity();
        if(!account.getName().equals(writerName)) {
            throw new BaseException("설문 작성자만 설문 통계를 확인할 수 있습니다.", 3021);
        }

        List<QuestionAnswerStatisticsResponse> questionAnswers = new ArrayList<>();
        for(Question question : survey.getQuestions()) {

            List<String> questionAnswerResponse = getQuestionAnswerResponse(question.getId(), question.getType());

            QuestionAnswerStatisticsResponse questionAnswerStatisticsResponse = QuestionAnswerStatisticsResponse
                    .builder()
                    .questionId(question.getId())
                    .questionCategoryType(question.getType())
                    .answers(questionAnswerResponse)
                    .build();

            questionAnswers.add(questionAnswerStatisticsResponse);
        }

        return StatisticsSurveyResponse.from(survey, writerName, questionAnswers);
    }

    private List<String> getQuestionAnswerResponse(Long questionId, QuestionCategoryType questionType) {
        List<QuestionAnswer> questionAnswer = questionAnswerRepository.findAllByQuestion_Id(
                questionId);

        List<String> questionAnswerResponse = new ArrayList<>();
        if((questionType == QuestionCategoryType.MULTIPLE_CHOICE)
                || (questionType == QuestionCategoryType.DROP_DOWN)
                || (questionType == QuestionCategoryType.CHECK_BOX)) {
            questionAnswerResponse = questionAnswer.stream()
                    .map(questionAnswer1 -> questionAnswer1.getAnswerNumber().toString())
                    .collect(Collectors.toList());
        }
        else if((questionType == QuestionCategoryType.SHORT_FORM)
                || (questionType == QuestionCategoryType.DATE_FORM)
                || (questionType == QuestionCategoryType.TIME_FORM)) {
            questionAnswerResponse = questionAnswer.stream()
                    .map(questionAnswer1 -> questionAnswer1.getShortFormAnswer().toString())
                    .collect(Collectors.toList());
        }

        return questionAnswerResponse;
    }
}
