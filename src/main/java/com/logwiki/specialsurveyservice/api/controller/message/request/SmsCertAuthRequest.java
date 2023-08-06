package com.logwiki.specialsurveyservice.api.controller.message.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SmsCertAuthRequest {

    @NotEmpty(message = "휴대폰 번호는 필수입니다.")
    @Pattern(regexp = "^01(?:0|1|[6-9])[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$", message = "휴대폰 번호는 10~11자리의 숫자만 입력가능합니다.")
    private String phoneNumber;

    @NotEmpty(message = "인증번호는 필수 입력사항입니다.")
    private String certAuthCode;

    @Builder
    private SmsCertAuthRequest(String phoneNumber , String certAuthCode) {
        this.phoneNumber =phoneNumber;
        this.certAuthCode = certAuthCode;
    }

    public SmsCertAuthRequest toServiceRequest() {
        return SmsCertAuthRequest.builder()
                .phoneNumber(phoneNumber)
                .certAuthCode(certAuthCode)
                .build();
    }
}
