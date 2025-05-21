package backend.eventsphere.ticket.repository;

import backend.eventsphere.ticket.model.Ticket;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Repository
public class TicketRepository {
    private final Map<UUID, Ticket> ticketStorage = new HashMap<>();

    public void save(Ticket ticket) {
        if (ticketStorage.containsKey(ticket.getId())) {
            throw new IllegalArgumentException("Ticket with this ID already exists.");
        }
        ticketStorage.put(ticket.getId(), ticket);
    }

    public Ticket findById(UUID id) {
        return ticketStorage.get(id);
    }

    public void update(Ticket ticket) {
        if (!ticketStorage.containsKey(ticket.getId())) {
            throw new IllegalArgumentException("Ticket not found with ID: " + ticket.getId());
        }
        ticketStorage.put(ticket.getId(), ticket);
    }

    public boolean delete(UUID id) {
        return ticketStorage.remove(id) != null;
    }

    public Map<UUID, Ticket> listByEvent(UUID eventId) {
        Map<UUID, Ticket> ticketsForEvent = new HashMap<>();
        for (Ticket ticket : ticketStorage.values()) {
            if (ticket.getEventId().equals(eventId)) {
                ticketsForEvent.put(ticket.getId(), ticket);
            }
        }
        return ticketsForEvent;
    }
}
