package backend.eventsphere.promo.service;

import backend.eventsphere.promo.model.KodePromo;
import backend.eventsphere.promo.repository.KodePromoRepository;
import backend.eventsphere.promo.factory.PromoFactory;
import backend.eventsphere.promo.factory.DiskonPromoFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class KodePromoServiceTest {

    private KodePromoRepository repository;
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
    void testCreatePromo_PercentageDiscount() {
        KodePromo promo = createSamplePromo("DISC10", new BigDecimal("0.1"),
                KodePromo.DiscountType.PERCENTAGE);

        when(repository.existsByCodeIgnoreCase("DISC10")).thenReturn(false);
        when(repository.save(any(KodePromo.class))).thenReturn(promo);

        KodePromo created = service.createPromo(
                "DISC10",
                new BigDecimal("0.1"),
                KodePromo.DiscountType.PERCENTAGE,
                today,
                today.plusDays(7),
                eventId,
                userId
        );

        assertEquals("DISC10", created.getCode());
        assertEquals(KodePromo.DiscountType.PERCENTAGE, created.getDiscountType());
        verify(repository).save(any(KodePromo.class));
    }

    @Test
    void testCreatePromo_FixedAmountDiscount() {
        KodePromo promo = createSamplePromo("CASH50", new BigDecimal("50000"),
                KodePromo.DiscountType.FIXED_AMOUNT);

        when(repository.existsByCodeIgnoreCase("CASH50")).thenReturn(false);
        when(repository.save(any(KodePromo.class))).thenReturn(promo);

        KodePromo created = service.createPromo(
                "CASH50",
                new BigDecimal("50000"),
                KodePromo.DiscountType.FIXED_AMOUNT,
                today,
                today.plusDays(30),
                eventId,
                userId
        );

        assertEquals("CASH50", created.getCode());
        assertEquals(KodePromo.DiscountType.FIXED_AMOUNT, created.getDiscountType());
        verify(repository).save(any(KodePromo.class));
    }

    @Test
    void testCreatePromo_ShouldThrowWhenFixedAmountHasDecimal() {
        assertThrows(IllegalArgumentException.class, () ->
                service.createPromo(
                        "INVALID",
                        new BigDecimal("50000.50"),
                        KodePromo.DiscountType.FIXED_AMOUNT,
                        today,
                        today.plusDays(1),
                        eventId,
                        userId
                )
        );
    }

    @Test
    void testCreatePromo_ShouldThrowWhenDiscountZero() {
        assertThrows(IllegalArgumentException.class, () ->
                service.createPromo(
                        "ZERO",
                        BigDecimal.ZERO,
                        KodePromo.DiscountType.PERCENTAGE,
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
    void testUpdatePromo_ChangeDiscountType() {
        KodePromo existing = createSamplePromo("OLD", new BigDecimal("0.1"),
                KodePromo.DiscountType.PERCENTAGE);

        when(repository.findById(promoId)).thenReturn(Optional.of(existing));
        when(repository.existsByCodeIgnoreCase("NEW")).thenReturn(false);
        when(repository.save(any(KodePromo.class))).thenReturn(existing);

        KodePromo updated = service.updatePromo(
                promoId,
                "NEW",
                new BigDecimal("50000"),
                KodePromo.DiscountType.FIXED_AMOUNT,
                today,
                today.plusDays(5)
        );

        assertEquals(KodePromo.DiscountType.FIXED_AMOUNT, updated.getDiscountType());
        assertEquals(new BigDecimal("50000"), updated.getDiscount());
    }

    @Test
    void testUpdatePromo_WhenNewCodeExists() {
        KodePromo existing = createSamplePromo("OLDCODE", new BigDecimal("0.1"),
                KodePromo.DiscountType.PERCENTAGE);
        when(repository.findById(promoId)).thenReturn(Optional.of(existing));
        when(repository.existsByCodeIgnoreCase("EXISTING_CODE")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                service.updatePromo(
                        promoId,
                        "EXISTING_CODE",
                        new BigDecimal("0.3"),
                        KodePromo.DiscountType.PERCENTAGE,
                        today.plusDays(1),
                        today.plusDays(5))
        );

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
                service.createPromo(
                        "DUPLICATE",
                        new BigDecimal("0.1"),
                        KodePromo.DiscountType.PERCENTAGE,
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
                service.createPromo(
                        "INVALID_DATE",
                        new BigDecimal("0.1"),
                        KodePromo.DiscountType.PERCENTAGE,
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
}