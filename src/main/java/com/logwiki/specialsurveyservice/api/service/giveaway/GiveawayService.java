package com.logwiki.specialsurveyservice.api.service.giveaway;

import com.logwiki.specialsurveyservice.api.controller.giveaway.request.GiveawayRequest;
import com.logwiki.specialsurveyservice.api.service.giveaway.response.GiveawayResponse;
import com.logwiki.specialsurveyservice.domain.giveaway.Giveaway;
import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayRepository;
import com.logwiki.specialsurveyservice.domain.survey.Survey;
import com.logwiki.specialsurveyservice.domain.surveygiveaway.SurveyGiveaway;
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
    private static final int EMPTY = 0;
    private static final int FIRST = 0;
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
                .map(giveaway -> GiveawayResponse.of(giveaway))
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

    public GiveawayResponse getRepGiveaway(Survey survey) {
        List<SurveyGiveaway> surveyGiveaways = survey.getSurveyGiveaways();
        if(surveyGiveaways.size() == EMPTY) {
            throw new BaseException("설문조사의 상품이 하나도 조회되지 않습니다." , 5005);
        }
        Giveaway repGiveaway = surveyGiveaways.get(FIRST).getGiveaway();
        for(SurveyGiveaway surveyGiveaway : surveyGiveaways) {
            if(surveyGiveaway.getGiveaway().getPrice() > repGiveaway.getPrice()){
                repGiveaway = surveyGiveaway.getGiveaway();
            }
        }

        return GiveawayResponse.of(repGiveaway);
    }
}
