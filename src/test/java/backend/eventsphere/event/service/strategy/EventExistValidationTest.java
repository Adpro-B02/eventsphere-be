package backend.eventsphere.event.service.strategy;

import backend.eventsphere.event.client.EventClient;
import backend.eventsphere.event.model.Event;
import backend.eventsphere.event.service.strategy.EventExistValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EventExistValidationTest {

    private EventClient eventClient;
    private EventExistValidation validation;

    private Event targetEvent;

    @BeforeEach
    public void setUp() {
        eventClient = mock(EventClient.class);
        validation = new EventExistValidation(eventClient);

        targetEvent = new Event(
                UUID.randomUUID(),
                "Tech Conference",
                100000L,
                LocalDateTime.now().plusDays(1),
                "Jakarta",
                "A tech event",
                "https://img.com/event.jpg"
        );
    }

    @Test
    public void testValidate_eventDoesNotExist_shouldPass() {
        Event other = new Event(
                UUID.randomUUID(),
                "Different Event",
                100000L,
                LocalDateTime.of(2025, 9, 16, 10, 0),
                "Bandung",
                "Another event",
                "https://img.com/event2.jpg"
        );

        when(eventClient.getAllEvents()).thenReturn(List.of(other));

        assertDoesNotThrow(() -> validation.validate(targetEvent));
    }

    @Test
    public void testValidate_eventWithSameNameAndDate_butSameId_shouldPass() {
        Event sameEvent = new Event(
                targetEvent.getId(),
                "Tech Conference",
                200000L,
                targetEvent.getEventDateTime(),
                "Jakarta",
                "Updated",
                "https://img.com"
        );

        when(eventClient.getAllEvents()).thenReturn(List.of(sameEvent));

        assertDoesNotThrow(() -> validation.validate(targetEvent));
    }
}
