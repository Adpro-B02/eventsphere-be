package com.example.event.controller;

import com.example.event.model.Event;
import com.example.event.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
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

        ResponseEntity<List<Event>> response = eventController.getAllEvents();

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isEqualTo(events);
        verify(eventService, times(1)).getAllEvents();
    }

    @Test
    void testCreateEvent() {
        Event newEvent = new Event(UUID.randomUUID(), "New Event", 15000L, LocalDateTime.now(), "Surabaya", "Deskripsi");
        when(eventService.createEvent(any(Event.class))).thenReturn(newEvent);

        ResponseEntity<Event> response = eventController.createEvent(newEvent);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isEqualTo(newEvent);
        verify(eventService, times(1)).createEvent(newEvent);
    }

    @Test
    void testUpdateEvent() {
        UUID eventId = UUID.randomUUID();
        Event updatedEvent = new Event(UUID.randomUUID(), "Updated", 99999L, LocalDateTime.now(), "Jogja", "Update deskripsi");
        when(eventService.updateEvent(eq(eventId), any(Event.class))).thenReturn(updatedEvent);

        ResponseEntity<Event> response = eventController.updateEvent(eventId, updatedEvent);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isEqualTo(updatedEvent);
        verify(eventService, times(1)).updateEvent(eventId, updatedEvent);
    }

    @Test
    void testDeleteEvent() {
        UUID eventId = UUID.randomUUID();
        when(eventService.deleteEvent(eventId)).thenReturn(true);

        ResponseEntity<String> response = eventController.deleteEvent(eventId);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isEqualTo("Event deleted successfully");
        verify(eventService, times(1)).deleteEvent(eventId);
    }

    @Test
    void testDeleteEventFails() {
        UUID eventId = UUID.randomUUID();
        when(eventService.deleteEvent(eventId)).thenReturn(false);

        ResponseEntity<String> response = eventController.deleteEvent(eventId);

        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
        assertThat(response.getBody()).isEqualTo("Event cannot be deleted");
        verify(eventService, times(1)).deleteEvent(eventId);
    }
}
