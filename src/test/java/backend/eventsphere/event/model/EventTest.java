package backend.eventsphere.event.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EventTest {

    @Test
    void testConstructorWithoutStatus() {
        UUID organizerId = UUID.randomUUID();
        LocalDateTime dateTime = LocalDateTime.of(2025, 6, 1, 13, 0);

        Event event = new Event(
                organizerId,
                "Tech Meetup",
                100000L,
                dateTime,
                "Surabaya",
                "Meetup developer komunitas regional",
                "https://example.com/image.jpg"
        );

        assertNotNull(event.getId());
        assertEquals(organizerId, event.getOrganizerId());
        assertEquals("Tech Meetup", event.getName());
        assertEquals(100000L, event.getTicketPrice());
        assertEquals(dateTime, event.getEventDateTime());
        assertEquals("Surabaya", event.getLocation());
        assertEquals("Meetup developer komunitas regional", event.getDescription());
        assertEquals("https://example.com/image.jpg", event.getLink_image());
        assertEquals("PLANNED", event.getStatus());
    }

    @Test
    void testConstructorWithValidStatus() {
        UUID organizerId = UUID.randomUUID();
        LocalDateTime dateTime = LocalDateTime.of(2025, 7, 10, 14, 30);

        Event event = new Event(
                organizerId,
                "Design Festival",
                150000L,
                dateTime,
                "Bandung",
                "Festival desain dan inovasi.",
                "CANCELLED",
                "https://example.com/image2.jpg"
        );

        assertEquals("CANCELLED", event.getStatus());
    }

    @Test
    void testConstructorWithInvalidStatus() {
        UUID organizerId = UUID.randomUUID();
        LocalDateTime dateTime = LocalDateTime.of(2025, 7, 10, 14, 30);

        assertThrows(IllegalArgumentException.class, () -> new Event(
                organizerId,
                "Design Festival",
                150000L,
                dateTime,
                "Bandung",
                "Festival desain dan inovasi.",
                "INVALID_STATUS",
                "https://example.com/image3.jpg"
        ));
    }

    @Test
    void testSetStatusWithValidValue() {
        Event event = new Event(
                UUID.randomUUID(),
                "Test Event",
                120000L,
                LocalDateTime.now(),
                "Jakarta",
                "Test description",
                "https://example.com/test.jpg"
        );

        event.setStatus("COMPLETED");
        assertEquals("COMPLETED", event.getStatus());
    }

    @Test
    void testSetStatusWithInvalidValue() {
        Event event = new Event(
                UUID.randomUUID(),
                "Test Event",
                120000L,
                LocalDateTime.now(),
                "Jakarta",
                "Test description",
                "https://example.com/test.jpg"
        );

        assertThrows(IllegalArgumentException.class, () -> event.setStatus("INVALID"));
    }

    @Test
    void testToStringReturnsName() {
        Event event = new Event(
                UUID.randomUUID(),
                "Startup Day",
                200000L,
                LocalDateTime.of(2025, 8, 20, 10, 0),
                "Jakarta",
                "Pameran startup dari seluruh Indonesia",
                "https://example.com/startup.jpg"
        );

        assertEquals("Startup Day", event.toString());
    }
}
