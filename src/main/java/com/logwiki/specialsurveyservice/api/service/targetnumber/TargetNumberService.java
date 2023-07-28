package com.logwiki.specialsurveyservice.api.service.targetnumber;

import com.logwiki.specialsurveyservice.api.service.targetnumber.request.TargetNumberCreateServiceRequest;
import com.logwiki.specialsurveyservice.api.utils.Randoms;
import com.logwiki.specialsurveyservice.domain.giveaway.Giveaway;
import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayRepository;
import com.logwiki.specialsurveyservice.domain.survey.Survey;
import com.logwiki.specialsurveyservice.domain.targetnumber.TargetNumber;
import com.logwiki.specialsurveyservice.exception.BaseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class TargetNumberService {

    private final GiveawayRepository giveawayRepository;

    private static final int START_RANDOM_NUMBER = 1;

    public List<TargetNumber> createTargetNumbers(TargetNumberCreateServiceRequest request) {
        int closedHeadCount = request.getClosedHeadCount();

        Map<Long, Integer> giveawayIdAndCount = request.getGiveaways();
        Map<Integer, Long> targetNumberAndGiveawayId = getTargetNumberAndGiveawayId(closedHeadCount, giveawayIdAndCount);

        Survey survey = request.getSurvey();
        List<TargetNumber> targetNumbers = getTargetNumbers(targetNumberAndGiveawayId, survey);
        request.getSurvey().addTargetNumbers(targetNumbers);

        return targetNumbers;
    }

    public Map<Integer, Long> getTargetNumberAndGiveawayId(int closedHeadCount, Map<Long, Integer> giveawayIdAndCount) {
        int totalGiveawayCount = giveawayIdAndCount.values().stream()
                .mapToInt(Integer::intValue)
                .sum();

        List<Integer> randomNumbers = Randoms.pickUniqueNumbersInRange(START_RANDOM_NUMBER, closedHeadCount,
                totalGiveawayCount);

        Map<Integer, Long> targetNumberAndGiveawayId = new HashMap<>();
        int idx = 0;
        for (Long giveawayId : giveawayIdAndCount.keySet()) {
            int giveawayCount = giveawayIdAndCount.get(giveawayId);
            while (giveawayCount > 0) {
                int randomNumber = randomNumbers.get(idx++);
                targetNumberAndGiveawayId.put(randomNumber, giveawayId);
                giveawayCount--;
            }
        }

        return targetNumberAndGiveawayId;
    }

    public List<TargetNumber> getTargetNumbers(Map<Integer, Long> targetNumberAndGiveawayId, Survey survey) {
        List<TargetNumber> targetNumbers = new ArrayList<>();

        for (int key : targetNumberAndGiveawayId.keySet()) {
            Giveaway giveaway = giveawayRepository.findById(targetNumberAndGiveawayId.get(key)).orElseThrow(
                    () -> new BaseException("설문에 등록할 당첨 상품의 값이 올바르지 않습니다.", 5004));
            TargetNumber targetNumber = TargetNumber.create(key, survey, giveaway);
            targetNumbers.add(targetNumber);
        }

        return targetNumbers;
    }
}
