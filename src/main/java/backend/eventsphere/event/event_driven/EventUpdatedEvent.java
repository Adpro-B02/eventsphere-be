package backend.eventsphere.event.event_driven;

import backend.eventsphere.event.model.Event;

public class EventUpdatedEvent {
    private final Event event;

    public EventUpdatedEvent(Event event) {
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }
}

