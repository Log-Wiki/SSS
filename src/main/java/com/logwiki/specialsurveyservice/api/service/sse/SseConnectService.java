package com.logwiki.specialsurveyservice.api.service.sse;
import com.logwiki.specialsurveyservice.api.service.sse.response.SurveyAnswerResponse;
import com.logwiki.specialsurveyservice.domain.sseemitter.EmitterRepository;
import com.logwiki.specialsurveyservice.exception.BaseException;
import java.io.IOException;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
@Slf4j
@Service
@RequiredArgsConstructor
public class SseConnectService {
    private final EmitterRepository emitterRepository;

    public SseEmitter subscribe(Long randomNumber , Long survey_id,SseEmitter sseEmitter) {
        String id = survey_id + "_" + randomNumber + "_" + System.currentTimeMillis();
        sseEmitter = emitterRepository.save(id , sseEmitter);
        try {
            sseEmitter.send(SseEmitter.event()
                    .name("connectName")
                    .data("connectedMessage" , MediaType.TEXT_EVENT_STREAM)
            );
        }
        catch (IOException e) {
            throw new BaseException("Sse 구독 과정에서 IO 예외발생" , 7000);
        }

        sseEmitter.onCompletion(() -> {
            emitterRepository.deleteById(id);
        });
        sseEmitter.onTimeout(() -> {
            emitterRepository.deleteById(id);
        });
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
                throw new BaseException("Sse 이벤트 데이터 송신 과정에서 IO 예외발생" , 7001);
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
                throw new BaseException("Sse 이벤트 데이터 송신 과정에서 IO 예외발생" , 7001);
            }
        });
    }



}