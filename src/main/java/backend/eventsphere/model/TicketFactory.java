package backend.eventsphere.model;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TicketFactory {
    private Map<UUID, Map<String, Ticket>> ticketsByEvent;

    public TicketFactory() {
        this.ticketsByEvent = new HashMap<UUID, Map<String, Ticket>>();
    }

    public Ticket createTicket(UUID eventId, String ticketType, Double price, Integer quota) {
        ticketsByEvent.putIfAbsent(eventId, new HashMap<>());

        Map<String, Ticket> ticketsForEvent = ticketsByEvent.get(eventId);

        if (ticketsForEvent.containsKey(ticketType)) {
            throw new IllegalArgumentException("Ticket type already exists for this event");
        }

        Ticket ticket = new Ticket(UUID.randomUUID(), ticketType, price, quota);
        ticketsForEvent.put(ticketType, ticket);

        return ticket;
    }
}
