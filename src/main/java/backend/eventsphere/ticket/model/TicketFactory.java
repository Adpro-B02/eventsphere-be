package backend.eventsphere.ticket.model;

import backend.eventsphere.ticket.repository.TicketRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class TicketFactory {
    private final TicketRepository ticketRepository;

    public TicketFactory(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public Ticket createTicket(UUID eventId, String ticketType, Double price, Integer quota) {
        List<Ticket> existingTickets = ticketRepository.findByEventId(eventId);

        boolean ticketTypeExists = existingTickets.stream()
            .anyMatch(ticket -> ticket.getTicketType().equals(ticketType));

        if (ticketTypeExists) {
            throw new IllegalArgumentException("Ticket type already exists for this event");
        }

        return new Ticket(eventId, ticketType, price, quota);
    }
}
