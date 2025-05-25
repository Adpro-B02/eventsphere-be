package backend.eventsphere.ticket.model;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TicketTest {
    @Test
    void testCreateValidTicket() {
        UUID eventId = UUID.randomUUID();
        String ticketType = "VIP";
        Double price = 100000.0;
        Integer quota = 50;

        Ticket ticket = new Ticket(eventId, ticketType, price, quota);

        assertNotNull(ticket);
        assertNotNull(ticket.getId());
        assertEquals(eventId, ticket.getEventId());
        assertEquals(ticketType, ticket.getTicketType());
        assertEquals(price, ticket.getPrice());
        assertEquals(quota, ticket.getQuota());
    }

    @Test
    void testCreateTicketWithEmptyTicketType() {
        assertThrows(IllegalArgumentException.class, () -> {
            Ticket ticket = new Ticket(UUID.randomUUID(), "", 100000.0, 50);
        });
    }

    @Test
    void testCreateTicketWithNegativePrice() {
        assertThrows(IllegalArgumentException.class, () -> {
            Ticket ticket = new Ticket(UUID.randomUUID(), "VIP", -50.0, 50);
        });
    }

    @Test
    void testCreateTicketWithNegativeQuota() {
        assertThrows(IllegalArgumentException.class, () -> {
            Ticket ticket = new Ticket(UUID.randomUUID(), "VIP", 100.0, -10);
        });
    }
}
