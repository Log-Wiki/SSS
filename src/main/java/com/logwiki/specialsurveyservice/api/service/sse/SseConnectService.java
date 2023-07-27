package com.logwiki.specialsurveyservice.api.service.sse;

import com.logwiki.specialsurveyservice.api.controller.sse.response.SseUpdateInfo;
import com.logwiki.specialsurveyservice.domain.SseEmiters.EmitterRepository;
import com.logwiki.specialsurveyservice.exception.BaseException;
import java.io.IOException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@Slf4j
@Service
public class SseConnectService {
    private final EmitterRepository emitterRepository;

    public SseConnectService(EmitterRepository emitterRepository) {
        this.emitterRepository = emitterRepository;
    }

    public SseEmitter subscribe(Long login_id , Long survey_id,SseEmitter sseEmitter) {
        String id = survey_id + "_" + login_id + "_" + System.currentTimeMillis();
        sseEmitter = emitterRepository.save(id , sseEmitter);
        log.info("timeout setting : {}" , sseEmitter.getTimeout());
        try {
            sseEmitter.send(SseEmitter.event()
                    .name("connectName1")
                    .data("connectedMessage1" , MediaType.TEXT_EVENT_STREAM)
            );
        }
        catch (IOException e) {
            throw new BaseException("Sse 구독 과정에서 IO 예외발생" , 2040);
        }

        sseEmitter.onCompletion(() -> {
            log.info("SseEmitter Completion {}" , id);
            emitterRepository.deleteById(id);
        });
        sseEmitter.onTimeout(() -> {
            log.info("SseEmitter Timeout {}" , id);
            emitterRepository.deleteById(id);
        });
        return sseEmitter;
    }

    public void refreshSurveyProbability(Long survey_id , String data) {
        Map<String, SseEmitter> targets = emitterRepository.findAllStartWithById(String.valueOf(survey_id));
        targets.forEach((key , value) -> {
            log.info(key);
            try {
                value.send(SseEmitter.event()
                        .id(String.valueOf(survey_id))
                        .name("확률변동")
                        .data(data));
            } catch (IOException e) {
                throw new BaseException("Sse 이벤트 데이터 송신 과정에서 IO 예외발생" , 2041);
            }
        });
    }

    public void refreshSurveyFinisher(Long survey_id , SseUpdateInfo data) {
        Map<String, SseEmitter> targets = emitterRepository.findAllStartWithById(String.valueOf(survey_id));
        targets.forEach((key , value) -> {
            try {
                value.send(SseEmitter.event()
                        .id(String.valueOf(survey_id))
                        .name("응답인원추가")
                        .data(data));
            } catch (IOException e) {
                throw new BaseException("Sse 이벤트 데이터 송신 과정에서 IO 예외발생" , 2041);
            }
        });
    }



}
