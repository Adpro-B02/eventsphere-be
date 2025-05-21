package backend.eventsphere.ticket.service;

import enums.TicketEventType;
import backend.eventsphere.ticket.model.Ticket;
import backend.eventsphere.ticket.model.TicketFactory;
import backend.eventsphere.ticket.model.TicketObserver;
import backend.eventsphere.ticket.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

public class TicketServiceTest {
    private TicketService ticketService;
    private TicketRepository ticketRepository;
    private TicketFactory ticketFactory;
    private TestObserver testObserver;

    @BeforeEach
    void setUp() {
        ticketRepository = new TicketRepository();
        ticketFactory = new TicketFactory();
        ticketService = new TicketService(ticketRepository, ticketFactory);
        testObserver = new TestObserver();
        ticketService.registerObserver(testObserver);
    }

    private static class TestObserver implements TicketObserver {
        private TicketEventType lastEvent;
        private Ticket lastTicket;
        private int notificationCount;

        @Override
        public void onTicketEvent(Ticket ticket, TicketEventType eventType) {
            this.lastEvent = eventType;
            this.lastTicket = ticket;
            this.notificationCount++;
        }

        void reset() {
            lastEvent = null;
            lastTicket = null;
            notificationCount = 0;
        }
    }

    @Test
    void testCreateTicket() {
        UUID eventId = UUID.randomUUID();
        Ticket ticket = ticketService.createTicket(eventId, "VIP", 100.0, 50);

        assertEquals(TicketEventType.CREATED, testObserver.lastEvent);
        assertEquals(ticket, testObserver.lastTicket);
        assertEquals(1, testObserver.notificationCount);
    }

    @Test
    void testCreateDuplicateTicket() {
        UUID eventId = UUID.randomUUID();
        ticketService.createTicket(eventId, "VIP", 100.0, 50);
        testObserver.reset();

        assertThrows(IllegalArgumentException.class, () -> {
            ticketService.createTicket(eventId, "VIP", 110.0, 30);
        });

        assertEquals(0, testObserver.notificationCount);
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
        testObserver.reset();

        Ticket updatedTicket = ticketService.updateTicket(ticket.getId(), 200.0, 100);

        assertEquals(TicketEventType.UPDATED, testObserver.lastEvent);
        assertEquals(updatedTicket, testObserver.lastTicket);
        assertEquals(1, testObserver.notificationCount);
    }

    @Test
    void testUpdateNonExistentTicket() {
        UUID nonExistentTicketId = UUID.randomUUID();
        testObserver.reset();

        assertThrows(IllegalArgumentException.class, () ->
            ticketService.updateTicket(nonExistentTicketId, 200.0, 100)
        );

        assertEquals(0, testObserver.notificationCount);
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
        testObserver.reset();

        boolean result = ticketService.deleteTicket(ticket.getId());

        assertTrue(result);
        assertEquals(TicketEventType.DELETED, testObserver.lastEvent);
        assertEquals(ticket, testObserver.lastTicket);
        assertEquals(1, testObserver.notificationCount);
    }

    @Test
    void testDeleteNonExistentTicket() {
        UUID nonExistentTicketId = UUID.randomUUID();
        testObserver.reset();

        boolean result = ticketService.deleteTicket(nonExistentTicketId);

        assertFalse(result);
        assertEquals(0, testObserver.notificationCount);
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

    @Test
    void testMultipleNotifications() {
        UUID eventId = UUID.randomUUID();

        Ticket ticket1 = ticketService.createTicket(eventId, "VIP", 200.0, 20);
        Ticket ticket2 = ticketService.createTicket(eventId, "Regular", 100.0, 100);
        ticketService.updateTicket(ticket1.getId(), 250.0, 15);
        ticketService.deleteTicket(ticket2.getId());

        assertEquals(4, testObserver.notificationCount);
        assertEquals(TicketEventType.DELETED, testObserver.lastEvent);
    }
}