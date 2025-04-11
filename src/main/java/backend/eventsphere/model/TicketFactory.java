package backend.eventsphere.model;

import java.util.Map;
import java.util.UUID;

public class TicketFactory {
    private Map<UUID, Map<String, Ticket>> ticketsByEvent;

    public TicketFactory() {
    }

    public Ticket createTicket(UUID eventId, String ticketType, Double price, Integer quota) {
        return null;
    }
}
