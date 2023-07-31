package com.logwiki.specialsurveyservice.api.controller.orders;

import com.logwiki.specialsurveyservice.api.controller.orders.request.OrderCreateRequest;
import com.logwiki.specialsurveyservice.api.service.order.RegistOrderService;
import com.logwiki.specialsurveyservice.api.service.order.response.OrderResponse;
import com.logwiki.specialsurveyservice.api.utils.ApiResponse;
import com.logwiki.specialsurveyservice.api.utils.ApiUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class OrderController {

    private final RegistOrderService registOrderService;

    @PostMapping("/order/regist/{userId}")
    public ApiResponse<OrderResponse> registOrder(
            @RequestBody @Valid OrderCreateRequest orderCreateRequest,
            @PathVariable String userId
    ) {
        return ApiUtils.success(registOrderService.createOrder(orderCreateRequest.toServiceRequest(userId,System.currentTimeMillis())));
    }
}
