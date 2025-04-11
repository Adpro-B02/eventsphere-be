package backend.eventsphere.service.strategy;

import backend.eventsphere.model.Event;

public interface ValidationStrategy {
    void validate(Event event);
}