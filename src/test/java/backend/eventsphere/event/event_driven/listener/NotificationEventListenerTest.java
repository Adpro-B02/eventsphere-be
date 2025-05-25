package backend.eventsphere.event.event_driven.listener;

import backend.eventsphere.event.event_driven.*;
import backend.eventsphere.event.model.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class NotificationEventListenerTest {

    private NotificationEventListener listener;

    @BeforeEach
    public void setUp() {
        listener = new NotificationEventListener();
    }

    @Test
    public void testHandleEventCreated() {
        Event event = new Event();
        event.setName("Created Event");
        EventCreatedEvent createdEvent = new EventCreatedEvent(event);

        listener.handleEventCreated(createdEvent);
    }

    @Test
    public void testHandleEventUpdated() {
        Event event = new Event();
        event.setName("Updated Event");
        EventUpdatedEvent updatedEvent = new EventUpdatedEvent(event);

        listener.handleEventUpdated(updatedEvent);
    }

    @Test
    public void testHandleEventDeleted() {
        UUID eventId = UUID.randomUUID();
        EventDeletedEvent deletedEvent = new EventDeletedEvent(eventId);

        listener.handleEventDeleted(deletedEvent);
    }
}

