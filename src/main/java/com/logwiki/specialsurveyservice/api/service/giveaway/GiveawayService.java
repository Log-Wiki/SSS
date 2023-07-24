package com.logwiki.specialsurveyservice.api.service.giveaway;

import com.logwiki.specialsurveyservice.api.controller.giveaway.request.GiveawayDto;
import com.logwiki.specialsurveyservice.api.service.giveaway.response.GiveawayResponse;
import com.logwiki.specialsurveyservice.domain.giveaway.Giveaway;
import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayRepository;
import com.logwiki.specialsurveyservice.exception.BaseException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class GiveawayService {

    private final GiveawayRepository giveawayRepository;

    @Transactional
    public GiveawayResponse createGiveaway(GiveawayDto request) {
        Optional<Giveaway> giveawayByName = giveawayRepository.findGiveawayByName(request.getName());
        if(giveawayByName.isPresent())
            throw new BaseException("이미 등록되어 있는 상품이 있습니다", 1000);

        Giveaway giveaway = Giveaway.builder()
                .giveawayType(request.getGiveawayType())
                .name(request.getName())
                .price(request.getPrice())
                .build();

        Giveaway saveGiveaway = giveawayRepository.save(giveaway);
        return GiveawayResponse.of(saveGiveaway);
    }
}
