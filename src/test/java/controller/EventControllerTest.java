package controller;

import model.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import service.EventService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EventControllerTest {

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventController eventController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllEvents() {
        List<Event> events = List.of(
                new Event(UUID.randomUUID(), "Event A", 10000L, LocalDateTime.now(), "Jakarta", "Deskripsi A"),
                new Event(UUID.randomUUID(), "Event B", 20000L, LocalDateTime.now(), "Bandung", "Deskripsi B")
        );

        when(eventService.getAllEvents()).thenReturn(events);

        List<Event> result = eventController.getAll();

        assertThat(result).isEqualTo(events);
        verify(eventService, times(1)).getAllEvents();
    }

    @Test
    void testCreateEvent() {
        Event newEvent = new Event(UUID.randomUUID(), "New Event", 15000L, LocalDateTime.now(), "Surabaya", "Deskripsi");

        when(eventService.createEvent(any(Event.class), anyList())).thenReturn(newEvent);

        ResponseEntity<Event> response = eventController.create(newEvent);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isEqualTo(newEvent);
        verify(eventService, times(1)).createEvent(eq(newEvent), anyList());
    }

    @Test
    void testUpdateEvent() {
        UUID eventId = UUID.randomUUID();
        Event updatedEvent = new Event(UUID.randomUUID(), "Updated Event", 20000L, LocalDateTime.now(), "Jogja", "Updated desc");

        when(eventService.updateEvent(eq(eventId), any(Event.class), anyList())).thenReturn(updatedEvent);

        ResponseEntity<Event> response = eventController.update(eventId, updatedEvent);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isEqualTo(updatedEvent);
        verify(eventService, times(1)).updateEvent(eq(eventId), eq(updatedEvent), anyList());
    }

    @Test
    void testDeleteEvent() {
        UUID eventId = UUID.randomUUID();

        // deleteEvent tidak return apapun, hanya validasi & eksekusi
        doNothing().when(eventService).deleteEvent(eq(eventId), any());

        ResponseEntity<Void> response = eventController.delete(eventId);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNull(); // karena noContent
        verify(eventService, times(1)).deleteEvent(eq(eventId), any());
    }
}
