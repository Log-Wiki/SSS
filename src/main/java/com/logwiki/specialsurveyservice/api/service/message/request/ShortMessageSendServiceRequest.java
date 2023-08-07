package com.logwiki.specialsurveyservice.api.service.message.request;
import com.logwiki.specialsurveyservice.domain.message.Message;
import com.logwiki.specialsurveyservice.domain.message.MessageType;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@NoArgsConstructor
@Getter
public class ShortMessageSendServiceRequest {
    private MessageType type = MessageType.SHORTMESSAGE;
    @Value("${sender.phone-number}")
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
