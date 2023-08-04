package com.logwiki.specialsurveyservice.domain.sseemitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Repository
public class EmitterRepository {
    public final Map<String , SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();

    public SseEmitter save(String id , SseEmitter sseEmitter) {
        sseEmitterMap.put(id , sseEmitter);
        return sseEmitter;
    }

    public Map<String , SseEmitter> findAllStartWithById(String id) {
        return sseEmitterMap.entrySet().stream().filter(entry -> entry.getKey().startsWith(id)).collect(
                Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void deleteById(String id) {
        sseEmitterMap.remove(id);
    }

    public void deleteAllStartWithById(String id) {
        sseEmitterMap.forEach((key , emitter) -> {
            if(key.startsWith(id)) {
                sseEmitterMap.remove(key);
            }
        });
    }
}