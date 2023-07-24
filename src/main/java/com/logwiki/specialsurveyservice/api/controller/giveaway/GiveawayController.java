package com.logwiki.specialsurveyservice.api.controller.giveaway;

import com.logwiki.specialsurveyservice.api.controller.giveaway.request.GiveawayDto;
import com.logwiki.specialsurveyservice.api.service.giveaway.GiveawayService;
import com.logwiki.specialsurveyservice.api.service.giveaway.response.GiveawayResponse;
import com.logwiki.specialsurveyservice.api.utils.ApiResponse;
import com.logwiki.specialsurveyservice.api.utils.ApiUtils;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class GiveawayController {

    private final GiveawayService giveawayService;

    @PostMapping("/giveaway/new")
    public ApiResponse<GiveawayResponse> createGiveaway(@Valid @RequestBody GiveawayDto giveawayDto) {
        return ApiUtils.success(giveawayService.createGiveaway(giveawayDto));
    }

    @GetMapping("/giveaway")
    public ApiResponse<List<GiveawayResponse>> getGiveaways() {
        return ApiUtils.success(giveawayService.getGiveaways());
    }
}
