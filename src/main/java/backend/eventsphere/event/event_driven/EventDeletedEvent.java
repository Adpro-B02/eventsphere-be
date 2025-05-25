package backend.eventsphere.event.event_driven;

import java.util.UUID;

public class EventDeletedEvent {
    private final UUID eventId;

    public EventDeletedEvent(UUID eventId) {
        this.eventId = eventId;
    }

    public UUID getEventId() {
        return eventId;
    }
}
