package model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class EventTest {
    @Test
    void testConstructorAndGetters() {
        UUID organizerId = UUID.randomUUID();
        LocalDateTime eventDateTime = LocalDateTime.of(2025, 6, 1, 13, 0);

        Event event = new Event(
                organizerId,
                "Tech Meetup",
                100000L,
                eventDateTime,
                "Surabaya",
                "Meetup developer komunitas regional"
        );

        assertNull(event.getId()); // ID belum diset oleh JPA
        assertEquals(organizerId, event.getOrganizerId());
        assertEquals("Tech Meetup", event.getName());
        assertEquals(100000L, event.getTicketPrice());
        assertEquals(eventDateTime, event.getEventDateTime());
        assertEquals("Surabaya", event.getLocation());
        assertEquals("Meetup developer komunitas regional", event.getDescription());
    }

    @Test
    void testSetters() {
        Event event = new Event();
        UUID organizerId = UUID.randomUUID();
        LocalDateTime eventDateTime = LocalDateTime.of(2025, 7, 10, 14, 30);

        event.setId(UUID.randomUUID());
        event.setOrganizerId(organizerId);
        event.setName("Design Festival");
        event.setTicketPrice(150000L);
        event.setEventDateTime(eventDateTime);
        event.setLocation("Bandung");
        event.setDescription("Festival desain dan inovasi.");

        assertNotNull(event.getId());
        assertEquals(organizerId, event.getOrganizerId());
        assertEquals("Design Festival", event.getName());
        assertEquals(150000L, event.getTicketPrice());
        assertEquals(eventDateTime, event.getEventDateTime());
        assertEquals("Bandung", event.getLocation());
        assertEquals("Festival desain dan inovasi.", event.getDescription());
    }

    @Test
    void testToStringContainsFieldValues() {
        UUID organizerId = UUID.randomUUID();
        LocalDateTime eventDateTime = LocalDateTime.of(2025, 8, 20, 10, 0);

        Event event = new Event(
                organizerId,
                "Startup Day",
                200000L,
                eventDateTime,
                "Jakarta",
                "Pameran startup dari seluruh Indonesia"
        );

        String eventString = event.toString();
        assertTrue(eventString.contains("Startup Day"));
        assertTrue(eventString.contains("Jakarta"));
        assertTrue(eventString.contains("200000"));
    }
}
