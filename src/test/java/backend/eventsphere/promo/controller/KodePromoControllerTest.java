package backend.eventsphere.promo.controller;

import backend.eventsphere.auth.config.JwtUtil;
import backend.eventsphere.config.TestSecurityConfig;
import backend.eventsphere.promo.model.KodePromo;
import backend.eventsphere.promo.service.KodePromoService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(KodePromoController.class)
@Import(TestSecurityConfig.class)
public class KodePromoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KodePromoService promoService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public JwtUtil jwtUtil() {
            return Mockito.mock(JwtUtil.class);
        }
    }

    private KodePromo samplePercentagePromo() {
        return new KodePromo(
                UUID.randomUUID(),
                "PROMO10",
                BigDecimal.valueOf(0.1),
                KodePromo.DiscountType.PERCENTAGE,
                LocalDate.now(),
                LocalDate.now().plusDays(10),
                UUID.randomUUID(),
                UUID.randomUUID()
        );
    }

    private KodePromo sampleFixedAmountPromo() {
        return new KodePromo(
                UUID.randomUUID(),
                "DISKON20K",
                BigDecimal.valueOf(20000),
                KodePromo.DiscountType.FIXED_AMOUNT,
                LocalDate.now(),
                LocalDate.now().plusDays(10),
                UUID.randomUUID(),
                UUID.randomUUID()
        );
    }

    @Test
    void testCreatePercentagePromo() throws Exception {
        KodePromo promo = samplePercentagePromo();
        Mockito.when(promoService.createPercentagePromo(
                anyString(), any(), any(), any(), any(), any())
        ).thenReturn(promo);

        mockMvc.perform(post("/api/promos")
                        .param("code", promo.getCode())
                        .param("amount", promo.getDiscount().toString())
                        .param("promoType", "percentage")
                        .param("startDate", promo.getStartDate().toString())
                        .param("endDate", promo.getEndDate().toString())
                        .param("eventId", promo.getEventId().toString())
                        .param("createdBy", promo.getCreatedBy().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(promo.getCode()))
                .andExpect(jsonPath("$.discountType").value("PERCENTAGE"));
    }

    @Test
    void testCreateFixedAmountPromo() throws Exception {
        KodePromo promo = sampleFixedAmountPromo();
        Mockito.when(promoService.createFixedAmountPromo(
                anyString(), any(), any(), any(), any(), any())
        ).thenReturn(promo);

        mockMvc.perform(post("/api/promos")
                        .param("code", promo.getCode())
                        .param("amount", promo.getDiscount().toString())
                        .param("promoType", "fixed")
                        .param("startDate", promo.getStartDate().toString())
                        .param("endDate", promo.getEndDate().toString())
                        .param("eventId", promo.getEventId().toString())
                        .param("createdBy", promo.getCreatedBy().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(promo.getCode()))
                .andExpect(jsonPath("$.discountType").value("FIXED_AMOUNT"));
    }

    @Test
    void testCreatePromo_WithoutType() throws Exception {
        mockMvc.perform(post("/api/promos")
                        .param("code", "INVALID")
                        .param("amount", "0.1")
                        .param("startDate", LocalDate.now().toString())
                        .param("endDate", LocalDate.now().plusDays(1).toString())
                        .param("eventId", UUID.randomUUID().toString())
                        .param("createdBy", UUID.randomUUID().toString()))
                .andExpect(status().isBadRequest());
    }


    @Test
    void testGetPromoById_Found() throws Exception {
        KodePromo promo = samplePercentagePromo();
        Mockito.when(promoService.getPromoById(promo.getId()))
                .thenReturn(Optional.of(promo));

        mockMvc.perform(get("/api/promos/{id}", promo.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(promo.getCode()))
                .andExpect(jsonPath("$.discountType").value(promo.getDiscountType().name()));
    }

    @Test
    void testGetPromoById_NotFound() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.when(promoService.getPromoById(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/promos/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetPromosByEventId() throws Exception {
        UUID eventId = UUID.randomUUID();
        List<KodePromo> promos = List.of(samplePercentagePromo(), sampleFixedAmountPromo());

        Mockito.when(promoService.getPromosByEvent(eventId)).thenReturn(promos);

        mockMvc.perform(get("/api/promos/event/{eventId}", eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(promos.size()));
    }

    @Test
    void testGetActivePromosByEvent() throws Exception {
        UUID eventId = UUID.randomUUID();
        List<KodePromo> promos = List.of(sampleFixedAmountPromo());

        Mockito.when(promoService.getActivePromosByEvent(eventId)).thenReturn(promos);

        mockMvc.perform(get("/api/promos/active/event/{eventId}", eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(promos.size()));
    }

    @Test
    void testGetPromoByCode_Found() throws Exception {
        KodePromo promo = samplePercentagePromo();
        Mockito.when(promoService.getPromoByCode(promo.getCode()))
                .thenReturn(Optional.of(promo));

        mockMvc.perform(get("/api/promos/code/{code}", promo.getCode()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(promo.getCode()));
    }

    @Test
    void testGetPromoByCode_NotFound() throws Exception {
        Mockito.when(promoService.getPromoByCode("INVALID")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/promos/code/{code}", "INVALID"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdatePromo() throws Exception {
        KodePromo promo = samplePercentagePromo();
        Mockito.when(promoService.updatePromo(
                eq(promo.getId()),
                anyString(), any(BigDecimal.class), any(), any(), any())
        ).thenReturn(promo);

        mockMvc.perform(put("/api/promos/{id}", promo.getId())
                        .param("code", promo.getCode())
                        .param("discount", promo.getDiscount().toString())
                        .param("discountType", promo.getDiscountType().name())
                        .param("startDate", promo.getStartDate().toString())
                        .param("endDate", promo.getEndDate().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(promo.getCode()));
    }

    @Test
    void testUpdatePromo_PartialUpdate() throws Exception {
        KodePromo promo = samplePercentagePromo();
        Mockito.when(promoService.updatePromo(
                eq(promo.getId()),
                anyString(), any(BigDecimal.class), any(), any(), any())
        ).thenReturn(promo);

        mockMvc.perform(put("/api/promos/{id}", promo.getId())
                        .param("code", "NEWCODE")
                        .param("discount", "0.2"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeletePromo() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.doNothing().when(promoService).deletePromo(id);

        mockMvc.perform(delete("/api/promos/{id}", id))
                .andExpect(status().isNoContent());
    }
}