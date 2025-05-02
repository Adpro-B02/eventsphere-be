package backend.eventsphere.event.repository;

import backend.eventsphere.event.model.Event;
import backend.eventsphere.event.repository.EventRepository;
import backend.eventsphere.event.repository.EventRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EventRepositoryTest {

    private EventRepository repository;

    @BeforeEach
    void setUp() {
        repository = new EventRepositoryImpl();
    }

    @Test
    void testSaveAndFindById() {
        Event event = createSampleEvent("Tech Talk");
        repository.save(event);

        Optional<Event> found = repository.findById(event.getId());
        assertTrue(found.isPresent());
        assertEquals(event.getName(), found.get().getName());
    }

    @Test
    void testFindAll() {
        repository.save(createSampleEvent("Event 1"));
        repository.save(createSampleEvent("Event 2"));

        List<Event> events = repository.findAll();
        assertEquals(2, events.size());
    }

    @Test
    void testDeleteById() {
        Event event = createSampleEvent("To Delete");
        repository.save(event);
        repository.deleteById(event.getId());

        assertFalse(repository.findById(event.getId()).isPresent());
    }

    @Test
    void testExistsByName() {
        Event event = createSampleEvent("Hackathon");
        repository.save(event);

        assertTrue(repository.existsByName("Hackathon"));
        assertFalse(repository.existsByName("Random"));
    }

    private Event createSampleEvent(String name) {
        return new Event(
                UUID.randomUUID(),
                name,
                50000L,
                LocalDateTime.now().plusDays(1),
                "Bandung",
                "Sample event"
        );
    }
}
