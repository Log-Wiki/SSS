package com.logwiki.specialsurveyservice.api.service.sse;

import com.logwiki.specialsurveyservice.domain.SseEmiters.EmitterRepository;
import com.logwiki.specialsurveyservice.exception.BaseException;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@Slf4j
@Service
public class SseConnectService {
    private final EmitterRepository emitterRepository;

    public SseConnectService(EmitterRepository emitterRepository) {
        this.emitterRepository = emitterRepository;
    }

    public SseEmitter add(Long login_id , Long survey_id,SseEmitter sseEmitter) {
        String id = survey_id + "_" + login_id + "_" + System.currentTimeMillis();
        log.info("sseEmitter add Event : addTo {} as {}" , survey_id, id);
        sseEmitter = emitterRepository.save(id , sseEmitter);
        log.info("sseEmitter now size : {}" , emitterRepository.sseEmitterMap.size());
        try {
            sseEmitter.send(SseEmitter.event()
                    .name("connect")
                    .data("connected!"));
        }
        catch (IOException e) {
            throw new BaseException("Sse 구독 과정에서 IO 예외발생" , 2040);
        }

        sseEmitter.onCompletion(() -> {
            log.info("sseEmitter onCompletion Event : remove {}" , id);
            emitterRepository.deleteById(id);
        });
        sseEmitter.onTimeout(() -> {
            log.info("sseEmitter onTimeout : complete {}" , id);
            emitterRepository.deleteById(id);
        });
        return sseEmitter;
    }



}
