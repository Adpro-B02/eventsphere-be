package backend.eventsphere.promo.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class KodePromoTest {
    private KodePromo promo;
    private LocalDate today;
    private LocalDate tomorrow;
    private LocalDate yesterday;
    private UUID eventId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        today = LocalDate.now();
        tomorrow = today.plusDays(1);
        yesterday = today.minusDays(1);
        eventId = UUID.randomUUID();
        userId = UUID.randomUUID();

        promo = new KodePromo(
                UUID.randomUUID(),
                "PROMO123",
                new BigDecimal("0.2"),
                KodePromo.DiscountType.PERCENTAGE,
                yesterday,
                tomorrow,
                eventId,
                userId
        );
    }

    @Test
    void testUUIDIsGenerated() {
        KodePromo newPromo = new KodePromo();
        assertThat(newPromo.getId()).isInstanceOf(UUID.class);
    }

    @Test
    void testConstructorWithParameters() {
        assertThat(promo.getCode()).isEqualTo("PROMO123");
        assertThat(promo.getDiscount()).isEqualByComparingTo("0.2");
        assertThat(promo.getDiscountType()).isEqualTo(KodePromo.DiscountType.PERCENTAGE);
        assertThat(promo.getStartDate()).isEqualTo(yesterday);
        assertThat(promo.getEndDate()).isEqualTo(tomorrow);
        assertThat(promo.getEventId()).isEqualTo(eventId);
        assertThat(promo.getCreatedBy()).isEqualTo(userId);
    }

    @Test
    void testDiscountPositive() {
        assertThat(promo.getDiscount()).isPositive();
    }

    @ParameterizedTest
    @EnumSource(KodePromo.DiscountType.class)
    void testValidDiscountTypes(KodePromo.DiscountType discountType) {
        BigDecimal discountValue = discountType == KodePromo.DiscountType.FIXED_AMOUNT
                ? new BigDecimal("10000")
                : new BigDecimal("0.1");

        KodePromo promo = new KodePromo(
                UUID.randomUUID(),
                "TEST",
                discountValue,
                discountType,
                yesterday,
                tomorrow,
                eventId,
                userId
        );

        assertThat(promo.getDiscountType()).isEqualTo(discountType);
    }

    @Test
    void testPercentageDiscountLimit() {
        assertThatThrownBy(() -> new KodePromo(
                UUID.randomUUID(),
                "OVER100",
                new BigDecimal("1.1"),
                KodePromo.DiscountType.PERCENTAGE,
                yesterday,
                tomorrow,
                eventId,
                userId
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Percentage discount cannot be more than 100%");
    }

    @Test
    void testFixedAmountDecimalValidation() {
        assertThatCode(() -> new KodePromo(
                UUID.randomUUID(),
                "VALID_FIXED",
                new BigDecimal("50000"),
                KodePromo.DiscountType.FIXED_AMOUNT,
                yesterday,
                tomorrow,
                eventId,
                userId
        )).doesNotThrowAnyException();

        assertThatThrownBy(() -> new KodePromo(
                UUID.randomUUID(),
                "INVALID_FIXED",
                new BigDecimal("50000.50"),
                KodePromo.DiscountType.FIXED_AMOUNT,
                yesterday,
                tomorrow,
                eventId,
                userId
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Fixed amount discount must be a whole number");
    }

    @Test
    void testPromoIsValidToday() {
        assertThat(promo.isValid()).isTrue();
    }

    @Test
    void testPromoIsInvalidAfterEndDate() {
        KodePromo expired = new KodePromo(
                UUID.randomUUID(),
                "EXPIRED",
                new BigDecimal("0.1"),
                KodePromo.DiscountType.PERCENTAGE,
                yesterday.minusDays(5),
                yesterday.minusDays(1),
                eventId,
                userId
        );
        assertThat(expired.isValid()).isFalse();
    }

    @Test
    void testPromoIsInvalidBeforeStartDate() {
        KodePromo futurePromo = new KodePromo(
                UUID.randomUUID(),
                "FUTURE",
                new BigDecimal("0.1"),
                KodePromo.DiscountType.PERCENTAGE,
                tomorrow.plusDays(1),
                tomorrow.plusDays(5),
                eventId,
                userId
        );
        assertThat(futurePromo.isValid()).isFalse();
    }

    @Test
    void testStartDateMustBeBeforeEndDate() {
        assertThatThrownBy(() -> new KodePromo(
                UUID.randomUUID(),
                "BAD_DATE",
                new BigDecimal("0.1"),
                KodePromo.DiscountType.PERCENTAGE,
                tomorrow,
                yesterday,
                eventId,
                userId
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Start date must be before or equal to end date");
    }

    @Test
    void testApplyPercentageDiscount() {
        KodePromo percentagePromo = new KodePromo(
                null,
                "20PERCENT",
                new BigDecimal("0.2"),
                KodePromo.DiscountType.PERCENTAGE,
                yesterday,
                tomorrow,
                eventId,
                userId
        );

        BigDecimal originalPrice = new BigDecimal("100000");
        BigDecimal discountedPrice = percentagePromo.applyDiscount(originalPrice);

        assertThat(discountedPrice).isEqualByComparingTo("80000");
    }

    @Test
    void testApplyFixedAmountDiscount() {
        KodePromo fixedPromo = new KodePromo(
                null,
                "50KCASH",
                new BigDecimal("50000"),
                KodePromo.DiscountType.FIXED_AMOUNT,
                yesterday,
                tomorrow,
                eventId,
                userId
        );

        BigDecimal originalPrice = new BigDecimal("100000");
        BigDecimal discountedPrice = fixedPromo.applyDiscount(originalPrice);

        assertThat(discountedPrice).isEqualByComparingTo("50000");
    }

    @Test
    void testApplyDiscount_ResultNotNegative() {
        KodePromo fixedPromo = new KodePromo(
                null,
                "BIGDISCOUNT",
                new BigDecimal("50000"),
                KodePromo.DiscountType.FIXED_AMOUNT,
                yesterday,
                tomorrow,
                eventId,
                userId
        );

        BigDecimal smallPrice = new BigDecimal("30000");
        BigDecimal discountedPrice = fixedPromo.applyDiscount(smallPrice);

        assertThat(discountedPrice).isEqualByComparingTo("0");
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "-0.1"})
    void testInvalidDiscountValues(String discountValue) {
        assertThatThrownBy(() -> new KodePromo(
                UUID.randomUUID(),
                "INVALID",
                new BigDecimal(discountValue),
                KodePromo.DiscountType.PERCENTAGE,
                yesterday,
                tomorrow,
                eventId,
                userId
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Discount must be positive");
    }
}