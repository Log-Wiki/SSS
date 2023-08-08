package com.logwiki.specialsurveyservice.api.controller.sse;

import com.logwiki.specialsurveyservice.api.service.sse.SseService;
import jakarta.servlet.http.HttpServletResponse;
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
    private final SseService sseService;

    private static final long TIMEOUT = 10 * 60 * 1000L;
    private static final int RANDSIZE = 10000;
    public SseController(SseService sseService) {
        this.sseService = sseService;
    }
    @GetMapping(value = "/subscribe/{survey_id}" , produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(@PathVariable Long survey_id , HttpServletResponse response) {
        response.setHeader("X-Accel-Buffering", "no");
        SseEmitter sseEmitter = new SseEmitter(TIMEOUT);
        sseEmitter = sseService.subscribe((long) (Math.random()* RANDSIZE), survey_id,sseEmitter);
        return sseEmitter;
    }
}