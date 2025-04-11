package backend.eventsphere.service;

import backend.eventsphere.model.Ticket;
import backend.eventsphere.model.TicketFactory;
import backend.eventsphere.repository.TicketRepository;

import java.util.Map;
import java.util.UUID;

public class TicketService {
    private final TicketRepository ticketRepository;
    private final TicketFactory ticketFactory;

    public TicketService(TicketRepository ticketRepository, TicketFactory ticketFactory) {
        this.ticketRepository = ticketRepository;
        this.ticketFactory = ticketFactory;
    }

    public Ticket createTicket(UUID eventId, String ticketType, Double price, Integer quota) {
        Ticket ticket = ticketFactory.createTicket(eventId, ticketType, price, quota);
        ticketRepository.save(ticket);
        return ticket;
    }

    public Map<UUID, Ticket> getTicketsByEvent(UUID eventId) {
        return ticketRepository.listByEvent(eventId);
    }

    public boolean deleteTicket(UUID ticketId) {
        return ticketRepository.delete(ticketId);
    }
}