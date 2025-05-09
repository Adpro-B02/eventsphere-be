package backend.eventsphere.promo.factory;

import backend.eventsphere.promo.model.KodePromo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public interface PromoFactory {
    KodePromo createPercentageDiscount(String code, BigDecimal percentage,
                                       LocalDate startDate, LocalDate endDate,
                                       UUID eventId, UUID createdBy);

    KodePromo createFixedAmountDiscount(String code, BigDecimal fixedAmount,
                                        LocalDate startDate, LocalDate endDate,
                                        UUID eventId, UUID createdBy);
}