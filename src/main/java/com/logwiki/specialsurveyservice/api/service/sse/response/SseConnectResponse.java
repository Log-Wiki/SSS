package com.logwiki.specialsurveyservice.api.service.sse.response;

import com.logwiki.specialsurveyservice.domain.account.Account;
import com.logwiki.specialsurveyservice.domain.gender.Gender;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Getter
public class SseConnectResponse {
    SseEmitter sseEmitter;

    @Builder
    private SseConnectResponse(String email, Gender gender, String name, String phoneNumber,
            LocalDate birthday) {
        this.email = email;
        this.gender = gender;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.birthday = birthday;
    }

    public static SseConnectResponse from(Account account) {
      if (account == null) {
        return null;
      }

        return SseConnectResponse.builder()
                .email(account.getEmail())
                .gender(account.getGender())
                .name(account.getName())
                .phoneNumber(account.getPhoneNumber())
                .birthday(account.getBirthday())
                .build();
    }
}
