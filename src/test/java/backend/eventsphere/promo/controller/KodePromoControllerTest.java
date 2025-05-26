package backend.eventsphere.promo.controller;

import backend.eventsphere.auth.config.JwtUtil;
import backend.eventsphere.auth.model.User;
import backend.eventsphere.auth.repository.UserRepository;
import backend.eventsphere.config.TestSecurityConfig;
import backend.eventsphere.promo.model.KodePromo;
import backend.eventsphere.promo.service.KodePromoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(KodePromoController.class)
@Import(TestSecurityConfig.class)
public class KodePromoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KodePromoService promoService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtUtil jwtUtil;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private UUID testUserId;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testUser = new User();
        testUser.setId(testUserId);
        testUser.setUsername("testuser");

        Mockito.when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        Mockito.when(jwtUtil.extractUsername(anyString())).thenReturn("testuser");
    }

    private KodePromo samplePercentagePromo() {
        return KodePromo.builder()
                .id(UUID.randomUUID())
                .code("PROMO10")
                .discount(BigDecimal.valueOf(0.1))
                .discountType(KodePromo.DiscountType.PERCENTAGE)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(10))
                .eventId(UUID.randomUUID())
                .createdBy(testUserId)
                .build();
    }

    private KodePromo sampleFixedAmountPromo() {
        return KodePromo.builder()
                .id(UUID.randomUUID())
                .code("DISKON20K")
                .discount(BigDecimal.valueOf(20000))
                .discountType(KodePromo.DiscountType.FIXED_AMOUNT)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(10))
                .eventId(UUID.randomUUID())
                .createdBy(testUserId)
                .build();
    }

    @Test
    @WithMockUser(username = "testuser", roles = "ORGANIZER")
    void testCreatePercentagePromo() throws Exception {
        KodePromo promo = samplePercentagePromo();

        Mockito.when(promoService.createPercentagePromo(
                anyString(), any(), any(), any(), any(), any())
        ).thenReturn(CompletableFuture.completedFuture(promo));

        mockMvc.perform(post("/api/promos")
                        .param("code", promo.getCode())
                        .param("amount", "10") // Sesuai dengan tipe Integer di controller
                        .param("promoType", "percentage")
                        .param("startDate", promo.getStartDate().format(formatter))
                        .param("endDate", promo.getEndDate().format(formatter))
                        .param("eventId", promo.getEventId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(promo.getCode()))
                .andExpect(jsonPath("$.discountType").value("PERCENTAGE"))
                .andExpect(jsonPath("$.discount").value(0.1));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "ORGANIZER")
    void testCreateFixedAmountPromo() throws Exception {
        KodePromo promo = sampleFixedAmountPromo();

        Mockito.when(promoService.createFixedAmountPromo(
                anyString(), any(), any(), any(), any(), any())
        ).thenReturn(CompletableFuture.completedFuture(promo));

        mockMvc.perform(post("/api/promos")
                        .param("code", promo.getCode())
                        .param("amount", "20000")
                        .param("promoType", "fixed_amount")
                        .param("startDate", promo.getStartDate().format(formatter))
                        .param("endDate", promo.getEndDate().format(formatter))
                        .param("eventId", promo.getEventId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(promo.getCode()))
                .andExpect(jsonPath("$.discountType").value("FIXED_AMOUNT"))
                .andExpect(jsonPath("$.discount").value(20000));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "ORGANIZER")
    void testCreatePromo_MissingRequiredFields() throws Exception {
        mockMvc.perform(post("/api/promos")
                        .param("code", "TEST")
                        .param("amount", "10")
                        .param("promoType", "percentage"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "ATTENDEE")
    void testGetPromoById_Found() throws Exception {
        KodePromo promo = samplePercentagePromo();
        Mockito.when(promoService.getPromoById(promo.getId()))
                .thenReturn(Optional.of(promo));

        mockMvc.perform(get("/api/promos/{id}", promo.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(promo.getId().toString()))
                .andExpect(jsonPath("$.code").value(promo.getCode()));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "ATTENDEE")
    void testGetPromoById_NotFound() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.when(promoService.getPromoById(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/promos/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "ATTENDEE")
    void testGetPromosByEventId() throws Exception {
        UUID eventId = UUID.randomUUID();
        List<KodePromo> promos = Arrays.asList(
                samplePercentagePromo(),
                sampleFixedAmountPromo()
        );

        Mockito.when(promoService.getPromosByEvent(eventId)).thenReturn(promos);

        mockMvc.perform(get("/api/promos/event/{eventId}", eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(promos.size()));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "ATTENDEE")
    void testGetActivePromosByEvent() throws Exception {
        UUID eventId = UUID.randomUUID();
        List<KodePromo> promos = List.of(sampleFixedAmountPromo());

        Mockito.when(promoService.getActivePromosByEvent(eventId)).thenReturn(promos);

        mockMvc.perform(get("/api/promos/active/event/{eventId}", eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(promos.size()));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "ATTENDEE")
    void testGetPromoByCode_Found() throws Exception {
        KodePromo promo = samplePercentagePromo();
        Mockito.when(promoService.getPromoByCode(promo.getCode()))
                .thenReturn(Optional.of(promo));

        mockMvc.perform(get("/api/promos/code/{code}", promo.getCode()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(promo.getCode()));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "ATTENDEE")
    void testGetPromoByCode_NotFound() throws Exception {
        Mockito.when(promoService.getPromoByCode("NON_EXISTENT")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/promos/code/{code}", "NON_EXISTENT"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "ORGANIZER")
    void testDeletePromo_Success() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.doNothing().when(promoService).deletePromo(id);

        mockMvc.perform(delete("/api/promos/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"ATTENDEE", "ORGANIZER"})
    void testGetAllPromos() throws Exception {
        List<KodePromo> promoList = Arrays.asList(samplePercentagePromo(), sampleFixedAmountPromo());
        Mockito.when(promoService.getAllPromos()).thenReturn(promoList);

        mockMvc.perform(get("/api/promos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(promoList.size()))
                .andExpect(jsonPath("$[0].code").value(promoList.get(0).getCode()))
                .andExpect(jsonPath("$[1].code").value(promoList.get(1).getCode()));
    }
}