package backend.eventsphere.service;

import backend.eventsphere.model.Ticket;
import backend.eventsphere.model.TicketFactory;
import backend.eventsphere.repository.TicketRepository;

import java.util.Map;
import java.util.UUID;

public class TicketService {
    private final TicketRepository ticketRepository = new TicketRepository();
    private final TicketFactory ticketFactory = new TicketFactory();

    public TicketService(TicketRepository ticketRepository, TicketFactory ticketFactory) {
    }

    public Ticket createTicket(UUID eventId, String ticketType, Double price, Integer quota) {return null;}

    public Map<UUID, Ticket> getTicketsByEvent(UUID eventId) {return null;}

    public boolean deleteTicket(UUID ticketId) {return false;}
}