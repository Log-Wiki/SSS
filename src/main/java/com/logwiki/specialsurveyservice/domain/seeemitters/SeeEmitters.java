package com.logwiki.specialsurveyservice.domain.seeemitters;

import jakarta.persistence.Entity;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@Slf4j
@Component
public class SeeEmitters {
    private final List<SseEmitter> sseEmitterList = new CopyOnWriteArrayList<>();

    SseEmitter add(SseEmitter sseEmitter) {
        log.info("sseEmitter add Event : add {}" , sseEmitter);
        log.info("sseEmitter now size : {}" , sseEmitterList.size());
        this.sseEmitterList.add(sseEmitter);
        sseEmitter.onCompletion(() -> {
            log.info("sseEmitter onCompletion Event : remove");
            sseEmitterList.remove(sseEmitter);
        });
        sseEmitter.onTimeout(() -> {
            log.info("sseEmitter onTimeout : complete" , sseEmitter);
            sseEmitter.complete();
        });
        return sseEmitter;
    }

}
