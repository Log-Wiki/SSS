package com.logwiki.specialsurveyservice.domain.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@lombok.Generated
public class Message {
    private String to;
    private String content;
    private String subject;


    @Builder
    public Message(String to, String content, String subject) {
        this.to = to;
        this.content = content;
        this.subject = subject;
    }
}
