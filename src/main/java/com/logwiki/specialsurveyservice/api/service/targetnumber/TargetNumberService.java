package com.logwiki.specialsurveyservice.api.service.targetnumber;

import com.logwiki.specialsurveyservice.api.service.targetnumber.request.TargetNumberCreateServiceRequest;
import com.logwiki.specialsurveyservice.domain.giveaway.Giveaway;
import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayRepository;
import com.logwiki.specialsurveyservice.domain.targetnumber.TargetNumber;
import com.logwiki.specialsurveyservice.domain.targetnumber.TargetNumberRepository;
import com.logwiki.specialsurveyservice.exception.BaseException;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class TargetNumberService {

    private final GiveawayRepository giveawayRepository;

    private final TargetNumberRepository targetNumberRepository;

    public List<TargetNumber> createTargetNumbers(TargetNumberCreateServiceRequest request) {
        Map<Long, Integer> giveaways = request.getGiveaways();
        Map<Integer, Long> targetNumbers = getTargetNumbers(request, giveaways);

        List<TargetNumber> targetNumbersForSurvey = getTargetNumbersForSurvey(
                request, targetNumbers);
        request.getSurvey().addTargetNumbers(targetNumbersForSurvey);

        return targetNumbersForSurvey;
    }

    private static Map<Integer, Long> getTargetNumbers(TargetNumberCreateServiceRequest request,
            Map<Long, Integer> giveaways) {
        Map<Integer, Long> targetNumbers = new HashMap<>();
        for (Long giveawayId : giveaways.keySet()) {
            int giveawayCount = giveaways.get(giveawayId);
            while (giveawayCount > 0) {
                int randomNumber = ThreadLocalRandom.current()
                        .nextInt(1, request.getClosedHeadCount());
                if (targetNumbers.containsKey(randomNumber)) {
                    continue;
                }

                targetNumbers.put(randomNumber, giveawayId);
                giveawayCount--;
            }
        }
        return targetNumbers;
    }

    private List<TargetNumber> getTargetNumbersForSurvey(TargetNumberCreateServiceRequest request,
            Map<Integer, Long> targetNumbers) {
        List<TargetNumber> targetNumbersForSurvey = new ArrayList<>();
        for (int key : targetNumbers.keySet()) {
            Giveaway giveaway = giveawayRepository.findById(targetNumbers.get(key)).orElseThrow(
                    () -> new BaseException("설문에 등록할 당첨 상품의 값이 올바르지 않습니다.", 1000));
            TargetNumber targetNumber = TargetNumber.create(key, request.getSurvey(), giveaway);
            targetNumbersForSurvey.add(targetNumber);
        }
        return targetNumbersForSurvey;
    }
}
