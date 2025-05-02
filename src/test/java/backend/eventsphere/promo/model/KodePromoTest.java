package backend.eventsphere.promo.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

public class KodePromoTest {
    private KodePromo promo;
    private LocalDate today;
    private LocalDate tomorrow;
    private LocalDate yesterday;

    @BeforeEach
    void setUp() {
        today = LocalDate.now();
        tomorrow = today.plusDays(1);
        yesterday = today.minusDays(1);
        promo = new KodePromo(
                UUID.randomUUID(),
                "PROMO123",
                new BigDecimal("20.0"),
                yesterday,
                tomorrow,
                UUID.randomUUID(),
                UUID.randomUUID()
        );
    }

    @Test
    void testUUIDIsGenerated() {
        assertThat(promo.getId()).isInstanceOf(UUID.class);
    }

    @Test
    void testDiscountPositiveAndBelowLimit() {
        assertThat(promo.getDiscount()).isPositive();
        assertThat(promo.getDiscount()).isLessThanOrEqualTo(new BigDecimal("100.0"));
    }

    @Test
    void testPromoIsValidToday() {
        assertThat(promo.isValid()).isTrue();
    }

    @Test
    void testPromoIsInvalidOutsideDate() {
        KodePromo expired = new KodePromo(
                UUID.randomUUID(),
                "EXPIRED",
                new BigDecimal("10.0"),
                yesterday.minusDays(5),
                yesterday.minusDays(1),
                UUID.randomUUID(),
                UUID.randomUUID()
        );
        assertThat(expired.isValid()).isFalse();
    }

    @Test
    void testStartDateMustBeBeforeEndDate() {
        assertThatThrownBy(() -> new KodePromo(
                UUID.randomUUID(),
                "BAD_DATE",
                new BigDecimal("10.0"),
                tomorrow,
                yesterday,
                UUID.randomUUID(),
                UUID.randomUUID()
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Start date must be before or equal to end date");
    }
}