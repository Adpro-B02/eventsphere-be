package backend.eventsphere.event.service.strategy;

import backend.eventsphere.event.model.Event;
import backend.eventsphere.event.service.strategy.EventDataValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class EventDataValidationTest {

    private EventDataValidation validation;
    private Event baseEvent;

    @BeforeEach
    public void setUp() {
        validation = new EventDataValidation();
        baseEvent = new Event(
                UUID.randomUUID(),
                "Concert",
                50000L,
                LocalDateTime.of(2025, 10, 10, 20, 0),
                "Jakarta",
                "Deskripsi konser",
                "https://example.com/image.jpg"
        );
    }

    @Test
    public void testValidate_validEvent_shouldPass() {
        assertDoesNotThrow(() -> validation.validate(baseEvent));
    }

    @Test
    public void testValidate_nullName_shouldThrow() {
        baseEvent.setName(null);

        assertThatThrownBy(() -> validation.validate(baseEvent))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Nama event tidak boleh kosong.");
    }

    @Test
    public void testValidate_blankName_shouldThrow() {
        baseEvent.setName("   ");

        assertThatThrownBy(() -> validation.validate(baseEvent))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Nama event tidak boleh kosong.");
    }

    @Test
    public void testValidate_nullTicketPrice_shouldThrow() {
        baseEvent.setTicketPrice(null);

        assertThatThrownBy(() -> validation.validate(baseEvent))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Harga tiket harus positif.");
    }

    @Test
    public void testValidate_negativeTicketPrice_shouldThrow() {
        baseEvent.setTicketPrice(-100L);

        assertThatThrownBy(() -> validation.validate(baseEvent))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Harga tiket harus positif.");
    }

    @Test
    public void testValidate_nullEventDateTime_shouldThrow() {
        baseEvent.setEventDateTime(null);

        assertThatThrownBy(() -> validation.validate(baseEvent))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tanggal dan waktu tidak boleh kosong.");
    }

    @Test
    public void testValidate_nullLocation_shouldThrow() {
        baseEvent.setLocation(null);

        assertThatThrownBy(() -> validation.validate(baseEvent))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Lokasi tidak boleh kosong.");
    }

    @Test
    public void testValidate_blankLocation_shouldThrow() {
        baseEvent.setLocation("  ");

        assertThatThrownBy(() -> validation.validate(baseEvent))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Lokasi tidak boleh kosong.");
    }
}
