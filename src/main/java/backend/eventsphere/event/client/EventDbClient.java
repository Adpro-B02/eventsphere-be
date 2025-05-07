package backend.eventsphere.event.client;

import backend.eventsphere.event.model.Event;
import backend.eventsphere.event.repository.EventRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class EventDbClient implements EventClient {

    private final EventRepository eventRepository;

    public EventDbClient(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    public Event getEventById(UUID id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));
    }
}