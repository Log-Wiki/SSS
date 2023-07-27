package com.logwiki.specialsurveyservice.api.controller.sse;

import com.logwiki.specialsurveyservice.api.controller.sse.response.SseUpdateInfo;
import com.logwiki.specialsurveyservice.api.service.sse.SseConnectService;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RestController
@RequestMapping("/api")
public class SseController {
    private final SseConnectService sseConnectService;

    public SseController(SseConnectService sseConnectService) {
        this.sseConnectService = sseConnectService;
    }
    @GetMapping(value = "/subscribe/{survey_id}" , produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(@PathVariable Long survey_id) {
        log.info("connect request");
        SseEmitter sseEmitter = new SseEmitter(10 * 60 * 1000L);
        sseEmitter = sseConnectService.subscribe((long) (Math.random()*10000), survey_id,sseEmitter);
        return sseEmitter;
    }

    @GetMapping(value = "/updateTest", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public void updateTest(){
        log.info("update");
        sseConnectService.refreshSurveyFinisher(335L,new SseUpdateInfo(LocalDateTime.now(),"toki","소금빵",true));
        sseConnectService.refreshSurveyProbability(335L,"67");
    }
}
