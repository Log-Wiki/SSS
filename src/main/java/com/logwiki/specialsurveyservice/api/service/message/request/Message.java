package com.logwiki.specialsurveyservice.api.service.message.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Message {
    private String to;
    private String content;
    private String subject;
}
