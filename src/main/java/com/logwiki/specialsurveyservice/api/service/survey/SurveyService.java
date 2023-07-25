package com.logwiki.specialsurveyservice.api.service.survey;


import com.logwiki.specialsurveyservice.api.service.survey.request.SurveyCreateServiceRequest;
import com.logwiki.specialsurveyservice.domain.account.Account;
import com.logwiki.specialsurveyservice.domain.account.AccountRepository;
import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayRepository;
import com.logwiki.specialsurveyservice.domain.survey.Survey;
import com.logwiki.specialsurveyservice.domain.survey.SurveyRepository;
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

    private final GiveawayRepository giveawayRepository;

    public Survey addSurvey(String userEmail, SurveyCreateServiceRequest dto) {
        Account account = accountRepository.findOneWithAuthoritiesByEmail(userEmail)
                .orElseThrow(() -> new BaseException("유저를 찾지못했습니다.", 1000));

        Survey survey = dto.toEntity(account.getId());

        List<SurveyGiveaway> surveyGiveaways = dto.getGiveaways().stream()
                .map(giveaway -> SurveyGiveaway.create(giveaway.getCount(), survey,
                        giveawayRepository.findById(giveaway.getId())
                                .orElseThrow(
                                        () -> new BaseException("등록되어 있지 않은 당첨 상품을 포함하고 있습니다.", 1000))))
                .collect(Collectors.toList());
        survey.addSurveyGiveaways(surveyGiveaways);

        return surveyRepository.save(survey);
    }
}
