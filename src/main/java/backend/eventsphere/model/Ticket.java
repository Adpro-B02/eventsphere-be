package backend.eventsphere.model;

import lombok.Builder;
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
    }
}
