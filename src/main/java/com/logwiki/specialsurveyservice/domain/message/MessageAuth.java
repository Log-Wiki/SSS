package com.logwiki.specialsurveyservice.domain.message;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@Setter
@RedisHash(value = "messageAuth", timeToLive = 60 * 5)
@NoArgsConstructor
@AllArgsConstructor
public class MessageAuth {
    @Id
    private String id;
    @Indexed
    private String phoneNumber;
    private String certAuthCode;

    @Builder
    public MessageAuth(String phoneNumber , String certAuthCode) {
        this.phoneNumber = phoneNumber;
        this.certAuthCode = certAuthCode;
    }

}
