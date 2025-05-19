package backend.eventsphere.event.event_driven;

import backend.eventsphere.event.event_driven.EventCreatedEvent;
import backend.eventsphere.event.model.Event;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class EventCreatedEventTest {

    @Test
    public void testGetEvent() {
        Event event = new Event(
                UUID.randomUUID(),
                "Test Event",
                10000L,
                null,
                "Test Location",
                "Test Description",
                "http://test.image"
        );

        EventCreatedEvent eventCreatedEvent = new EventCreatedEvent(event);

        assertThat(eventCreatedEvent.getEvent()).isEqualTo(event);
    }
}
