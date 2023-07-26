package com.logwiki.specialsurveyservice.api.controller.sse;

import com.logwiki.specialsurveyservice.api.service.sse.SseConnectService;
import com.logwiki.specialsurveyservice.api.utils.ApiResponse;
import com.logwiki.specialsurveyservice.api.utils.ApiUtils;
import com.logwiki.specialsurveyservice.exception.BaseException;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    @GetMapping(value = "/subscribe/{login_id}/{survey_id}" , produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(@PathVariable Long login_id , @PathVariable Long survey_id) {
        log.info("connect request");
        SseEmitter sseEmitter = new SseEmitter(60 * 1000L);
        sseConnectService.subscribe(login_id, survey_id,sseEmitter);
        return sseEmitter;
    }

    @GetMapping(value = "/updateTest", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public void updateTest(){
        sseConnectService.refreshSurveyProbability(335L,"67");
    }
}
