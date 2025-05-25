package backend.eventsphere.event.service.strategy;

import backend.eventsphere.event.model.Event;
import backend.eventsphere.event.service.strategy.DeletionValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class DeletionValidationTest {

    private DeletionValidation deletionValidation;
    private Event baseEvent;

    @BeforeEach
    public void setUp() {
        deletionValidation = new DeletionValidation();
        baseEvent = new Event(
                UUID.randomUUID(),
                "Test Event",
                10000L,
                LocalDateTime.now().plusDays(1),
                "Test Location",
                "Test Description",
                "http://example.com/image.jpg"
        );
    }

    @Test
    public void testValidate_whenStatusIsCancelled_shouldPass() {
        baseEvent.setStatus("CANCELLED");

        assertDoesNotThrow(() -> deletionValidation.validate(baseEvent));
    }

    @Test
    public void testValidate_whenStatusIsCompleted_shouldPass() {
        baseEvent.setStatus("COMPLETED");

        assertDoesNotThrow(() -> deletionValidation.validate(baseEvent));
    }

    @Test
    public void testValidate_whenStatusIsPlanned_shouldThrowException() {
        baseEvent.setStatus("PLANNED");

        assertThatThrownBy(() -> deletionValidation.validate(baseEvent))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Event hanya bisa dihapus");
    }

    @Test
    public void testValidate_whenStatusIsOngoing_shouldThrowException() {
        baseEvent.setStatus("PLANNED");

        assertThatThrownBy(() -> deletionValidation.validate(baseEvent))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Event hanya bisa dihapus");
    }
}
