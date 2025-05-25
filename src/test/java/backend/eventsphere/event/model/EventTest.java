package backend.eventsphere.event.model;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional
class EventTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    void testCreateEventWithoutStatus() {
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

        entityManager.persist(event);
        entityManager.flush();

        assertNotNull(event.getId());
        assertEquals("Tech Meetup", event.getName());
        assertEquals("PLANNED", event.getStatus());
    }

    @Test
    void testCreateEventWithValidStatus() {
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

        entityManager.persist(event);
        entityManager.flush();

        assertEquals("CANCELLED", event.getStatus());
    }

    @Test
    void testCreateEventWithInvalidStatus() {
        UUID organizerId = UUID.randomUUID();
        LocalDateTime dateTime = LocalDateTime.of(2025, 7, 10, 14, 30);

        assertThrows(IllegalArgumentException.class, () -> {
            new Event(
                    organizerId,
                    "Design Festival",
                    150000L,
                    dateTime,
                    "Bandung",
                    "Festival desain dan inovasi.",
                    "INVALID_STATUS",
                    "https://example.com/image3.jpg"
            );
        });
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

    @Test
    void testDefaultConstructor() {
        Event event = new Event();
        assertEquals("PLANNED", event.getStatus());
    }

    @Test
    void testBeanValidationViolations() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Event invalidEvent = new Event();
        invalidEvent.setOrganizerId(UUID.randomUUID());

        Set<ConstraintViolation<Event>> violations = validator.validate(invalidEvent);
        assertFalse(violations.isEmpty());

        List<String> messages = violations.stream().map(ConstraintViolation::getMessage).toList();

        assertTrue(messages.contains("Nama event tidak boleh kosong."));
        assertTrue(messages.contains("Harga tiket tidak boleh kosong."));
        assertTrue(messages.contains("Tanggal dan waktu event tidak boleh kosong."));
        assertTrue(messages.contains("Lokasi event tidak boleh kosong."));
        assertTrue(messages.contains("Deskripsi event tidak boleh kosong."));
        assertTrue(messages.contains("Gambar event tidak boleh kosong."));
    }
}
