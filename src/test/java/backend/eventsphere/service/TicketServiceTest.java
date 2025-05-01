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
    void testCreateDuplicateTicket() {
        UUID eventId = UUID.randomUUID();
        ticketService.createTicket(eventId, "VIP", 100.0, 50);

        assertThrows(IllegalArgumentException.class, () -> {
            ticketService.createTicket(eventId, "VIP", 110.0, 30);
        });
    }

    @Test
    void testCreateTicketWithNegativePrice() {
        UUID eventId = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () -> {
            ticketService.createTicket(eventId, "Regular", -15.0, 100);
        });
    }

    @Test
    void testCreateTicketWithNullTicketType() {
        UUID eventId = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () -> {
            ticketService.createTicket(eventId, null, 50.0, 100);
        });
    }

    @Test
    void testCreateTicketWithEmptyTicketType() {
        UUID eventId = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () -> {
            ticketService.createTicket(eventId, "   ", 50.0, 100);
        });
    }

    @Test
    void testCreateTicketWithNegativeQuota() {
        UUID eventId = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () -> {
            ticketService.createTicket(eventId, "Regular", 50.0, -10);
        });
    }

    @Test
    void testGetTicketsByEvent() {
        UUID eventId = UUID.randomUUID();
        ticketService.createTicket(eventId, "VIP", 100.0, 50);
        ticketService.createTicket(eventId, "Regular", 50.0, 100);

        assertEquals(2, ticketService.getTicketsByEvent(eventId).size());
    }

    @Test
    void testGetTicketsForNonExistentEvent() {
        UUID nonExistentEventId = UUID.randomUUID();
        assertTrue(ticketService.getTicketsByEvent(nonExistentEventId).isEmpty());
    }

    @Test
    void testTicketsForDifferentEvents() {
        UUID eventId1 = UUID.randomUUID();
        UUID eventId2 = UUID.randomUUID();

        Ticket ticket1 = ticketService.createTicket(eventId1, "VIP", 100.0, 50);
        Ticket ticket2 = ticketService.createTicket(eventId2, "VIP", 150.0, 30);

        assertEquals(1, ticketService.getTicketsByEvent(eventId1).size());
        assertEquals(1, ticketService.getTicketsByEvent(eventId2).size());

        assertEquals("VIP", ticket1.getTicketType());
        assertEquals("VIP", ticket2.getTicketType());

        assertNotEquals(ticket1.getPrice(), ticket2.getPrice());
        assertNotEquals(ticket1.getQuota(), ticket2.getQuota());
    }

    @Test
    void testMultipleTicketTypesForSameEvent() {
        UUID eventId = UUID.randomUUID();

        ticketService.createTicket(eventId, "VIP", 200.0, 20);
        ticketService.createTicket(eventId, "Regular", 100.0, 100);
        ticketService.createTicket(eventId, "Economy", 50.0, 200);

        assertEquals(3, ticketService.getTicketsByEvent(eventId).size());
    }

    @Test
    void testUpdateTicket() {
        UUID eventId = UUID.randomUUID();
        Ticket ticket = ticketService.createTicket(eventId, "VIP", 150.0, 80);

        Ticket updatedTicket = ticketService.updateTicket(ticket.getId(), 200.0, 100);

        assertEquals(200.0, updatedTicket.getPrice());
        assertEquals(100, updatedTicket.getQuota());

        Ticket stored = ticketRepository.findById(ticket.getId());
        assertEquals(200.0, stored.getPrice());
        assertEquals(100, stored.getQuota());
    }

    @Test
    void testUpdateNonExistentTicket() {
        UUID nonExistentTicketId = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () ->
            ticketService.updateTicket(nonExistentTicketId, 200.0, 100)
        );
    }

    @Test
    void testUpdateTicketNegativePrice() {
        UUID eventId = UUID.randomUUID();
        Ticket ticket = ticketService.createTicket(eventId, "Regular", 50.0, 30);

        assertThrows(IllegalArgumentException.class, () ->
            ticketService.updateTicket(ticket.getId(), -10.0, 25)
        );
    }

    @Test
    void testUpdateTicketNegativeQuota() {
        UUID eventId = UUID.randomUUID();
        Ticket ticket = ticketService.createTicket(eventId, "Student", 15.0, 10);

        assertThrows(IllegalArgumentException.class, () ->
            ticketService.updateTicket(ticket.getId(), 20.0, -5)
        );
    }

    @Test
    void testDeleteTicket() {
        UUID eventId = UUID.randomUUID();
        Ticket ticket = ticketService.createTicket(eventId, "VIP", 100.0, 50);

        assertTrue(ticketService.deleteTicket(ticket.getId()));
        assertNull(ticketRepository.findById(ticket.getId())); // Ensure ticket is removed
    }

    @Test
    void testDeleteNonExistentTicket() {
        UUID nonExistentTicketId = UUID.randomUUID();
        assertFalse(ticketService.deleteTicket(nonExistentTicketId));
    }

    @Test
    void testDeleteAllTicketsForEvent() {
        UUID eventId = UUID.randomUUID();

        Ticket ticket1 = ticketService.createTicket(eventId, "VIP", 200.0, 20);
        Ticket ticket2 = ticketService.createTicket(eventId, "Regular", 100.0, 100);

        ticketService.deleteTicket(ticket1.getId());
        ticketService.deleteTicket(ticket2.getId());

        assertTrue(ticketService.getTicketsByEvent(eventId).isEmpty());
    }
}