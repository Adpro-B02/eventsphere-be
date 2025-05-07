package backend.eventsphere.event.service;

import backend.eventsphere.event.model.Event;
import backend.eventsphere.event.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    private Event event1;
    private Event event2;

    @BeforeEach
    public void setUp() {
        // Setup sample event objects
        event1 = new Event(UUID.randomUUID(), "Event 1", 20000L, LocalDateTime.of(2025, 6, 1, 15, 0), "Location 1", "Description 1", "http://example.com/image1.jpg");
        event2 = new Event(UUID.randomUUID(), "Event 2", 30000L, LocalDateTime.of(2025, 7, 1, 17, 0), "Location 2", "Description 2", "http://example.com/image2.jpg");
    }

    @Test
    public void testGetAllEvents() {
        // Arrange: mock the repository method to return a list of events
        when(eventRepository.findAll()).thenReturn(Arrays.asList(event1, event2));

        // Act: call the service method
        List<Event> events = eventService.getAllEvents();

        // Assert: check that the result is correct
        assertThat(events).hasSize(2);
        assertThat(events).containsExactlyInAnyOrder(event1, event2);

        // Verify the interaction with the repository
        verify(eventRepository, times(1)).findAll();
    }

    @Test
    public void testGetAllEventsWhenNoEvents() {
        // Arrange: mock the repository method to return an empty list
        when(eventRepository.findAll()).thenReturn(Arrays.asList());

        // Act: call the service method
        List<Event> events = eventService.getAllEvents();

        // Assert: check that the result is empty
        assertThat(events).isEmpty();

        // Verify the interaction with the repository
        verify(eventRepository, times(1)).findAll();
    }
}
