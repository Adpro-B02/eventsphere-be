package backend.eventsphere.event.event_driven;

import backend.eventsphere.event.event_driven.EventUpdatedEvent;
import backend.eventsphere.event.model.Event;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class EventUpdatedEventTest {

    @Test
    public void testGetEvent() {
        Event event = new Event(
                UUID.randomUUID(),
                "Updated Event",
                15000L,
                null,
                "Updated Location",
                "Updated Description",
                "http://updated.image"
        );

        EventUpdatedEvent eventUpdatedEvent = new EventUpdatedEvent(event);

        assertThat(eventUpdatedEvent.getEvent()).isEqualTo(event);
    }
}
