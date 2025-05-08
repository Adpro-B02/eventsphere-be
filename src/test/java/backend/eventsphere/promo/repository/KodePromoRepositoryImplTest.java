package backend.eventsphere.promo.repository;

import backend.eventsphere.promo.model.KodePromo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

public class KodePromoRepositoryImplTest {

    private KodePromoRepository repository;
    private KodePromo promo;
    private UUID eventId;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        repository = new KodePromoRepositoryImpl();
        today = LocalDate.now();
        eventId = UUID.randomUUID();

        promo = new KodePromo(
                UUID.randomUUID(),
                "DISKON10",
                new BigDecimal("0.10"),
                KodePromo.DiscountType.PERCENTAGE,
                today.minusDays(1),
                today.plusDays(5),
                eventId,
                UUID.randomUUID()
        );
    }

    @Test
    void testSaveAndFindById() {
        KodePromo saved = repository.save(promo);
        Optional<KodePromo> result = repository.findPromoById(saved.getId());

        assertTrue(result.isPresent());
        assertEquals("DISKON10", result.get().getCode());
        assertEquals(KodePromo.DiscountType.PERCENTAGE, result.get().getDiscountType());
    }

    @Test
    void testSaveGeneratesIdWhenNull() {
        KodePromo newPromo = new KodePromo(
                null,
                "NEWPROMO",
                new BigDecimal("0.15"),
                KodePromo.DiscountType.PERCENTAGE,
                today,
                today.plusDays(5),
                eventId,
                UUID.randomUUID()
        );

        KodePromo saved = repository.save(newPromo);
        assertNotNull(saved.getId());
    }

    @Test
    void testFindByCode_CaseInsensitive() {
        repository.save(promo);

        Optional<KodePromo> result1 = repository.findPromoByCode("DISKON10");
        Optional<KodePromo> result2 = repository.findPromoByCode("diskon10");

        assertTrue(result1.isPresent());
        assertTrue(result2.isPresent());
        assertEquals(promo.getId(), result1.get().getId());
        assertEquals(promo.getId(), result2.get().getId());
    }

    @Test
    void testFindAllByEventId() {
        repository.save(promo);

        KodePromo promo2 = new KodePromo(
                UUID.randomUUID(),
                "DISKON20",
                new BigDecimal("20000"),
                KodePromo.DiscountType.FIXED_AMOUNT,
                today,
                today.plusDays(3),
                eventId,
                UUID.randomUUID()
        );
        repository.save(promo2);

        List<KodePromo> promos = repository.findAllByEventId(eventId);
        assertEquals(2, promos.size());
        assertThat(promos).extracting(KodePromo::getDiscountType)
                .containsExactlyInAnyOrder(
                        KodePromo.DiscountType.PERCENTAGE,
                        KodePromo.DiscountType.FIXED_AMOUNT
                );
    }

    @Test
    void testFindActivePromos() {
        // Active promo
        repository.save(promo);

        // Expired promo
        KodePromo expiredPromo = new KodePromo(
                UUID.randomUUID(),
                "EXPIRED",
                new BigDecimal("0.20"),
                KodePromo.DiscountType.PERCENTAGE,
                today.minusDays(5),
                today.minusDays(1),
                eventId,
                UUID.randomUUID()
        );
        repository.save(expiredPromo);

        List<KodePromo> activePromos = repository.findActivePromos(eventId, today);
        assertEquals(1, activePromos.size());
        assertEquals("DISKON10", activePromos.get(0).getCode());
    }

    @Test
    void testDeleteById() {
        repository.save(promo);
        repository.deletePromoById(promo.getId());
        assertFalse(repository.findPromoById(promo.getId()).isPresent());
    }

    @Test
    void testExistsByCode() {
        repository.save(promo);
        assertTrue(repository.existsByCode("DISKON10"));
        assertTrue(repository.existsByCode("diskon10"));
        assertFalse(repository.existsByCode("NON_EXISTENT"));
    }

    @Test
    void testFindPromoByCode_ReturnsEmpty_WhenNotFound() {
        Optional<KodePromo> result = repository.findPromoByCode("NOT_EXIST");
        assertTrue(result.isEmpty());
    }
}