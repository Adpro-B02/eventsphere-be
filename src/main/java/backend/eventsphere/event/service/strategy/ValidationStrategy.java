package backend.eventsphere.event.service.strategy;

import backend.eventsphere.event.model.Event;

public interface ValidationStrategy {
    void validate(Event event);
}