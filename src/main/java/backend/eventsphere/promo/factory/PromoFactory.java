package backend.eventsphere.promo.factory;

import backend.eventsphere.promo.model.KodePromo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public interface PromoFactory {
    KodePromo createPromo(String code, BigDecimal discount, KodePromo.DiscountType discountType,
                          LocalDate startDate, LocalDate endDate, UUID eventId, UUID createdBy);
}