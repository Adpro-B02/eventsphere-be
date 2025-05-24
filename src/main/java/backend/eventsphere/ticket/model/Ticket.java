package backend.eventsphere.ticket.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "tickets", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"event_id", "ticket_type"})
})
@Getter
@NoArgsConstructor
public class Ticket {
    @Id
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