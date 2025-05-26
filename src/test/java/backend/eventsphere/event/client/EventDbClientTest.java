package backend.eventsphere.event.client;

import backend.eventsphere.event.client.EventDbClient;
import backend.eventsphere.event.model.Event;
import backend.eventsphere.event.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EventDbClientTest {

    private EventRepository eventRepository;
    private EventDbClient eventDbClient;

    private Event event1;
    private Event event2;

    @BeforeEach
    public void setUp() {
        eventRepository = mock(EventRepository.class);
        eventDbClient = new EventDbClient(eventRepository);

        event1 = new Event(
                UUID.randomUUID(),
                "Event 1",
                10000L,
                null,
                "Location 1",
                "Desc 1",
                "http://image1.jpg"
        );
        event2 = new Event(
                UUID.randomUUID(),
                "Event 2",
                20000L,
                null,
                "Location 2",
                "Desc 2",
                "http://image2.jpg"
        );
    }

    @Test
    public void testGetAllEvents_returnsList() {
        when(eventRepository.findAll()).thenReturn(List.of(event1, event2));

        List<Event> events = eventDbClient.getAllEvents();

        assertThat(events).hasSize(2).containsExactlyInAnyOrder(event1, event2);
        verify(eventRepository, times(1)).findAll();
    }

    @Test
    public void testGetEventById_eventFound_returnsEvent() {
        UUID id = event1.getId();
        when(eventRepository.findById(id)).thenReturn(Optional.of(event1));

        Event event = eventDbClient.getEventById(id);

        assertThat(event).isEqualTo(event1);
        verify(eventRepository, times(1)).findById(id);
    }

    @Test
    public void testGetEventById_eventNotFound_throwsException() {
        UUID id = UUID.randomUUID();
        when(eventRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventDbClient.getEventById(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event not found");

        verify(eventRepository, times(1)).findById(id);
    }
}
