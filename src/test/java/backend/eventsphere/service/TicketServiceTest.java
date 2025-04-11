package backend.eventsphere.service;

import backend.eventsphere.model.Ticket;
import backend.eventsphere.model.TicketFactory;
import backend.eventsphere.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

public class TicketServiceTest {
    private TicketService ticketService;
    private TicketRepository ticketRepository;
    private TicketFactory ticketFactory;

    @BeforeEach
    void setUp() {
        ticketRepository = new TicketRepository();
        ticketFactory = new TicketFactory();
        ticketService = new TicketService(ticketRepository, ticketFactory);
    }

    @Test
    void testCreateTicket() {
        UUID eventId = UUID.randomUUID();
        Ticket ticket = ticketService.createTicket(eventId, "VIP", 100.0, 50);

        assertNotNull(ticket);
        assertNotNull(ticket.getId());
        assertEquals(eventId, ticket.getEventId());
    }

    @Test
    void testGetTicketsByEvent() {
        UUID eventId = UUID.randomUUID();
        ticketService.createTicket(eventId, "VIP", 100.0, 50);
        ticketService.createTicket(eventId, "Regular", 50.0, 100);

        assertEquals(2, ticketService.getTicketsByEvent(eventId).size());
    }

    @Test
    void testDeleteTicket() {
        UUID eventId = UUID.randomUUID();
        Ticket ticket = ticketService.createTicket(eventId, "VIP", 100.0, 50);

        assertTrue(ticketService.deleteTicket(ticket.getId()));
        assertNull(ticketRepository.findById(ticket.getId())); // Ensure ticket is removed
    }

    @Test
    void testCreateDuplicateTicket() {
        UUID eventId = UUID.randomUUID();
        ticketService.createTicket(eventId, "VIP", 100.0, 50);

        assertThrows(IllegalArgumentException.class, () -> {
            ticketService.createTicket(eventId, "VIP", 110.0, 30);
        });
    }
}