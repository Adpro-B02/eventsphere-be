package backend.eventsphere.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
public class Ticket {
    private UUID id;
    private UUID eventId;
    private String ticketType;
    @Setter
    private Double price;
    @Setter
    private Integer quota;

    public Ticket(UUID eventId, String ticketType, Double price, Integer quota) {
        if (ticketType == null || ticketType.trim().isEmpty()) {
            throw new IllegalArgumentException("TicketType cannot be empty");
        }
        if (price == null || price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        if (quota == null || quota < 0) {
            throw new IllegalArgumentException("Quota cannot be negative");
        }

        this.id = UUID.randomUUID();
        this.eventId = eventId;
        this.ticketType = ticketType;
        this.price = price;
        this.quota = quota;
    }
}
