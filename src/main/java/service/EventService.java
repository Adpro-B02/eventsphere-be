package service;

import model.Event;
import repository.EventRepository;
import service.strategy.ValidationStrategy;

import java.util.List;
import java.util.UUID;

public class EventService {

    private final EventRepository repository;

    public EventService(EventRepository repository) {
        this.repository = repository;
    }

    public Event createEvent(Event event, List<ValidationStrategy> strategies) {
        for (ValidationStrategy strategy : strategies) {
            strategy.validate(event);
        }
        return repository.save(event);
    }

    public List<Event> getAllEvents() {
        return repository.findAll();
    }

    public EventRepository getRepository() {
        return repository;
    }


    public void deleteEvent(UUID eventId, ValidationStrategy strategy) {
        Event event = repository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        strategy.validate(event);
        repository.deleteById(eventId);
    }

    public Event updateEvent(UUID id, Event updatedEvent, List<ValidationStrategy> strategies) {
        Event existing = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        for (ValidationStrategy strategy : strategies) {
            strategy.validate(updatedEvent);
        }

        // Hapus lama dan ganti baru (karena in-memory)
        repository.deleteById(id);
        return repository.save(updatedEvent);
    }
}
