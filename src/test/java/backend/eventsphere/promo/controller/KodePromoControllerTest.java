// package backend.eventsphere.promo.controller;

// import backend.eventsphere.auth.config.JwtUtil;
// import backend.eventsphere.config.TestSecurityConfig;
// import backend.eventsphere.promo.model.KodePromo;
// import backend.eventsphere.promo.service.KodePromoService;
// import org.junit.jupiter.api.Test;
// import org.mockito.Mockito;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.context.TestConfiguration;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Import;
// import org.springframework.context.annotation.Primary;
// import org.springframework.test.web.servlet.MockMvc;

// import java.math.BigDecimal;
// import java.time.LocalDate;
// import java.time.format.DateTimeFormatter;
// import java.util.*;
// import java.util.concurrent.CompletableFuture;

// import static org.mockito.ArgumentMatchers.*;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @WebMvcTest(KodePromoController.class)
// @Import(TestSecurityConfig.class)
// public class KodePromoControllerTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @MockBean
//     private KodePromoService promoService;

//     @TestConfiguration
//     static class TestConfig {
//         @Bean
//         @Primary
//         public JwtUtil jwtUtil() {
//             return Mockito.mock(JwtUtil.class);
//         }
//     }

//     private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

//     private KodePromo samplePercentagePromo() {
//         return KodePromo.builder()
//                 .id(UUID.randomUUID())
//                 .code("PROMO10")
//                 .discount(BigDecimal.valueOf(0.1))
//                 .discountType(KodePromo.DiscountType.PERCENTAGE)
//                 .startDate(LocalDate.now())
//                 .endDate(LocalDate.now().plusDays(10))
//                 .eventId(UUID.randomUUID())
//                 .createdBy(UUID.randomUUID())
//                 .build();
//     }

//     private KodePromo sampleFixedAmountPromo() {
//         return KodePromo.builder()
//                 .id(UUID.randomUUID())
//                 .code("DISKON20K")
//                 .discount(BigDecimal.valueOf(20000))
//                 .discountType(KodePromo.DiscountType.FIXED_AMOUNT)
//                 .startDate(LocalDate.now())
//                 .endDate(LocalDate.now().plusDays(10))
//                 .eventId(UUID.randomUUID())
//                 .createdBy(UUID.randomUUID())
//                 .build();
//     }

//     @Test
//     void testCreatePercentagePromo() throws Exception {
//         KodePromo promo = samplePercentagePromo();
//         Mockito.when(promoService.createPercentagePromo(
//                 anyString(), any(), any(), any(), any(), any())
//         ).thenReturn(CompletableFuture.completedFuture(promo));

//         mockMvc.perform(post("/api/promos")
//                         .param("code", promo.getCode())
//                         .param("amount", "10")
//                         .param("promoType", "percentage")
//                         .param("startDate", promo.getStartDate().format(formatter))
//                         .param("endDate", promo.getEndDate().format(formatter))
//                         .param("eventId", promo.getEventId().toString())
//                         .param("createdBy", promo.getCreatedBy().toString()))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.code").value(promo.getCode()))
//                 .andExpect(jsonPath("$.discountType").value("PERCENTAGE"))
//                 .andExpect(jsonPath("$.discount").value(0.1));
//     }

//     @Test
//     void testCreateFixedAmountPromo() throws Exception {
//         KodePromo promo = sampleFixedAmountPromo();
//         Mockito.when(promoService.createFixedAmountPromo(
//                 anyString(), any(), any(), any(), any(), any())
//         ).thenReturn(CompletableFuture.completedFuture(promo));

//         mockMvc.perform(post("/api/promos")
//                         .param("code", promo.getCode())
//                         .param("amount", "20000")
//                         .param("promoType", "fixed_amount")
//                         .param("startDate", promo.getStartDate().format(formatter))
//                         .param("endDate", promo.getEndDate().format(formatter))
//                         .param("eventId", promo.getEventId().toString())
//                         .param("createdBy", promo.getCreatedBy().toString()))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.code").value(promo.getCode()))
//                 .andExpect(jsonPath("$.discountType").value("FIXED_AMOUNT"))
//                 .andExpect(jsonPath("$.discount").value(20000));
//     }

//     @Test
//     void testCreatePromo_InvalidType() throws Exception {
//         mockMvc.perform(post("/api/promos")
//                         .param("code", "INVALID")
//                         .param("amount", "10")
//                         .param("promoType", "invalid_type")
//                         .param("startDate", LocalDate.now().format(formatter))
//                         .param("endDate", LocalDate.now().plusDays(1).format(formatter))
//                         .param("eventId", UUID.randomUUID().toString())
//                         .param("createdBy", UUID.randomUUID().toString()))
//                 .andExpect(status().isBadRequest());
//     }

//     @Test
//     void testCreatePromo_MissingRequiredFields() throws Exception {
//         mockMvc.perform(post("/api/promos")
//                         .param("code", "TEST")
//                         .param("amount", "10")
//                         .param("promoType", "percentage"))
//                 .andExpect(status().isBadRequest());
//     }

//     @Test
//     void testGetPromoById_Found() throws Exception {
//         KodePromo promo = samplePercentagePromo();
//         Mockito.when(promoService.getPromoById(promo.getId()))
//                 .thenReturn(Optional.of(promo));

//         mockMvc.perform(get("/api/promos/{id}", promo.getId()))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.id").value(promo.getId().toString()))
//                 .andExpect(jsonPath("$.code").value(promo.getCode()));
//     }

//     @Test
//     void testGetPromoById_NotFound() throws Exception {
//         UUID id = UUID.randomUUID();
//         Mockito.when(promoService.getPromoById(id)).thenReturn(Optional.empty());

//         mockMvc.perform(get("/api/promos/{id}", id))
//                 .andExpect(status().isNotFound());
//     }

//     @Test
//     void testGetPromosByEventId() throws Exception {
//         UUID eventId = UUID.randomUUID();
//         List<KodePromo> promos = Arrays.asList(
//                 samplePercentagePromo(),
//                 sampleFixedAmountPromo()
//         );

//         Mockito.when(promoService.getPromosByEvent(eventId)).thenReturn(promos);

//         mockMvc.perform(get("/api/promos/event/{eventId}", eventId))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.length()").value(promos.size()));
//     }

//     @Test
//     void testGetActivePromosByEvent() throws Exception {
//         UUID eventId = UUID.randomUUID();
//         List<KodePromo> promos = List.of(sampleFixedAmountPromo());

//         Mockito.when(promoService.getActivePromosByEvent(eventId)).thenReturn(promos);

//         mockMvc.perform(get("/api/promos/active/event/{eventId}", eventId))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.length()").value(promos.size()));
//     }

//     @Test
//     void testGetPromoByCode_Found() throws Exception {
//         KodePromo promo = samplePercentagePromo();
//         Mockito.when(promoService.getPromoByCode(promo.getCode()))
//                 .thenReturn(Optional.of(promo));

//         mockMvc.perform(get("/api/promos/code/{code}", promo.getCode()))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.code").value(promo.getCode()));
//     }

//     @Test
//     void testGetPromoByCode_NotFound() throws Exception {
//         Mockito.when(promoService.getPromoByCode("NON_EXISTENT")).thenReturn(Optional.empty());

//         mockMvc.perform(get("/api/promos/code/{code}", "NON_EXISTENT"))
//                 .andExpect(status().isNotFound());
//     }

//     @Test
//     void testUpdatePromo_Success() throws Exception {
//         KodePromo promo = samplePercentagePromo();
//         KodePromo updated = KodePromo.builder()
//                 .id(promo.getId())
//                 .code("UPDATED_CODE")
//                 .discount(BigDecimal.valueOf(0.2))
//                 .discountType(KodePromo.DiscountType.PERCENTAGE)
//                 .startDate(promo.getStartDate())
//                 .endDate(promo.getEndDate())
//                 .eventId(promo.getEventId())
//                 .createdBy(promo.getCreatedBy())
//                 .build();

//         Mockito.when(promoService.updatePromo(
//                 eq(promo.getId()),
//                 anyString(), any(), any(), any(), any())
//         ).thenReturn(CompletableFuture.completedFuture(updated));

//         mockMvc.perform(put("/api/promos/{id}", promo.getId())
//                         .param("code", updated.getCode())
//                         .param("discount", "0.2")
//                         .param("discountType", "PERCENTAGE")
//                         .param("startDate", updated.getStartDate().format(formatter))
//                         .param("endDate", updated.getEndDate().format(formatter)))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.code").value(updated.getCode()))
//                 .andExpect(jsonPath("$.discount").value(0.2));
//     }

//     @Test
//     void testUpdatePromo_PartialUpdate() throws Exception {
//         KodePromo promo = samplePercentagePromo();
//         KodePromo updated = KodePromo.builder()
//                 .id(promo.getId())
//                 .code("PARTIAL_UPDATE")
//                 .discount(promo.getDiscount())
//                 .discountType(promo.getDiscountType())
//                 .startDate(promo.getStartDate())
//                 .endDate(promo.getEndDate())
//                 .eventId(promo.getEventId())
//                 .createdBy(promo.getCreatedBy())
//                 .build();

//         Mockito.when(promoService.updatePromo(
//                 eq(promo.getId()),
//                 anyString(), isNull(), isNull(), isNull(), isNull())
//         ).thenReturn(CompletableFuture.completedFuture(updated));

//         mockMvc.perform(put("/api/promos/{id}", promo.getId())
//                         .param("code", "PARTIAL_UPDATE"))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.code").value("PARTIAL_UPDATE"));
//     }

//     @Test
//     void testUpdatePromo_NotFound() throws Exception {
//         UUID id = UUID.randomUUID();
//         Mockito.when(promoService.updatePromo(
//                 eq(id), any(), any(), any(), any(), any())
//         ).thenReturn(CompletableFuture.failedFuture(new IllegalArgumentException("Promo not found")));

//         mockMvc.perform(put("/api/promos/{id}", id)
//                         .param("code", "TEST")
//                         .param("discount", "0.1")
//                         .param("discountType", "PERCENTAGE")
//                         .param("startDate", LocalDate.now().format(formatter))
//                         .param("endDate", LocalDate.now().plusDays(1).format(formatter)))
//                 .andExpect(status().isBadRequest());
//     }

//     @Test
//     void testDeletePromo_Success() throws Exception {
//         UUID id = UUID.randomUUID();
//         Mockito.doNothing().when(promoService).deletePromo(id);

//         mockMvc.perform(delete("/api/promos/{id}", id))
//                 .andExpect(status().isNoContent());
//     }

//     @Test
//     void testDeletePromo_NotFound() throws Exception {
//         UUID id = UUID.randomUUID();
//         Mockito.doThrow(new IllegalArgumentException("Promo not found"))
//                 .when(promoService).deletePromo(id);

//         mockMvc.perform(delete("/api/promos/{id}", id))
//                 .andExpect(status().isNotFound());
//     }

//     @Test
//     void testGetAllPromos() throws Exception {
//         List<KodePromo> promoList = Arrays.asList(samplePercentagePromo(), sampleFixedAmountPromo());
//         Mockito.when(promoService.getAllPromos()).thenReturn(promoList);

//         mockMvc.perform(get("/api/promos"))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.length()").value(promoList.size()))
//                 .andExpect(jsonPath("$[0].code").value(promoList.get(0).getCode()))
//                 .andExpect(jsonPath("$[1].code").value(promoList.get(1).getCode()));
//     }

// }