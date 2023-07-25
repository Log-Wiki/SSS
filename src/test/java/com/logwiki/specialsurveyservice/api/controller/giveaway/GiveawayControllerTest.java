package com.logwiki.specialsurveyservice.api.controller.giveaway;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.logwiki.specialsurveyservice.ControllerTestSupport;
import com.logwiki.specialsurveyservice.api.controller.giveaway.request.GiveawayRequest;
import com.logwiki.specialsurveyservice.api.service.giveaway.response.GiveawayResponse;
import com.logwiki.specialsurveyservice.domain.giveaway.GiveawayType;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

class GiveawayControllerTest extends ControllerTestSupport {

    @DisplayName("상품 타입, 상품 이름, 상품 가격을 이용하여 상품을 등록한다.")
    @WithMockUser
    @Test
    void createGiveaway() throws Exception {
        // given
        Long id = 1L;
        GiveawayType giveawayType = GiveawayType.COFFEE;
        String name = "스타벅스 아메리카노";
        int price = 4500;

        GiveawayResponse giveawayResponse = getGiveawayResponse(id, giveawayType, name, price);

        when(giveawayService.createGiveaway(any())).thenReturn(giveawayResponse);

        GiveawayRequest request = GiveawayRequest.builder()
                .giveawayType(giveawayType)
                .name(name)
                .price(price)
                .build();

        // when // then
        mockMvc.perform(
                post("/api/giveaway")
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
        // given
        GiveawayRequest request = GiveawayRequest.builder()
                .giveawayType(null)
                .name("스타벅스 아메리카노")
                .price(4500)
                .build();

        // when // then
        mockMvc.perform(
                        post("/api/giveaway")
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
        // given
        GiveawayRequest request = GiveawayRequest.builder()
                .giveawayType(GiveawayType.COFFEE)
                .name(null)
                .price(4500)
                .build();

        // when // then
        mockMvc.perform(
                        post("/api/giveaway")
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
        // given
        GiveawayRequest request = GiveawayRequest.builder()
                .giveawayType(GiveawayType.COFFEE)
                .name("스타벅스 아메리카노")
                .build();

        // when // then
        mockMvc.perform(
                        post("/api/giveaway")
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
        // given
        GiveawayRequest request = GiveawayRequest.builder()
                .giveawayType(GiveawayType.COFFEE)
                .name("스타벅스 아메리카노")
                .price(0)
                .build();

        // when // then
        mockMvc.perform(
                        post("/api/giveaway")
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

    @DisplayName("등록된 상품을 조회한다.")
    @WithMockUser
    @Test
    void getGiveaways() throws Exception {
        // given
        GiveawayResponse giveawayResponse1 = getGiveawayResponse(1L, GiveawayType.COFFEE, "스타벅스 아메리카노", 4500);
        GiveawayResponse giveawayResponse2 = getGiveawayResponse(2L, GiveawayType.COFFEE, "컴포즈 아메리카노", 1500);
        GiveawayResponse giveawayResponse3 = getGiveawayResponse(3L, GiveawayType.COFFEE, "BHC 뿌링클", 20_000);
        List<GiveawayResponse> giveawaysResponse = List.of(giveawayResponse1, giveawayResponse2, giveawayResponse3);

        when(giveawayService.getGiveaways()).thenReturn(giveawaysResponse);

        // when // then
        mockMvc.perform(
                        get("/api/giveaway")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.response[0].giveawayType").value(giveawayResponse1.getGiveawayType().name()))
                .andExpect(jsonPath("$.response[0].name").value(giveawayResponse1.getName()))
                .andExpect(jsonPath("$.response[0].price").value(giveawayResponse1.getPrice()))
                .andExpect(jsonPath("$.response[1].giveawayType").value(giveawayResponse2.getGiveawayType().name()))
                .andExpect(jsonPath("$.response[1].name").value(giveawayResponse2.getName()))
                .andExpect(jsonPath("$.response[1].price").value(giveawayResponse2.getPrice()))
                .andExpect(jsonPath("$.response[2].giveawayType").value(giveawayResponse3.getGiveawayType().name()))
                .andExpect(jsonPath("$.response[2].name").value(giveawayResponse3.getName()))
                .andExpect(jsonPath("$.response[2].price").value(giveawayResponse3.getPrice()));
    }

    @DisplayName("상품 삭제를 성공하면 삭제된 상품의 정보를 받는다.")
    @WithMockUser
    @Test
    void deleteGiveaway() throws Exception {
        // given
        Long id = 1L;
        GiveawayType giveawayType = GiveawayType.COFFEE;
        String name = "스타벅스 아메리카노";
        int price = 4500;

        GiveawayResponse giveawayResponse = getGiveawayResponse(id, giveawayType, name, price);
        when(giveawayService.deleteGiveaway(any())).thenReturn(giveawayResponse);

        // when // then
        mockMvc.perform(
                        delete("/api/giveaway/{id}", id)
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

    @DisplayName("상품 수정을 성공하면 수정된 상품의 정보를 받는다.")
    @WithMockUser
    @Test
    void updateGiveaway() throws Exception {
        // given
        Long id = 1L;
        GiveawayType giveawayType = GiveawayType.COFFEE;
        String name = "스타벅스 아메리카노";
        int price = 4500;

        GiveawayRequest request = GiveawayRequest.builder()
                .giveawayType(giveawayType)
                .name(name)
                .price(price)
                .build();

        GiveawayResponse giveawayResponse = getGiveawayResponse(id, giveawayType, name, price);
        when(giveawayService.updateGiveaway(any(), any())).thenReturn(giveawayResponse);

        // when // then
        mockMvc.perform(
                        put("/api/giveaway/{id}", id)
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

    @DisplayName("상품을 수정할 때 상품 타입은 필수값이다.")
    @WithMockUser
    @Test
    void updateGiveawayWithoutGiveawayType() throws Exception {
        // given
        GiveawayRequest request = GiveawayRequest.builder()
                .giveawayType(null)
                .name("스타벅스 아메리카노")
                .price(4500)
                .build();

        // when // then
        mockMvc.perform(
                        put("/api/giveaway/{id}", 1)
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

    @DisplayName("상품을 수정할 때 상품 이름은 필수값이다.")
    @WithMockUser
    @Test
    void updateGiveawayWithoutName() throws Exception {
        // given
        GiveawayRequest request = GiveawayRequest.builder()
                .giveawayType(GiveawayType.COFFEE)
                .name(null)
                .price(4500)
                .build();

        // when // then
        mockMvc.perform(
                        put("/api/giveaway/{id}", 1)
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

    @DisplayName("상품을 수정할 때 상품 가격은 필수값이다.")
    @WithMockUser
    @Test
    void updateGiveawayWithoutPrice() throws Exception {
        // given
        GiveawayRequest request = GiveawayRequest.builder()
                .giveawayType(GiveawayType.COFFEE)
                .name("스타벅스 아메리카노")
                .build();

        // when // then
        mockMvc.perform(
                        put("/api/giveaway/{id}", 1)
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

    @DisplayName("상품을 수정할 때 상품 가격은 양수여야 한다.")
    @WithMockUser
    @Test
    void updateGiveawayWithNotValidPrice() throws Exception {
        // given
        GiveawayRequest request = GiveawayRequest.builder()
                .giveawayType(GiveawayType.COFFEE)
                .name("스타벅스 아메리카노")
                .price(0)
                .build();

        // when // then
        mockMvc.perform(
                        put("/api/giveaway/{id}", 1)
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

    private static GiveawayResponse getGiveawayResponse(Long id, GiveawayType giveawayType, String name, int price) {
        return GiveawayResponse.builder()
                .id(id)
                .giveawayType(giveawayType)
                .name(name)
                .price(price)
                .build();
    }
}