package backend.eventsphere.promo.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class KodePromo {
    public enum DiscountType {
        PERCENTAGE,
        FIXED_AMOUNT
    }

    private UUID id;
    private String code;
    private BigDecimal discount;
    private DiscountType discountType;
    private LocalDate startDate;
    private LocalDate endDate;
    private UUID eventId;
    private UUID createdBy;

    public KodePromo() {
        this.id = UUID.randomUUID();
    }

    public KodePromo(UUID id, String code, BigDecimal discount, DiscountType discountType,
                     LocalDate startDate, LocalDate endDate, UUID eventId, UUID createdBy) {
        validateDates(startDate, endDate);
        validateDiscount(discount, discountType);

        this.id = id != null ? id : UUID.randomUUID();
        this.code = code;
        this.discount = discount;
        this.discountType = discountType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.eventId = eventId;
        this.createdBy = createdBy;
    }

    private void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }
    }

    private void validateDiscount(BigDecimal discount, DiscountType discountType) {
        if (discount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Discount must be positive");
        }

        if (discountType == DiscountType.PERCENTAGE && discount.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("Percentage discount cannot be more than 100%");
        }

        if (discountType == DiscountType.FIXED_AMOUNT && discount.scale() > 0) {
            throw new IllegalArgumentException("Fixed amount discount must be a whole number");
        }
    }

    public boolean isValid() {
        LocalDate today = LocalDate.now();
        return !today.isBefore(startDate) && !today.isAfter(endDate);
    }

    public BigDecimal applyDiscount(BigDecimal originalPrice) {
        return discountType == DiscountType.PERCENTAGE
                ? originalPrice.multiply(BigDecimal.ONE.subtract(discount))
                : originalPrice.subtract(discount).max(BigDecimal.ZERO);
    }
}