package backend.eventsphere.ticket.service;

import enums.TicketEventType;
import backend.eventsphere.ticket.model.Ticket;
import backend.eventsphere.ticket.model.TicketFactory;
import backend.eventsphere.ticket.model.TicketObserver;
import backend.eventsphere.ticket.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {
    private TicketService ticketService;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private TicketFactory ticketFactory;

    private TestObserver testObserver;

    @BeforeEach
    void setUp() {
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
        String ticketType = "VIP";
        Double price = 100.0;
        Integer quota = 50;

        Ticket mockTicket = new Ticket(eventId, ticketType, price, quota);

        when(ticketFactory.createTicket(eventId, ticketType, price, quota)).thenReturn(mockTicket);
        when(ticketRepository.save(mockTicket)).thenReturn(mockTicket);

        Ticket ticket = ticketService.createTicket(eventId, ticketType, price, quota);

        assertEquals(TicketEventType.CREATED, testObserver.lastEvent);
        assertEquals(ticket, testObserver.lastTicket);
        assertEquals(1, testObserver.notificationCount);

        verify(ticketFactory).createTicket(eventId, ticketType, price, quota);
        verify(ticketRepository).save(mockTicket);
    }

    @Test
    void testCreateDuplicateTicket() {
        UUID eventId = UUID.randomUUID();
        String ticketType = "VIP";

        when(ticketFactory.createTicket(eventId, ticketType, 110.0, 30))
            .thenThrow(new IllegalArgumentException("Ticket type already exists for this event"));

        testObserver.reset();

        assertThrows(IllegalArgumentException.class, () -> {
            ticketService.createTicket(eventId, ticketType, 110.0, 30);
        });

        assertEquals(0, testObserver.notificationCount);
    }

    @Test
    void testGetTicketsByEvent() {
        UUID eventId = UUID.randomUUID();
        List<Ticket> tickets = Arrays.asList(
            new Ticket(eventId, "VIP", 100.0, 50),
            new Ticket(eventId, "Regular", 50.0, 100)
        );

        when(ticketRepository.findByEventId(eventId)).thenReturn(tickets);

        Map<UUID, Ticket> result = ticketService.getTicketsByEvent(eventId);
        assertEquals(2, result.size());
    }

    @Test
    void testUpdateTicket() {
        UUID ticketId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        Ticket ticket = new Ticket(eventId, "VIP", 150.0, 80);

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(ticket)).thenReturn(ticket);

        testObserver.reset();

        Ticket updatedTicket = ticketService.updateTicket(ticketId, 200.0, 100);

        assertEquals(TicketEventType.UPDATED, testObserver.lastEvent);
        assertEquals(updatedTicket, testObserver.lastTicket);
        assertEquals(1, testObserver.notificationCount);
        assertEquals(200.0, updatedTicket.getPrice());
        assertEquals(100, updatedTicket.getQuota());

        verify(ticketRepository).save(ticket);
    }

    @Test
    void testUpdateNonExistentTicket() {
        UUID nonExistentTicketId = UUID.randomUUID();
        when(ticketRepository.findById(nonExistentTicketId)).thenReturn(Optional.empty());

        testObserver.reset();

        assertThrows(IllegalArgumentException.class, () ->
            ticketService.updateTicket(nonExistentTicketId, 200.0, 100)
        );

        assertEquals(0, testObserver.notificationCount);
    }

    @Test
    void testUpdateTicketNegativePrice() {
        UUID ticketId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        Ticket ticket = new Ticket(eventId, "Regular", 50.0, 30);

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        assertThrows(IllegalArgumentException.class, () ->
            ticketService.updateTicket(ticketId, -10.0, 25)
        );
    }

    @Test
    void testUpdateTicketNegativeQuota() {
        UUID ticketId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        Ticket ticket = new Ticket(eventId, "Regular", 50.0, 30);

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        assertThrows(IllegalArgumentException.class, () ->
            ticketService.updateTicket(ticketId, 20.0, -5)
        );
    }

    @Test
    void testDeleteTicket() {
        UUID ticketId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        Ticket ticket = new Ticket(eventId, "VIP", 100.0, 50);

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        doNothing().when(ticketRepository).delete(ticket);

        testObserver.reset();

        boolean result = ticketService.deleteTicket(ticketId);

        assertTrue(result);
        assertEquals(TicketEventType.DELETED, testObserver.lastEvent);
        assertEquals(ticket, testObserver.lastTicket);
        assertEquals(1, testObserver.notificationCount);

        verify(ticketRepository).delete(ticket);
    }

    @Test
    void testDeleteNonExistentTicket() {
        UUID nonExistentTicketId = UUID.randomUUID();
        when(ticketRepository.findById(nonExistentTicketId)).thenReturn(Optional.empty());

        testObserver.reset();

        boolean result = ticketService.deleteTicket(nonExistentTicketId);

        assertFalse(result);
        assertEquals(0, testObserver.notificationCount);
    }

    @Test
    void testMultipleObservers() {
        UUID eventId = UUID.randomUUID();
        TestObserver secondObserver = new TestObserver();
        ticketService.registerObserver(secondObserver);

        Ticket mockTicket = new Ticket(eventId, "VIP", 200.0, 20);
        when(ticketFactory.createTicket(eventId, "VIP", 200.0, 20)).thenReturn(mockTicket);
        when(ticketRepository.save(mockTicket)).thenReturn(mockTicket);

        Ticket ticket = ticketService.createTicket(eventId, "VIP", 200.0, 20);

        assertEquals(1, testObserver.notificationCount);
        assertEquals(1, secondObserver.notificationCount);
        assertEquals(TicketEventType.CREATED, testObserver.lastEvent);
        assertEquals(TicketEventType.CREATED, secondObserver.lastEvent);

        ticketService.removeObserver(secondObserver);
        testObserver.reset();
        secondObserver.reset();

        when(ticketRepository.findById(ticket.getId())).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(ticket)).thenReturn(ticket);

        ticketService.updateTicket(ticket.getId(), 250.0, 15);

        assertEquals(1, testObserver.notificationCount);
        assertEquals(0, secondObserver.notificationCount);
    }
}