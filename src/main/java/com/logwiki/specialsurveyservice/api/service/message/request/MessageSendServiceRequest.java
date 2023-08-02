package com.logwiki.specialsurveyservice.api.service.message.request;

import com.logwiki.specialsurveyservice.domain.message.Message;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MessageSendServiceRequest {
    private String type;
    private String from;
    private String content;
    List<Message> messages;
    private String subject;
    List<String> files;

    @Builder
    MessageSendServiceRequest(String type , String from , String content , List<Message> messages , String subject , List<String> files) {
        this.type = type;
        this.from = from;
        this.content = content;
        this.messages = messages;
        this.subject = subject;
        this.files = files;
    }




}
