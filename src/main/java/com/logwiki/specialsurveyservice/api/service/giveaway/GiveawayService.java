package com.logwiki.specialsurveyservice.api.service.giveaway;

import com.logwiki.specialsurveyservice.api.controller.giveaway.request.GiveawayRequest;
import com.logwiki.specialsurveyservice.api.service.account.AccountService;
import com.logwiki.specialsurveyservice.api.service.giveaway.response.GiveawayResponse;
import com.logwiki.specialsurveyservice.api.service.giveaway.response.MyGiveawayResponse;
import com.logwiki.specialsurveyservice.domain.account.Account;
import com.logwiki.specialsurveyservice.domain.giveaway.Giveaway;
import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayRepository;
import com.logwiki.specialsurveyservice.domain.surveyresult.SurveyResult;
import com.logwiki.specialsurveyservice.domain.surveyresult.SurveyResultRepository;
import com.logwiki.specialsurveyservice.domain.targetnumber.TargetNumberRepository;
import com.logwiki.specialsurveyservice.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class GiveawayService {

    private final GiveawayRepository giveawayRepository;
    private final AccountService accountService;
    private final TargetNumberRepository targetNumberRepository;
    private final SurveyResultRepository surveyResultRepository;

    private final static double DEFAULT_PROBABILITY = 0;
    private final static double PARSE_100 = 100;

    @Transactional
    public GiveawayResponse createGiveaway(GiveawayRequest request) {
        giveawayRepository.findGiveawayByName(request.getName())
                .ifPresent(giveaway -> {
                    throw new BaseException("이미 등록되어 있는 상품이 있습니다.", 5000);
                });

        Giveaway giveaway = Giveaway.builder()
                .giveawayType(request.getGiveawayType())
                .name(request.getName())
                .price(request.getPrice())
                .build();

        Giveaway saveGiveaway = giveawayRepository.save(giveaway);
        return GiveawayResponse.of(saveGiveaway);
    }


    public List<GiveawayResponse> getGiveaways() {
        List<Giveaway> giveaways = giveawayRepository.findAll();

        return giveaways.stream()
                .map(GiveawayResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public GiveawayResponse deleteGiveaway(Long id) {
        Giveaway giveaway = giveawayRepository.findById(id)
                .orElseThrow(() -> new BaseException("삭제할 상품의 PK가 올바르지 않습니다.", 5001));
        giveawayRepository.delete(giveaway);
        return GiveawayResponse.of(giveaway);
    }

    @Transactional
    public GiveawayResponse updateGiveaway(Long id, GiveawayRequest giveawayRequest) {
        Giveaway giveaway = giveawayRepository.findById(id)
                .orElseThrow(() -> new BaseException("수정할 상품의 PK가 올바르지 않습니다.", 5002));

        Giveaway updatedGiveaway = giveaway.update(giveawayRequest);
        return GiveawayResponse.of(updatedGiveaway);
    }

    public List<MyGiveawayResponse> getMyGiveaways() {
        Account account = accountService.getCurrentAccountBySecurity();
        List<SurveyResult> surveyResults = surveyResultRepository.findSurveyResultsByAccount_Id(
                account.getId());

        List<SurveyResult> winSurveyResults = surveyResults.stream()
                .filter(SurveyResult::isResponse)
                .toList();

        return winSurveyResults.stream()
                .map(surveyResult -> {
                            if (targetNumberRepository.findTargetNumberByNumberAndSurvey_Id(
                                    surveyResult.getSubmitOrder(),
                                    surveyResult.getSurvey().getId()) == null) {
                                return MyGiveawayResponse.builder()
                                        .win(surveyResult.isWin())
                                        .userCheck(surveyResult.isUserCheck())
                                        .surveyId(surveyResult.getSurvey().getId())
                                        .surveyTitle(surveyResult.getSurvey().getTitle())
                                        .surveyWriter(accountService.getUserNameById(surveyResult.getSurvey().getWriter()))
                                        .probabilty(DEFAULT_PROBABILITY)
                                        .answerDateTime(surveyResult.getAnswerDateTime())
                                        .build();
                            }
                            return MyGiveawayResponse.of(
                                    surveyResult,
                                    targetNumberRepository.findTargetNumberByNumberAndSurvey_Id(
                                                    surveyResult.getSubmitOrder(),
                                                    surveyResult.getSurvey().getId())
                                            .getGiveaway(),
                                    accountService.getUserNameById(surveyResult.getSurvey().getWriter()),
                                    Math.min(100.0, (double) surveyResultRepository.findByGiveawaySurvey(surveyResult.getSurvey().getId(), surveyResult.getSubmitOrder())
                                            .orElse(0) / surveyResult.getSurvey().getHeadCount() * PARSE_100)
                            );
                        }
                )
                .toList();
    }
}
