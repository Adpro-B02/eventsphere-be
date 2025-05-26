package backend.eventsphere.promo.service;

import backend.eventsphere.promo.model.KodePromo;
import backend.eventsphere.promo.repository.KodePromoRepository;
import backend.eventsphere.promo.factory.PromoFactory;
import backend.eventsphere.promo.factory.DiskonPromoFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KodePromoServiceTest {

    @Mock
    private KodePromoRepository repository;

    @InjectMocks
    private KodePromoService service;
    private UUID promoId;
    private UUID eventId;
    private UUID userId;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        repository = mock(KodePromoRepository.class);
        PromoFactory factory = new DiskonPromoFactory();
        service = new KodePromoService(repository, factory);

        promoId = UUID.randomUUID();
        eventId = UUID.randomUUID();
        userId = UUID.randomUUID();
        today = LocalDate.now();
    }

    private KodePromo createSamplePromo(String code, BigDecimal discount,
                                        KodePromo.DiscountType discountType) {
        return new KodePromo(
                promoId,
                code,
                discount,
                discountType,
                today,
                today.plusDays(7),
                eventId,
                userId
        );
    }

    @Test
    void testCreatePercentagePromo_Success() throws Exception{
        KodePromo promo = createSamplePromo("DISC10", new BigDecimal("0.1"),
                KodePromo.DiscountType.PERCENTAGE);

        when(repository.existsByCodeIgnoreCase("DISC10")).thenReturn(false);
        when(repository.save(any(KodePromo.class))).thenReturn(promo);

        CompletableFuture<KodePromo> future = service.createPercentagePromo(
                "DISC10",
                new BigDecimal("0.1"),
                today,
                today.plusDays(7),
                eventId,
                userId
        );

        KodePromo created = future.get();

        assertEquals("DISC10", created.getCode());
        assertEquals(KodePromo.DiscountType.PERCENTAGE, created.getDiscountType());
        assertEquals(new BigDecimal("0.1"), created.getDiscount());
        verify(repository).save(any(KodePromo.class));
    }

    @Test
    void testCreateFixedAmountPromo_Success() throws Exception{
        KodePromo promo = createSamplePromo("CASH50", new BigDecimal("50000"),
                KodePromo.DiscountType.FIXED_AMOUNT);

        when(repository.existsByCodeIgnoreCase("CASH50")).thenReturn(false);
        when(repository.save(any(KodePromo.class))).thenReturn(promo);

        CompletableFuture<KodePromo> future = service.createFixedAmountPromo(
                "CASH50",
                new BigDecimal("50000"),
                today,
                today.plusDays(30),
                eventId,
                userId
        );

        KodePromo created = future.get();

        assertEquals("CASH50", created.getCode());
        assertEquals(KodePromo.DiscountType.FIXED_AMOUNT, created.getDiscountType());
        assertEquals(new BigDecimal("50000"), created.getDiscount());
        verify(repository).save(any(KodePromo.class));
    }

    @Test
    void testCreateFixedAmountPromo_InvalidAmount() {
        assertThrows(IllegalArgumentException.class, () -> {
            try {
                service.createFixedAmountPromo(
                        "INVALID",
                        new BigDecimal("50000.50"),
                        today,
                        today.plusDays(1),
                        eventId,
                        userId
                ).get();
            } catch (ExecutionException | InterruptedException e) {
                throw e.getCause();
            }
        });
    }

    @Test
    void testCreatePercentagePromo_InvalidPercentage() {
        // Percentage > 100%
        assertThrows(IllegalArgumentException.class, () ->
                service.createPercentagePromo(
                        "INVALID",
                        new BigDecimal("1.5"),
                        today,
                        today.plusDays(1),
                        eventId,
                        userId
                )
        );

        // Percentage <= 0
        assertThrows(IllegalArgumentException.class, () ->
                service.createPercentagePromo(
                        "INVALID",
                        BigDecimal.ZERO,
                        today,
                        today.plusDays(1),
                        eventId,
                        userId
                )
        );
    }

    @Test
    void testGetPromoByIdFound() {
        KodePromo promo = createSamplePromo("CODE", new BigDecimal("0.2"),
                KodePromo.DiscountType.PERCENTAGE);
        when(repository.findById(promoId)).thenReturn(Optional.of(promo));

        Optional<KodePromo> found = service.getPromoById(promoId);

        assertTrue(found.isPresent());
        assertEquals("CODE", found.get().getCode());
        assertEquals(KodePromo.DiscountType.PERCENTAGE, found.get().getDiscountType());
    }

    @Test
    void testGetPromoByIdNotFound() {
        when(repository.findById(promoId)).thenReturn(Optional.empty());

        Optional<KodePromo> found = service.getPromoById(promoId);

        assertTrue(found.isEmpty());
    }

    @Test
    void testUpdatePromo_Success() throws Exception {
        KodePromo existing = createSamplePromo("OLD", new BigDecimal("0.1"),
                KodePromo.DiscountType.PERCENTAGE);
        KodePromo updatedPromo = createSamplePromo("NEW", new BigDecimal("50000"),
                KodePromo.DiscountType.FIXED_AMOUNT);

        when(repository.findById(promoId)).thenReturn(Optional.of(existing));
        when(repository.existsByCodeIgnoreCase("NEW")).thenReturn(false);
        when(repository.save(any(KodePromo.class))).thenReturn(updatedPromo);

        CompletableFuture<KodePromo> future = service.updatePromo(
                promoId,
                "NEW",
                new BigDecimal("50000"),
                KodePromo.DiscountType.FIXED_AMOUNT,
                today,
                today.plusDays(5)
        );

        KodePromo updated = future.get();

        assertEquals(KodePromo.DiscountType.FIXED_AMOUNT, updated.getDiscountType());
        assertEquals(new BigDecimal("50000"), updated.getDiscount());
        verify(repository).save(any(KodePromo.class));
    }

    @Test
    void testUpdatePromo_WhenNewCodeExists() {
        KodePromo existing = createSamplePromo("OLDCODE", new BigDecimal("0.1"),
                KodePromo.DiscountType.PERCENTAGE);
        when(repository.findById(promoId)).thenReturn(Optional.of(existing));
        when(repository.existsByCodeIgnoreCase("EXISTING_CODE")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            try {
                service.updatePromo(
                        promoId,
                        "EXISTING_CODE",
                        new BigDecimal("0.3"),
                        KodePromo.DiscountType.PERCENTAGE,
                        today.plusDays(1),
                        today.plusDays(5)
                ).get();
            } catch (ExecutionException | InterruptedException e) {
                throw e.getCause();
            }
        });

        verify(repository, never()).save(any());
    }

    @Test
    void testUpdatePromo_NotFound() {
        when(repository.findById(promoId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                service.updatePromo(
                        promoId,
                        "X",
                        BigDecimal.ONE,
                        KodePromo.DiscountType.PERCENTAGE,
                        today,
                        today)
        );
    }

    @Test
    void testDeletePromo() {
        service.deletePromo(promoId);
        verify(repository, times(1)).deleteById(promoId);
    }

    @Test
    void testGetPromosByEvent() {
        List<KodePromo> promos = Arrays.asList(
                createSamplePromo("A", BigDecimal.ONE, KodePromo.DiscountType.PERCENTAGE),
                createSamplePromo("B", new BigDecimal("20000"), KodePromo.DiscountType.FIXED_AMOUNT)
        );
        when(repository.findByEventId(eventId)).thenReturn(promos);

        List<KodePromo> result = service.getPromosByEvent(eventId);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(p -> p.getDiscountType() == KodePromo.DiscountType.FIXED_AMOUNT));
    }

    @Test
    void testGetActivePromosByEvent() {
        List<KodePromo> activePromos = Arrays.asList(
                createSamplePromo("ACTIVE1", new BigDecimal("0.1"), KodePromo.DiscountType.PERCENTAGE),
                createSamplePromo("ACTIVE2", new BigDecimal("20000"), KodePromo.DiscountType.FIXED_AMOUNT)
        );

        when(repository.findActivePromos(eventId, today)).thenReturn(activePromos);

        List<KodePromo> result = service.getActivePromosByEvent(eventId);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(p -> p.getDiscountType() == KodePromo.DiscountType.FIXED_AMOUNT));
    }

    @Test
    void testCreatePromo_ShouldThrowException_WhenCodeAlreadyExists() {
        when(repository.existsByCodeIgnoreCase("DUPLICATE")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                service.createPercentagePromo(
                        "DUPLICATE",
                        new BigDecimal("0.1"),
                        today,
                        today.plusDays(1),
                        eventId,
                        userId)
        );

        verify(repository, never()).save(any());
    }

    @Test
    void testCreatePromo_ShouldThrowException_WhenEndDateBeforeStartDate() {
        assertThrows(IllegalArgumentException.class, () ->
                service.createPercentagePromo(
                        "INVALID_DATE",
                        new BigDecimal("0.1"),
                        today,
                        today.minusDays(1),
                        eventId,
                        userId)
        );
    }

    @Test
    void testIsPromoCodeExists() {
        when(repository.existsByCodeIgnoreCase("TEST_CODE")).thenReturn(true);
        assertTrue(service.isPromoCodeExists("TEST_CODE"));

        when(repository.existsByCodeIgnoreCase("NON_EXISTENT")).thenReturn(false);
        assertFalse(service.isPromoCodeExists("NON_EXISTENT"));
    }

    @Test
    void testGetAllPromos() {
        List<KodePromo> promos = Arrays.asList(
                createSamplePromo("PROMO1", new BigDecimal("0.1"), KodePromo.DiscountType.PERCENTAGE),
                createSamplePromo("PROMO2", new BigDecimal("20000"), KodePromo.DiscountType.FIXED_AMOUNT)
        );

        when(repository.findAll()).thenReturn(promos);

        List<KodePromo> result = service.getAllPromos();

        assertEquals(2, result.size());
        assertEquals("PROMO1", result.get(0).getCode());
        assertEquals("PROMO2", result.get(1).getCode());
        verify(repository, times(1)).findAll();
    }
}