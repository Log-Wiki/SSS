package com.logwiki.specialsurveyservice.api.controller.survey;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class SurveyController {

    @PostMapping("/survey")
    public String surveyAdd() {
        return "";
    }
}
