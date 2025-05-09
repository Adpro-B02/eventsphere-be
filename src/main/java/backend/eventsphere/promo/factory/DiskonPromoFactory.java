package backend.eventsphere.promo.factory;

import backend.eventsphere.promo.model.KodePromo;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Component
public class DiskonPromoFactory implements PromoFactory {

    @Override
    public KodePromo createPercentageDiscount(String code, BigDecimal percentage,
                                              LocalDate startDate, LocalDate endDate,
                                              UUID eventId, UUID createdBy) {
        if (percentage.compareTo(BigDecimal.ZERO) <= 0 ||
                percentage.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("Percentage must be between 0 and 1 (100%)");
        }

        return new KodePromo(
                null, code, percentage,
                KodePromo.DiscountType.PERCENTAGE,
                startDate, endDate, eventId, createdBy
        );
    }

    @Override
    public KodePromo createFixedAmountDiscount(String code, BigDecimal fixedAmount,
                                               LocalDate startDate, LocalDate endDate,
                                               UUID eventId, UUID createdBy) {
        if (fixedAmount.scale() > 0) {
            throw new IllegalArgumentException("Fixed amount must be a whole number");
        }

        return new KodePromo(
                null, code, fixedAmount,
                KodePromo.DiscountType.FIXED_AMOUNT,
                startDate, endDate, eventId, createdBy
        );
    }
}