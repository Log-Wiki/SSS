package com.logwiki.specialsurveyservice.api.service.survey;


import com.logwiki.specialsurveyservice.api.service.survey.request.SurveyCreateServiceRequest;
import com.logwiki.specialsurveyservice.domain.account.Account;
import com.logwiki.specialsurveyservice.domain.account.AccountRepository;
import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayRepository;
import com.logwiki.specialsurveyservice.domain.survey.Survey;
import com.logwiki.specialsurveyservice.domain.survey.SurveyRepository;
import com.logwiki.specialsurveyservice.domain.surveycategory.SurveyCategory;
import com.logwiki.specialsurveyservice.domain.surveycategory.SurveyCategoryRepository;
import com.logwiki.specialsurveyservice.domain.surveygiveaway.SurveyGiveaway;
import com.logwiki.specialsurveyservice.exception.BaseException;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class SurveyService {

    private final SurveyRepository surveyRepository;

    private final AccountRepository accountRepository;

    private final SurveyCategoryRepository surveyCategoryRepository;

    private final GiveawayRepository giveawayRepository;

    public void addSurvey(String userEmail, SurveyCreateServiceRequest dto) {
        Account account = accountRepository.findOneWithAuthoritiesByEmail(userEmail)
                .orElseThrow(() -> new BaseException("유저를 찾지못했습니다.", 1000));
        SurveyCategory surveyCategory = SurveyCategory.builder()
                .type(dto.getType())
                .build();
        Survey survey = surveyRepository.save(dto.toEntity(account.getId()));
        survey.addCategory(surveyCategory);

        // 설문에 당첨 상품 등록
        List<SurveyGiveaway> surveyGiveaways = dto.getGiveaways().stream()
                .map(giveaway -> SurveyGiveaway.create(giveaway.getCount(), survey,
                        giveawayRepository.findById(giveaway.getId())
                                .orElseThrow(
                                        () -> new BaseException("당첨 상품의 PK 값이 올바르지 않습니다.", 1000))))
                .collect(Collectors.toList());


        survey.addSurveyGiveaways(surveyGiveaways);
    }
}
