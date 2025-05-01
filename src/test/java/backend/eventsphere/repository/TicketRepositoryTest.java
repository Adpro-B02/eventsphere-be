package backend.eventsphere.repository;

import backend.eventsphere.model.Ticket;
import backend.eventsphere.model.TicketFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TicketRepositoryTest {

    private TicketRepository ticketRepository;
    private TicketFactory ticketFactory;

    @BeforeEach
    void setUp() {
        ticketRepository = new TicketRepository();
        ticketFactory = new TicketFactory();
    }

    @Test
    void testSaveAndGetTicket() {
        UUID eventId = UUID.randomUUID();
        Ticket ticket = ticketFactory.createTicket(eventId, "VIP", 100.0, 50);

        ticketRepository.save(ticket);

        Ticket retrievedTicket = ticketRepository.findById(ticket.getId());
        assertNotNull(retrievedTicket);
        assertEquals(ticket.getId(), retrievedTicket.getId());
    }

    @Test
    void testSaveDuplicateTicket() {
        UUID eventId = UUID.randomUUID();
        Ticket ticket1 = ticketFactory.createTicket(eventId, "VIP", 100.0, 50);
        ticketRepository.save(ticket1);

        assertThrows(IllegalArgumentException.class, () -> {
            ticketRepository.save(ticket1); // Attempting to save duplicate
        });
    }

    @Test
    void testFindTicketById() {
        UUID eventId = UUID.randomUUID();
        Ticket ticket = ticketFactory.createTicket(eventId, "VIP", 100.0, 50);
        ticketRepository.save(ticket);

        Ticket retrievedTicket = ticketRepository.findById(ticket.getId());
        assertNotNull(retrievedTicket);
        assertEquals(ticket.getId(), retrievedTicket.getId());
    }

    @Test
    void testFindMissingTicket() {
        UUID missingTicketId = UUID.randomUUID();
        Ticket retrievedTicket = ticketRepository.findById(missingTicketId);

        assertNull(retrievedTicket);
    }

    @Test
    void testDeleteTicket() {
        UUID eventId = UUID.randomUUID();
        Ticket ticket = ticketFactory.createTicket(eventId, "VIP", 100.0, 50);
        ticketRepository.save(ticket);

        assertTrue(ticketRepository.delete(ticket.getId()));
        assertNull(ticketRepository.findById(ticket.getId())); // Should not find ticket after deletion
    }

    @Test
    void testDeleteMissingTicket() {
        UUID missingTicketId = UUID.randomUUID();

        assertFalse(ticketRepository.delete(missingTicketId));
    }

    @Test
    void testListTicketsForEvent() {
        UUID eventId = UUID.randomUUID();

        Ticket ticket1 = ticketFactory.createTicket(eventId, "VIP", 100.0, 50);
        Ticket ticket2 = ticketFactory.createTicket(eventId, "Regular", 50.0, 100);

        ticketRepository.save(ticket1);
        ticketRepository.save(ticket2);

        assertEquals(2, ticketRepository.listByEvent(eventId).size());
    }
}