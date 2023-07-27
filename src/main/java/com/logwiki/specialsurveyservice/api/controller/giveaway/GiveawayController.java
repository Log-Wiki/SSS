package com.logwiki.specialsurveyservice.api.controller.giveaway;

import com.logwiki.specialsurveyservice.api.controller.giveaway.request.GiveawayRequest;
import com.logwiki.specialsurveyservice.api.service.giveaway.GiveawayService;
import com.logwiki.specialsurveyservice.api.service.giveaway.response.GiveawayResponse;
import com.logwiki.specialsurveyservice.api.utils.ApiResponse;
import com.logwiki.specialsurveyservice.api.utils.ApiUtils;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class GiveawayController {

    private final GiveawayService giveawayService;

    @PostMapping("/giveaway")
    public ApiResponse<GiveawayResponse> createGiveaway(@Valid @RequestBody GiveawayRequest giveawayRequest) {
        return ApiUtils.success(giveawayService.createGiveaway(giveawayRequest));
    }

    @GetMapping("/giveaway")
    public ApiResponse<List<GiveawayResponse>> getGiveaways() {
        return ApiUtils.success(giveawayService.getGiveaways());
    }

    @DeleteMapping("/giveaway/{id}")
    public ApiResponse<GiveawayResponse> deleteGiveaway(@PathVariable Long id) {
        return ApiUtils.success(giveawayService.deleteGiveaway(id));
    }

    @PatchMapping("/giveaway/{id}")
    public ApiResponse<GiveawayResponse> updateGiveaway(@PathVariable Long id, @Valid @RequestBody GiveawayRequest giveawayRequest) {
        return ApiUtils.success(giveawayService.updateGiveaway(id, giveawayRequest));
    }
}
