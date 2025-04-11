package backend.eventsphere.service;

import backend.eventsphere.service.EventService;
import backend.eventsphere.service.strategy.DeletionValidation;
import backend.eventsphere.service.strategy.EventDataValidation;
import backend.eventsphere.service.strategy.EventExistValidation;
import backend.eventsphere.model.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import backend.eventsphere.repository.EventRepositoryImpl;
import backend.eventsphere.repository.EventRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EventServiceTest {

    private EventRepository repository;
    private EventService service;

    @BeforeEach
    void setUp() {
        repository = new EventRepositoryImpl();
        service = new EventService(repository);
    }

    @Test
    void testCreateEventWithValidData() {
        Event event = sampleEvent("New Event");

        Event created = service.createEvent(event, List.of(
                new EventExistValidation(repository),
                new EventDataValidation()
        ));

        assertNotNull(created);
        assertEquals("New Event", created.getName());
    }

    @Test
    void testCreateEventFailsIfDuplicate() {
        Event event = sampleEvent("Same Name");
        repository.save(event);

        Event duplicate = sampleEvent("Same Name");

        assertThrows(IllegalArgumentException.class, () -> service.createEvent(duplicate, List.of(
                new EventExistValidation(repository)
        )));
    }

    @Test
    void testDeleteEventOnlyIfCancelled() {
        Event event = sampleEvent("To Delete");
        event.setStatus("CANCELLED");
        repository.save(event);

        assertDoesNotThrow(() -> service.deleteEvent(event.getId(), new DeletionValidation()));
    }

    @Test
    void testDeleteEventFailsIfNotCancelled() {
        Event event = sampleEvent("Cannot Delete");
        event.setStatus("PLANNED");
        repository.save(event);

        assertThrows(IllegalStateException.class, () -> service.deleteEvent(event.getId(), new DeletionValidation()));
    }

    private Event sampleEvent(String name) {
        return new Event(
                UUID.randomUUID(),
                name,
                50000L,
                LocalDateTime.now().plusDays(2),
                "Bandung",
                "Deskripsi"
        );
    }
}