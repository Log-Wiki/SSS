package com.logwiki.specialsurveyservice.api.controller.sse;

import com.logwiki.specialsurveyservice.api.utils.ApiResponse;
import com.logwiki.specialsurveyservice.api.utils.ApiUtils;
import com.logwiki.specialsurveyservice.exception.BaseException;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.logwiki.specialsurveyservice.domain.sseemitters.SseConnectService;

@RestController
public class SseController {
    private final SseConnectService sseConnectService;

    public SseController(SseConnectService sseConnectService) {
        this.sseConnectService = sseConnectService;
    }
    @GetMapping(value = "/subscribe/{login_id}/{survey_id}" , produces= MediaType.TEXT_EVENT_STREAM_VALUE)
    public ApiResponse<SseEmitter> connect(@PathVariable Long login_id , @PathVariable Long survey_id) {
        SseEmitter sseEmitter = new SseEmitter();
        sseConnectService.add(login_id, survey_id,sseEmitter);

        return ApiUtils.success(sseEmitter);
    }
}
