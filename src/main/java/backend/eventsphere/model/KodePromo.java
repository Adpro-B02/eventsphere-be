package backend.eventsphere.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class KodePromo {

    private UUID id;
    private String code;
    private BigDecimal discount;
    private LocalDate startDate;
    private LocalDate endDate;
    private UUID eventId;
    private UUID createdBy;

    public KodePromo() {
        this.id = UUID.randomUUID();
    }

    public KodePromo(UUID uuid, String code, BigDecimal discount, LocalDate startDate,
                     LocalDate endDate, UUID eventId, UUID createdBy) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }

        this.id = UUID.randomUUID();
        this.code = code;
        this.discount = discount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.eventId = eventId;
        this.createdBy = createdBy;
    }

    public boolean isValid() {
        LocalDate today = LocalDate.now();
        return (startDate.isEqual(today) || startDate.isBefore(today)) &&
                (endDate.isEqual(today) || endDate.isAfter(today));
    }
}