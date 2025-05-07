package backend.eventsphere.event.service;

import backend.eventsphere.event.model.Event;
import backend.eventsphere.event.repository.EventRepository;
import backend.eventsphere.event.service.strategy.ValidationStrategy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final List<ValidationStrategy> validationStrategies;

    public EventService(EventRepository eventRepository, List<ValidationStrategy> validationStrategies) {
        this.eventRepository = eventRepository;
        this.validationStrategies = validationStrategies;
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Event addEvent(Event event) {
        if (event.getStatus() == null) {
            event.setStatus("PLANNED");
        }

        for (ValidationStrategy strategy : validationStrategies) {
            strategy.validate(event);
        }
        return eventRepository.save(event);
    }
}