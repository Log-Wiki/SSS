package com.logwiki.specialsurveyservice.api.service.sse;
import com.logwiki.specialsurveyservice.api.service.sse.response.SurveyAnswerResponse;
import com.logwiki.specialsurveyservice.api.service.survey.response.SurveyResponse;
import com.logwiki.specialsurveyservice.domain.sseemitter.EmitterRepository;
import com.logwiki.specialsurveyservice.domain.survey.Survey;
import com.logwiki.specialsurveyservice.domain.survey.SurveyRepository;
import com.logwiki.specialsurveyservice.domain.surveycategory.SurveyCategoryType;
import com.logwiki.specialsurveyservice.domain.surveyresult.SurveyResult;
import com.logwiki.specialsurveyservice.domain.targetnumber.TargetNumber;
import com.logwiki.specialsurveyservice.domain.targetnumber.TargetNumberRepository;
import com.logwiki.specialsurveyservice.exception.BaseException;
import java.io.IOException;
import java.util.Map;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
@Slf4j
@Service
@RequiredArgsConstructor
public class SseService {

    private final EmitterRepository emitterRepository;
    private final SurveyRepository surveyRepository;
    private final TargetNumberRepository targetNumberRepository;

    private static final String LOSEPRODUCT = "꽝";

    public SseEmitter subscribe(Long randomNumber , Long survey_id,SseEmitter sseEmitter) {
        String id = survey_id + "_" + randomNumber + "_" + System.currentTimeMillis();
        sseEmitter = emitterRepository.save(id , sseEmitter);
        try {
            sseEmitter.send(SseEmitter.event()
                    .name("SSE연결")
                    .data("SSE연결성공")
            );
        }
        catch (IOException e) {
            throw new BaseException("Sse 구독 과정에서 IO 예외발생" , 7000);
        }

        sseEmitter.onCompletion(() -> {
            emitterRepository.deleteById(id);
        });
        sseEmitter.onTimeout(sseEmitter::complete);
        return sseEmitter;
    }

    public void refreshSurveyProbability(Long survey_id , String data) {
        Map<String, SseEmitter> targets = emitterRepository.findAllStartWithById(String.valueOf(survey_id));
        targets.forEach((key , value) -> {
            try {
                value.send(SseEmitter.event()
                        .id(String.valueOf(survey_id))
                        .name("확률변동")
                        .data(data));
            } catch (IOException e) {
                emitterRepository.deleteById(key);
//                throw new BaseException("Sse 이벤트 데이터(확률) 송신 과정에서 IO 예외발생" , 7001);
            } catch (IllegalStateException e) {
                emitterRepository.deleteById(key);
            }
        });
    }

    public void refreshSurveyFinisher(Long survey_id , SurveyAnswerResponse data) {
        Map<String, SseEmitter> targets = emitterRepository.findAllStartWithById(String.valueOf(survey_id));
        targets.forEach((key , value) -> {
            try {
                value.send(SseEmitter.event()
                        .id(String.valueOf(survey_id))
                        .name("응답인원추가")
                        .data(data));
            } catch (IOException e) {
                emitterRepository.deleteById(key);
//                throw new BaseException("Sse 이벤트 데이터(인원추가) 송신 과정에서 IO 예외발생" , 7001);
            } catch (IllegalStateException e) {
                emitterRepository.deleteById(key);
            }
        });
    }

    public void sendResultToSSE(Long surveyId, SurveyResult surveyResult, int submitOrder) {
        Survey targetSurvey = surveyRepository.findById(surveyId).get();
        SurveyResponse surveyResponse = SurveyResponse.from(targetSurvey);
        boolean resultSuccess = false;


        String giveawayName = LOSEPRODUCT;

        Optional<TargetNumber> targetNumber = targetNumberRepository.findFirstBySurveyAndNumber(
                targetSurvey,
                submitOrder);
        if (targetNumber.isPresent()) {
            giveawayName = targetNumber.get().getGiveaway().getName();
            resultSuccess = true;
        }

        if (targetSurvey.getSurveyCategory().getType().equals(SurveyCategoryType.NORMAL)) {
            refreshSurveyProbability(surveyResponse.getId(), String.valueOf(surveyResponse.getWinningPercent()));
            if (targetSurvey.isClosed() == false) {
                resultSuccess = false;
                giveawayName = LOSEPRODUCT;
            }

        }
        refreshSurveyFinisher(surveyResponse.getId(),
                SurveyAnswerResponse.from(surveyResult, giveawayName, resultSuccess));
    }

}