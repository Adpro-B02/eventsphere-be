package backend.eventsphere.promo.repository;

import backend.eventsphere.promo.model.KodePromo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class KodePromoRepositoryTest {

    @Autowired
    private KodePromoRepository repository;

    private KodePromo promo;
    private UUID eventId;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        today = LocalDate.now();
        eventId = UUID.randomUUID();

        promo = new KodePromo(
                null,
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
        Optional<KodePromo> found = repository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getCode()).isEqualTo("DISKON10");
        assertThat(found.get().getDiscount()).isEqualTo(new BigDecimal("0.10"));
        assertThat(found.get().getDiscountType()).isEqualTo(KodePromo.DiscountType.PERCENTAGE);
    }

    @Test
    void testSaveGeneratesId() {
        assertThat(promo.getId()).isNull();
        KodePromo saved = repository.save(promo);
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void testFindByCodeIgnoreCase() {
        repository.save(promo);

        assertThat(repository.findByCodeIgnoreCase("diskon10")).isPresent();
        assertThat(repository.findByCodeIgnoreCase("DISKON10")).isPresent();
        assertThat(repository.findByCodeIgnoreCase("invalid")).isEmpty();
    }

    @Test
    void testExistsByCodeIgnoreCase() {
        repository.save(promo);

        assertThat(repository.existsByCodeIgnoreCase("diskon10")).isTrue();
        assertThat(repository.existsByCodeIgnoreCase("DISKON10")).isTrue();
        assertThat(repository.existsByCodeIgnoreCase("invalid")).isFalse();
    }

    @Test
    void testFindByEventId() {
        repository.save(promo);

        KodePromo promo2 = new KodePromo(
                null,
                "DISKON20",
                new BigDecimal("20000"),
                KodePromo.DiscountType.FIXED_AMOUNT,
                today,
                today.plusDays(3),
                eventId,
                UUID.randomUUID()
        );
        repository.save(promo2);

        List<KodePromo> promos = repository.findByEventId(eventId);

        assertThat(promos).hasSize(2);
        assertThat(promos)
                .extracting(KodePromo::getCode)
                .containsExactlyInAnyOrder("DISKON10", "DISKON20");
    }

    @Test
    void testFindActivePromos() {
        // Active promo
        repository.save(promo);

        // Expired promo
        KodePromo expiredPromo = new KodePromo(
                null,
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

        assertThat(activePromos)
                .hasSize(1)
                .extracting(KodePromo::getCode)
                .containsExactly("DISKON10");
    }

    @Test
    void testDeleteById() {
        KodePromo saved = repository.save(promo);

        repository.deleteById(saved.getId());

        assertThat(repository.findById(saved.getId())).isEmpty();
    }

    @Test
    void testDeleteByEntity() {
        KodePromo saved = repository.save(promo);

        repository.delete(saved);

        assertThat(repository.findById(saved.getId())).isEmpty();
    }

    @Test
    void testUpdatePromo() {
        KodePromo saved = repository.save(promo);

        saved.setCode("UPDATED_CODE");
        saved.setDiscount(new BigDecimal("0.15"));
        saved.setDiscountType(KodePromo.DiscountType.FIXED_AMOUNT);

        KodePromo updated = repository.save(saved);

        Optional<KodePromo> found = repository.findById(updated.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getCode()).isEqualTo("UPDATED_CODE");
        assertThat(found.get().getDiscount()).isEqualTo(new BigDecimal("0.15"));
        assertThat(found.get().getDiscountType()).isEqualTo(KodePromo.DiscountType.FIXED_AMOUNT);
    }

    @Test
    void testSaveWithInvalidDiscount() {
        KodePromo invalidPromo = new KodePromo(
                null,
                "INVALID",
                new BigDecimal("1.50"), // Percentage > 100%
                KodePromo.DiscountType.PERCENTAGE,
                today,
                today.plusDays(5),
                eventId,
                UUID.randomUUID()
        );

        try {
            repository.save(invalidPromo);
        } catch (Exception e) {
            assertThat(e).hasCauseInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Percentage discount cannot be more than 100%");
        }
    }
}