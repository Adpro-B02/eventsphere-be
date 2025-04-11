package backend.eventsphere.repository;

import backend.eventsphere.model.Ticket;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Repository
public class TicketRepository {
    private final Map<UUID, Ticket> ticketStorage = new HashMap<>();

    public void save(Ticket ticket) {}
    public Ticket findById(UUID id) {return null;}
    public boolean delete(UUID id) {return false;}
    public Map<UUID, Ticket> listByEvent(UUID eventId) {return null;}
}
