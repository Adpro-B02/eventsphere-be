package backend.eventsphere.event.repository;

import backend.eventsphere.event.model.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    private Event event;

    @BeforeEach
    public void setUp() {
        event = new Event(
                UUID.randomUUID(),
                "Concert",
                50000L,
                LocalDateTime.now().plusDays(1),
                "Stadium",
                "Music concert",
                "http://example.com/image.jpg"
        );
    }

    @Test
    public void testSaveEvent() {
        Event savedEvent = eventRepository.save(event);
        assertThat(savedEvent).isNotNull();
        assertThat(savedEvent.getId()).isNotNull();
    }

    @Test
    public void testFindById() {
        Event savedEvent = eventRepository.save(event);
        Optional<Event> foundEvent = eventRepository.findById(savedEvent.getId());
        assertThat(foundEvent).isPresent();
        assertThat(foundEvent.get().getId()).isEqualTo(savedEvent.getId());
    }

    @Test
    public void testFindAll() {
        Event event1 = new Event(UUID.randomUUID(), "Event 1", 20000L, LocalDateTime.of(2025, 6, 1, 15, 0), "Location 1", "Description 1", "http://example.com/image1.jpg");
        Event event2 = new Event(UUID.randomUUID(), "Event 2", 30000L, LocalDateTime.of(2025, 7, 1, 17, 0), "Location 2", "Description 2", "http://example.com/image2.jpg");
        eventRepository.save(event1);
        eventRepository.save(event2);
        List<Event> events = eventRepository.findAll();
        assertThat(events).hasSize(2);
    }

    @Test
    public void testDeleteById() {
        Event savedEvent = eventRepository.save(event);
        eventRepository.deleteById(savedEvent.getId());
        Optional<Event> foundEvent = eventRepository.findById(savedEvent.getId());
        assertThat(foundEvent).isNotPresent();
    }

    @Test
    public void testUpdateEvent() {
        Event savedEvent = eventRepository.save(event);
        savedEvent.setName("Updated Event");
        savedEvent.setTicketPrice(60000L);
        savedEvent.setLocation("Updated Location");
        Event updatedEvent = eventRepository.save(savedEvent);

        assertThat(updatedEvent.getName()).isEqualTo("Updated Event");
        assertThat(updatedEvent.getTicketPrice()).isEqualTo(60000L);
        assertThat(updatedEvent.getLocation()).isEqualTo("Updated Location");
    }

    @Test
    public void testDefaultStatusIsPlanned() {
        Event savedEvent = eventRepository.save(event);
        assertThat(savedEvent.getStatus()).isEqualTo("PLANNED");
    }

    @Test
    public void testDeleteAll() {
        Event event1 = eventRepository.save(new Event(UUID.randomUUID(), "E1", 10000L, LocalDateTime.of(2025, 6, 1, 10, 0), "Loc1", "Desc1", "http://img1.jpg"));
        Event event2 = eventRepository.save(new Event(UUID.randomUUID(), "E2", 20000L, LocalDateTime.of(2025, 6, 2, 11, 0), "Loc2", "Desc2", "http://img2.jpg"));

        eventRepository.deleteAll();
        List<Event> events = eventRepository.findAll();
        assertThat(events).isEmpty();
    }

    @Test
    public void testExistsById() {
        Event savedEvent = eventRepository.save(event);
        boolean exists = eventRepository.existsById(savedEvent.getId());
        assertThat(exists).isTrue();
    }

    @Test
    public void testCount() {
        eventRepository.save(event);
        long count = eventRepository.count();
        assertThat(count).isEqualTo(1);
    }

    @Test
    public void testSaveEventWithCompletedStatus() {
        Event customStatusEvent = new Event(
                UUID.randomUUID(),
                "Workshop",
                40000L,
                LocalDateTime.of(2025, 10, 1, 14, 0),
                "Coworking Space",
                "Workshop coding",
                "COMPLETED",
                "http://example.com/workshop.jpg"
        );

        Event saved = eventRepository.save(customStatusEvent);
        assertThat(saved.getStatus()).isEqualTo("COMPLETED");
    }
}
