package backend.eventsphere.factory;

import backend.eventsphere.model.KodePromo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class DiskonPromoFactory implements PromoFactory {

    @Override
    public KodePromo createPromo(String code, BigDecimal discount, LocalDate startDate,
                                 LocalDate endDate, UUID eventId, UUID createdBy) {
        return new KodePromo(null, code, discount, startDate, endDate, eventId, createdBy);
    }
}