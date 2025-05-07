package backend.eventsphere.event.service.strategy;

import backend.eventsphere.event.model.Event;
import backend.eventsphere.event.repository.EventRepository;
import org.springframework.stereotype.Component;

@Component
public class EventExistValidation implements ValidationStrategy {

    private final EventRepository eventRepository;

    public EventExistValidation(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public void validate(Event event) {
        boolean exists = eventRepository.findAll().stream().anyMatch(
                e -> e.getName().equalsIgnoreCase(event.getName()) &&
                        e.getEventDateTime().equals(event.getEventDateTime())
        );
        if (exists) {
            throw new IllegalArgumentException("Event dengan nama dan tanggal tersebut sudah ada.");
        }
    }
}