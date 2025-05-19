package backend.eventsphere.event.service;

import backend.eventsphere.event.event_driven.EventCreatedEvent;
import backend.eventsphere.event.event_driven.EventDeletedEvent;
import backend.eventsphere.event.event_driven.EventUpdatedEvent;
import backend.eventsphere.event.model.Event;
import backend.eventsphere.event.repository.EventRepository;
import backend.eventsphere.event.service.strategy.DeletionValidation;
import backend.eventsphere.event.service.strategy.ValidationStrategy;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class EventService {

    private final ApplicationEventPublisher publisher;
    private final EventRepository eventRepository;
    private final List<ValidationStrategy> validationStrategies;

    public EventService(EventRepository eventRepository, List<ValidationStrategy> validationStrategies, ApplicationEventPublisher publisher) {
        this.eventRepository = eventRepository;
        this.validationStrategies = validationStrategies;
        this.publisher = publisher;
    }

    @Async
    public CompletableFuture<List<Event>> getAllEventsAsync() {
        List<Event> events = eventRepository.findAll();
        return CompletableFuture.completedFuture(events);
    }

    public Event addEvent(Event event) {
        if (event.getStatus() == null) {
            event.setStatus("PLANNED");
        }

        for (ValidationStrategy strategy : validationStrategies) {
            if (!(strategy instanceof DeletionValidation)) {
                strategy.validate(event);
            }
        }
        Event savedEvent = eventRepository.save(event);
        publisher.publishEvent(new EventCreatedEvent(savedEvent));
        return savedEvent;
    }

    public void deleteEventById(UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event tidak ditemukan"));

        for (ValidationStrategy strategy : validationStrategies) {
            if (strategy instanceof DeletionValidation) {
                strategy.validate(event);
            }
        }

        eventRepository.deleteById(eventId);
        publisher.publishEvent(new EventDeletedEvent(eventId));
    }

    public Event updateEvent(UUID eventId, Event updatedEvent) {
        Event existingEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event tidak ditemukan"));

        existingEvent.setName(updatedEvent.getName());
        existingEvent.setTicketPrice(updatedEvent.getTicketPrice());
        existingEvent.setEventDateTime(updatedEvent.getEventDateTime());
        existingEvent.setLocation(updatedEvent.getLocation());
        existingEvent.setDescription(updatedEvent.getDescription());
        existingEvent.setLink_image(updatedEvent.getLink_image());
        existingEvent.setStatus(updatedEvent.getStatus());

        for (ValidationStrategy strategy : validationStrategies) {
            if (!(strategy instanceof DeletionValidation)) {
                strategy.validate(existingEvent);
            }
        }

        Event saved = eventRepository.save(existingEvent);
        publisher.publishEvent(new EventUpdatedEvent(saved));
        return saved;
    }

    public Event getEventById(UUID id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event tidak ditemukan"));
    }
}