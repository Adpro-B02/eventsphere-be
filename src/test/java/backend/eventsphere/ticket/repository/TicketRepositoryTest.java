package backend.eventsphere.ticket.repository;

import backend.eventsphere.ticket.model.Ticket;
import backend.eventsphere.ticket.model.TicketFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class TicketRepositoryTest {

    @Autowired
    private TicketRepository ticketRepository;

    private TicketFactory ticketFactory;

    @BeforeEach
    void setUp() {
        ticketFactory = new TicketFactory(ticketRepository);
    }

    @Test
    void testSaveAndGetTicket() {
        UUID eventId = UUID.randomUUID();
        Ticket ticket = ticketFactory.createTicket(eventId, "VIP", 100.0, 50);

        ticketRepository.save(ticket);

        Ticket retrievedTicket = ticketRepository.findById(ticket.getId()).orElse(null);
        assertNotNull(retrievedTicket);
        assertEquals(ticket.getId(), retrievedTicket.getId());
    }

    @Test
    void testUniqueTicketTypeConstraint() {
        UUID eventId = UUID.randomUUID();
        ticketRepository.save(ticketFactory.createTicket(eventId, "VIP", 100.0, 50));

        assertThrows(IllegalArgumentException.class, () -> {
            ticketFactory.createTicket(eventId, "VIP", 150.0, 30);
        });
    }

    @Test
    void testUpdateTicket() {
        UUID eventId = UUID.randomUUID();
        Ticket ticket = ticketFactory.createTicket(eventId, "VIP", 100.0, 50);
        ticketRepository.save(ticket);

        ticket.setPrice(150.0);
        ticket.setQuota(75);

        ticketRepository.save(ticket);

        Ticket updatedTicket = ticketRepository.findById(ticket.getId()).orElse(null);
        assertNotNull(updatedTicket);
        assertEquals(150.0, updatedTicket.getPrice());
        assertEquals(75, updatedTicket.getQuota());
    }

    @Test
    void testDeleteTicket() {
        UUID eventId = UUID.randomUUID();
        Ticket ticket = ticketFactory.createTicket(eventId, "VIP", 100.0, 50);
        ticketRepository.save(ticket);

        ticketRepository.deleteById(ticket.getId());

        assertFalse(ticketRepository.existsById(ticket.getId()));
    }

    @Test
    void testFindTicketsByEventId() {
        UUID eventId = UUID.randomUUID();

        Ticket ticket1 = ticketFactory.createTicket(eventId, "VIP", 100.0, 50);
        Ticket ticket2 = ticketFactory.createTicket(eventId, "Regular", 50.0, 100);

        ticketRepository.save(ticket1);
        ticketRepository.save(ticket2);

        assertEquals(2, ticketRepository.findByEventId(eventId).size());
    }
}