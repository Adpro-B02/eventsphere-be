package backend.eventsphere.service;

import backend.eventsphere.model.Ticket;
import backend.eventsphere.model.TicketFactory;
import backend.eventsphere.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
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

    public Ticket updateTicket(UUID ticketId, Double newPrice, Integer newQuota) {
        Ticket ticket = ticketRepository.findById(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("Ticket not found with ID: " + ticketId);
        }
        if (newPrice != null) {
            if (newPrice < 0) {
                throw new IllegalArgumentException("Price cannot be negative");
            }
            ticket.setPrice(newPrice);
        }
        if (newQuota != null) {
            if (newQuota < 0) {
                throw new IllegalArgumentException("Quota cannot be negative");
            }
            ticket.setQuota(newQuota);
        }
        ticketRepository.update(ticket);
        return ticket;
    }

    public boolean deleteTicket(UUID ticketId) {
        return ticketRepository.delete(ticketId);
    }
}