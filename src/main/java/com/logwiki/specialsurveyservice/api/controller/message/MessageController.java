package com.logwiki.specialsurveyservice.api.controller.message;

import com.logwiki.specialsurveyservice.api.service.message.MessageService;
import com.logwiki.specialsurveyservice.api.service.message.request.LongMessageSendServiceRequest;
import com.logwiki.specialsurveyservice.api.service.message.request.MultimediaMessageSendServiceRequest;
import com.logwiki.specialsurveyservice.api.service.message.request.ShortMessageSendServiceRequest;
import com.logwiki.specialsurveyservice.api.utils.ApiResponse;
import com.logwiki.specialsurveyservice.api.utils.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class MessageController {

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


}
