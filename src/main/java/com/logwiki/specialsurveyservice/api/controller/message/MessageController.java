package com.logwiki.specialsurveyservice.api.controller.message;

import com.logwiki.specialsurveyservice.api.controller.message.request.SmsCertAuthRequest;
import com.logwiki.specialsurveyservice.api.controller.message.request.SmsCertSendRequest;
import com.logwiki.specialsurveyservice.api.service.message.MessageService;
import com.logwiki.specialsurveyservice.api.service.message.request.LongMessageSendServiceRequest;
import com.logwiki.specialsurveyservice.api.service.message.request.MultimediaMessageSendServiceRequest;
import com.logwiki.specialsurveyservice.api.service.message.request.ShortMessageSendServiceRequest;
import com.logwiki.specialsurveyservice.api.utils.ApiResponse;
import com.logwiki.specialsurveyservice.api.utils.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class MessageController {
    private static final String CERTREPLY = "인증문자 전송완료";
    private final MessageService messageService;

    @PostMapping("/message/sms")
    public ApiResponse<?> shortMessageSend(@RequestBody ShortMessageSendServiceRequest request, Authentication authentication) {
        return ApiUtils.success( messageService.sendSMS(request));
    }

    @PostMapping("/message/lms")
    public ApiResponse<?> longMessageSend(@RequestBody LongMessageSendServiceRequest request, Authentication authentication) {
        return ApiUtils.success( messageService.sendLMS(request));
    }

    @PostMapping("/message/mms")
    public ApiResponse<?> MultimediaMessageSend(@RequestBody MultimediaMessageSendServiceRequest request, Authentication authentication) {
        return ApiUtils.success( messageService.sendMMS(request));
    }


    @PostMapping ("/message/cert/signup/send")
    public ApiResponse<?> registCertSend(@RequestBody SmsCertSendRequest smsCertSendRequest) {
        messageService.sendSMS(messageService.makeCertMessage(smsCertSendRequest.getPhoneNumber()));
        return ApiUtils.success(CERTREPLY);
    }

    @PostMapping ("/message/cert/signup/auth")
    public ApiResponse<?> registCertAuth(@RequestBody SmsCertAuthRequest smsCertRequest) {
        return ApiUtils.success(messageService.checkCertAuthCode(smsCertRequest));
    }


}
