package backend.eventsphere.event.event_driven;

import backend.eventsphere.event.event_driven.EventDeletedEvent;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class EventDeletedEventTest {

    @Test
    public void testGetEventId() {
        UUID id = UUID.randomUUID();
        EventDeletedEvent eventDeletedEvent = new EventDeletedEvent(id);

        assertThat(eventDeletedEvent.getEventId()).isEqualTo(id);
    }
}
