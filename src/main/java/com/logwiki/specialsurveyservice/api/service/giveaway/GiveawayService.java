package com.logwiki.specialsurveyservice.api.service.giveaway;

import com.logwiki.specialsurveyservice.api.controller.giveaway.request.GiveawayDto;
import com.logwiki.specialsurveyservice.api.service.giveaway.response.GiveawayResponse;
import com.logwiki.specialsurveyservice.domain.giveaway.Giveaway;
import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayRepository;
import com.logwiki.specialsurveyservice.exception.BaseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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


    public List<GiveawayResponse> getGiveaways() {
        List<Giveaway> giveaways = giveawayRepository.findAll();

        return giveaways.stream()
                .map(giveaway -> GiveawayResponse.of(giveaway))
                .collect(Collectors.toList());
    }

    @Transactional
    public GiveawayResponse deleteGiveaway(String name) {
        System.out.println("삭제 이름 : " + name);
        Optional<Giveaway> giveawayByName = giveawayRepository.findGiveawayByName(name);
        System.out.println(giveawayByName.isEmpty());
        if(giveawayByName.isEmpty())
            throw new BaseException("삭제할 상품의 이름이 올바르지 않습니다.", 1000);

        Giveaway giveaway = giveawayByName.get();
        giveawayRepository.delete(giveaway);
        return GiveawayResponse.of(giveaway);
    }
}
