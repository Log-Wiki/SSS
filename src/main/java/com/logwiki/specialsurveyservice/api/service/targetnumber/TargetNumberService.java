package com.logwiki.specialsurveyservice.api.service.targetnumber;

import com.logwiki.specialsurveyservice.api.service.targetnumber.request.TargetNumberCreateServiceRequest;
import jakarta.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class TargetNumberService {

    public Map<Integer, Long> createTargetNumbers(TargetNumberCreateServiceRequest request) {
        Map<Integer, Long> targetNumbers = new HashMap<>();
        Map<Long, Integer> giveaways = request.getGiveaways();
        for(Long giveawayId : giveaways.keySet()) {
            int giveawayCount = giveaways.get(giveawayId);
            while(giveawayCount > 0) {
                int randomNumber = ThreadLocalRandom.current().nextInt(1, request.getHeadCount());
                if(targetNumbers.containsKey(randomNumber))
                    continue;

                targetNumbers.put(randomNumber, giveawayId);
                giveawayCount--;
            }
        }
        return targetNumbers;
    }
}
