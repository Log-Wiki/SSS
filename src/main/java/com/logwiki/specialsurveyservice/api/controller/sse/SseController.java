package com.logwiki.specialsurveyservice.api.controller.sse;

import com.logwiki.specialsurveyservice.domain.seeemitters.SeeEmitters;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SseController {
    private final SeeEmitters seeEmitters;

    public SseController(SeeEmitters seeEmitters) {
        this.seeEmitters = seeEmitters;
    }


}
