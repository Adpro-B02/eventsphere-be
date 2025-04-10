package backend.eventsphere.service.strategy;

import backend.eventsphere.model.Event;
import backend.eventsphere.repository.EventRepository;

public class EventExistValidation implements ValidationStrategy {

    private final EventRepository repository;

    public EventExistValidation(EventRepository repository) {
        this.repository = repository;
    }

    @Override
    public void validate(Event event) {
        if (repository.existsByName(event.getName())) {
            throw new IllegalArgumentException("Event already exists with name: " + event.getName());
        }
    }
}