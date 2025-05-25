package backend.eventsphere.event.event_driven;

import backend.eventsphere.event.model.Event;

public class EventCreatedEvent {
    private final Event event;

    public EventCreatedEvent(Event event) {
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }
}
