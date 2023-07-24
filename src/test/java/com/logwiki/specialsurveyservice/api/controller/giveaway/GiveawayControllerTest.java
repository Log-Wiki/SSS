package com.logwiki.specialsurveyservice.api.controller.giveaway;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.logwiki.specialsurveyservice.ControllerTestSupport;
import com.logwiki.specialsurveyservice.api.controller.giveaway.request.GiveawayDto;
import com.logwiki.specialsurveyservice.api.service.giveaway.response.GiveawayResponse;
import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

class GiveawayControllerTest extends ControllerTestSupport {

    @DisplayName("상품 타입, 상품 이름, 상품 가격을 이용하여 상품을 등록한다.")
    @WithMockUser
    @Test
    void createGiveaway() throws Exception {
        GiveawayType giveawayType = GiveawayType.COFFEE;
        String name = "스타벅스 아메리카노";
        int price = 4500;

        GiveawayResponse giveawayResponse = GiveawayResponse.builder()
                .giveawayType(giveawayType)
                .name(name)
                .price(price)
                .build();

        when(giveawayService.createGiveaway(any())).thenReturn(giveawayResponse);

        GiveawayDto request = GiveawayDto.builder()
                .giveawayType(giveawayType)
                .name(name)
                .price(price)
                .build();

        mockMvc.perform(
                post("/api/giveaway/new")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
        )
        .andDo(print())
        .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.response.giveawayType").value(giveawayType.name()))
                .andExpect(jsonPath("$.response.name").value(name))
                .andExpect(jsonPath("$.response.price").value(price));
    }

    @DisplayName("상품을 등록할 때 상품 타입은 필수값이다.")
    @WithMockUser
    @Test
    void createGiveawayWithoutGiveawayType() throws Exception {
        GiveawayDto request = GiveawayDto.builder()
                .giveawayType(null)
                .name("스타벅스 아메리카노")
                .price(4500)
                .build();

        mockMvc.perform(
                        post("/api/giveaway/new")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.apiError.message").value("상품 타입은 필수입니다."))
                .andExpect(jsonPath("$.apiError.status").value(1000));
    }

    @DisplayName("상품을 등록할 때 상품 이름은 필수값이다.")
    @WithMockUser
    @Test
    void createGiveawayWithoutName() throws Exception {
        GiveawayDto request = GiveawayDto.builder()
                .giveawayType(GiveawayType.COFFEE)
                .name(null)
                .price(4500)
                .build();

        mockMvc.perform(
                        post("/api/giveaway/new")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.apiError.message").value("상품 이름은 필수입니다."))
                .andExpect(jsonPath("$.apiError.status").value(1000));
    }

    @DisplayName("상품을 등록할 때 상품 가격은 필수값이다.")
    @WithMockUser
    @Test
    void createGiveawayWithoutPrice() throws Exception {
        GiveawayDto request = GiveawayDto.builder()
                .giveawayType(GiveawayType.COFFEE)
                .name("스타벅스 아메리카노")
                .build();

        mockMvc.perform(
                        post("/api/giveaway/new")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.apiError.message").value("상품 가격은 필수(양수)입니다."))
                .andExpect(jsonPath("$.apiError.status").value(1000));
    }

    @DisplayName("상품을 등록할 때 상품 가격은 양수여야 한다.")
    @WithMockUser
    @Test
    void createGiveawayWithNotValidPrice() throws Exception {
        GiveawayDto request = GiveawayDto.builder()
                .giveawayType(GiveawayType.COFFEE)
                .name("스타벅스 아메리카노")
                .price(0)
                .build();

        mockMvc.perform(
                        post("/api/giveaway/new")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.apiError.message").value("상품 가격은 필수(양수)입니다."))
                .andExpect(jsonPath("$.apiError.status").value(1000));
    }
}