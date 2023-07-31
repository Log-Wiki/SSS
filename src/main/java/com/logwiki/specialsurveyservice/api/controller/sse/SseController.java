package com.logwiki.specialsurveyservice.api.controller.sse;

import com.logwiki.specialsurveyservice.api.controller.sse.response.SurveyAnswerResponse;
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

    private static final long Timeout = 10 * 60 * 1000L;
    private static final int RandSize = 10000;
    public SseController(SseConnectService sseConnectService) {
        this.sseConnectService = sseConnectService;
    }
    @GetMapping(value = "/subscribe/{survey_id}" , produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(@PathVariable Long survey_id) {
        SseEmitter sseEmitter = new SseEmitter(Timeout);
        sseEmitter = sseConnectService.subscribe((long) (Math.random()*RandSize), survey_id,sseEmitter);
        return sseEmitter;
    }
}
