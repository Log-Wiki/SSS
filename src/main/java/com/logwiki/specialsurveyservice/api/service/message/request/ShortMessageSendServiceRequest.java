package com.logwiki.specialsurveyservice.api.service.message.request;
import com.logwiki.specialsurveyservice.domain.message.Message;
import com.logwiki.specialsurveyservice.domain.message.MessageType;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ShortMessageSendServiceRequest {
    private MessageType type = MessageType.SHORTMESSAGE;
    private String from;
    private String content;
    List<Message> messages;

    @Builder
    ShortMessageSendServiceRequest(String from , String content , List<Message> messages ) {
        this.type = MessageType.SHORTMESSAGE;
        this.from = from;
        this.content = content;
        this.messages = messages;
    }




}
