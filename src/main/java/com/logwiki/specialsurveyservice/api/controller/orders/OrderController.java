package com.logwiki.specialsurveyservice.api.controller.orders;

import com.logwiki.specialsurveyservice.api.controller.orders.request.OrderCreateRequest;
import com.logwiki.specialsurveyservice.api.service.order.RegistOrderService;
import com.logwiki.specialsurveyservice.api.service.order.response.OrderResponse;
import com.logwiki.specialsurveyservice.api.utils.ApiResponse;
import com.logwiki.specialsurveyservice.api.utils.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class OrderController {

    private final RegistOrderService registOrderService;

    @PostMapping("/registOrder")
    public ApiResponse<OrderResponse> registOrder(
            @RequestBody OrderCreateRequest orderCreateRequest
    ) {
        return ApiUtils.success(registOrderService.regist(orderCreateRequest.toServiceRequest()));
    }
}
