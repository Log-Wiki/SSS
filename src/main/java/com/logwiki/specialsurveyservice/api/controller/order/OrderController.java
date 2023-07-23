package com.logwiki.specialsurveyservice.api.controller.order;

import com.logwiki.specialsurveyservice.api.controller.order.request.OrderCreateRequest;
import com.logwiki.specialsurveyservice.api.service.order.RegistOrderService;
import com.logwiki.specialsurveyservice.api.service.order.response.OrderResponse;
import com.logwiki.specialsurveyservice.api.utils.ApiResponse;
import com.logwiki.specialsurveyservice.api.utils.ApiUtils;
import jakarta.validation.Valid;
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
            @Valid @RequestBody OrderCreateRequest orderCreateRequest
    ) {
        // 검증 추가?
        // 들어온 가격이랑 물건수 * 물건가격이 같은지 확인
        return ApiUtils.success(registOrderService.regist(orderCreateRequest.toServiceRequest()));
    }
}
