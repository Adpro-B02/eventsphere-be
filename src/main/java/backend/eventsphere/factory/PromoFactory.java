package backend.eventsphere.factory;

import backend.eventsphere.model.KodePromo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public interface PromoFactory {
    KodePromo createPromo(String code, BigDecimal discount, LocalDate startDate, LocalDate endDate, UUID eventId, UUID createdBy);
}