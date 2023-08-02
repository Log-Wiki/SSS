package com.logwiki.specialsurveyservice.api.service.message.request;

import com.logwiki.specialsurveyservice.domain.message.Message;
import com.logwiki.specialsurveyservice.domain.message.MessageType;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class LongMessageSendServiceRequest {
    private MessageType type;
    private String from;
    private String content;
    List<Message> messages;
    private String subject;
    List<String> files;

    @Builder
    LongMessageSendServiceRequest( String from , String content , List<Message> messages , String subject , List<String> files) {
        this.type = MessageType.LONGMESSAGE;
        this.from = from;
        this.content = content;
        this.messages = messages;
        this.subject = subject;
        this.files = files;
    }




}
