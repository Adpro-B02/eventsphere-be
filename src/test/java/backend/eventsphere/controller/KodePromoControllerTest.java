package backend.eventsphere.controller;

import backend.eventsphere.model.KodePromo;
import backend.eventsphere.service.KodePromoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(KodePromoController.class)
public class KodePromoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KodePromoService promoService;

    @Autowired
    private ObjectMapper objectMapper;

    private KodePromo samplePromo() {
        return new KodePromo(
                UUID.randomUUID(),
                "PROMO10",
                BigDecimal.valueOf(0.1),
                LocalDate.now(),
                LocalDate.now().plusDays(10),
                UUID.randomUUID(),
                UUID.randomUUID()
        );
    }

    @Test
    void testCreatePromo() throws Exception {
        KodePromo promo = samplePromo();
        Mockito.when(promoService.createPromo(
                anyString(), any(BigDecimal.class), any(), any(), any(), any())
        ).thenReturn(promo);

        mockMvc.perform(post("/api/promos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(promo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(promo.getCode()));
    }

    @Test
    void testGetPromoById_Found() throws Exception {
        KodePromo promo = samplePromo();
        Mockito.when(promoService.getPromoById(promo.getId()))
                .thenReturn(Optional.of(promo));

        mockMvc.perform(get("/api/promos/{id}", promo.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(promo.getCode()));
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
        List<KodePromo> promos = List.of(samplePromo(), samplePromo());

        Mockito.when(promoService.getPromosByEvent(eventId)).thenReturn(promos);

        mockMvc.perform(get("/api/promos/event/{eventId}", eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(promos.size()));
    }

    @Test
    void testGetPromoByCode_Found() throws Exception {
        KodePromo promo = samplePromo();
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
        KodePromo promo = samplePromo();
        Mockito.when(promoService.updatePromo(
                        eq(promo.getId()),
                        anyString(), any(BigDecimal.class), any(), any()))
                .thenReturn(promo);

        mockMvc.perform(put("/api/promos/{id}", promo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(promo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(promo.getCode()));
    }

    @Test
    void testDeletePromo() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.doNothing().when(promoService).deletePromo(id);

        mockMvc.perform(delete("/api/promos/{id}", id))
                .andExpect(status().isNoContent());
    }
}