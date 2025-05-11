package backend.eventsphere.event.service;

import backend.eventsphere.event.model.Event;
import backend.eventsphere.event.repository.EventRepository;
import backend.eventsphere.event.service.strategy.DeletionValidation;
import backend.eventsphere.event.service.strategy.ValidationStrategy;
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

    @Mock
    private ValidationStrategy validationStrategy1;

    @Mock
    private ValidationStrategy validationStrategy2;

    @Mock
    private DeletionValidation deletionValidation;

    private EventService eventService;

    private Event event1;
    private Event event2;

    @BeforeEach
    public void setUp() {
        eventService = new EventService(eventRepository, List.of(validationStrategy1, validationStrategy2));

        UUID organizerId = UUID.randomUUID();
        event1 = new Event(
                organizerId,
                "Event 1",
                20000L,
                LocalDateTime.of(2025, 6, 1, 15, 0),
                "Location 1",
                "Description 1",
                "http://example.com/image1.jpg"
        );

        event2 = new Event(
                organizerId,
                "Event 2",
                30000L,
                LocalDateTime.of(2025, 7, 1, 17, 0),
                "Location 2",
                "Description 2",
                "http://example.com/image2.jpg"
        );
    }

    @Test
    public void testGetAllEvents() {
        when(eventRepository.findAll()).thenReturn(Arrays.asList(event1, event2));
        List<Event> events = eventService.getAllEvents();
        assertThat(events).hasSize(2);
        assertThat(events).containsExactlyInAnyOrder(event1, event2);
        verify(eventRepository, times(1)).findAll();
    }

    @Test
    public void testGetAllEventsWhenNoEvents() {
        when(eventRepository.findAll()).thenReturn(List.of());
        List<Event> events = eventService.getAllEvents();
        assertThat(events).isEmpty();
        verify(eventRepository, times(1)).findAll();
    }

    @Test
    public void testAddEvent_setsPlannedStatusIfNull_andRunsValidation_andSaves() {
        UUID organizerId = UUID.randomUUID();
        Event event = new Event(
                organizerId,
                "Test Event",
                10000L,
                LocalDateTime.of(2025, 8, 1, 14, 0),
                "Jakarta",
                "Desc",
                "https://img.com"
        );

        when(eventRepository.save(event)).thenReturn(event);

        Event saved = eventService.addEvent(event);

        assertThat(saved.getStatus()).isEqualTo("PLANNED");
        verify(validationStrategy1).validate(event);
        verify(validationStrategy2).validate(event);
        verify(eventRepository).save(event);
    }

    @Test
    public void testAddEvent_preservesStatusIfSet_andRunsValidation_andSaves() {
        UUID organizerId = UUID.randomUUID();
        Event event = new Event(
                organizerId,
                "Published Event",
                50000L,
                LocalDateTime.of(2025, 9, 1, 19, 0),
                "Bandung",
                "Some description",
                "https://img.com"
        );
        event.setStatus("CANCELLED");

        when(eventRepository.save(event)).thenReturn(event);

        Event saved = eventService.addEvent(event);

        assertThat(saved.getStatus()).isEqualTo("CANCELLED");
        verify(validationStrategy1).validate(event);
        verify(validationStrategy2).validate(event);
        verify(eventRepository).save(event);
    }

    @Test
    public void testDeleteEventById_validStatus_shouldDelete() {
        UUID eventId = UUID.randomUUID();
        event1.setStatus("CANCELLED");

        when(eventRepository.findById(eventId)).thenReturn(java.util.Optional.of(event1));

        eventService = new EventService(eventRepository, List.of(deletionValidation, validationStrategy1));

        eventService.deleteEventById(eventId);

        verify(deletionValidation).validate(event1);
        verify(eventRepository).deleteById(eventId);
    }

    @Test
    public void testDeleteEventById_eventNotFound_shouldThrow() {
        UUID eventId = UUID.randomUUID();

        when(eventRepository.findById(eventId)).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> eventService.deleteEventById(eventId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Event tidak ditemukan");

        verify(eventRepository, never()).deleteById(any());
    }

    @Test
    public void testDeleteEventById_otherStrategiesShouldNotRun() {
        UUID eventId = UUID.randomUUID();
        event1.setStatus("COMPLETED");

        when(eventRepository.findById(eventId)).thenReturn(java.util.Optional.of(event1));

        eventService = new EventService(eventRepository, List.of(validationStrategy1, deletionValidation));

        eventService.deleteEventById(eventId);

        verify(deletionValidation).validate(event1);
        verify(validationStrategy1, never()).validate(event1);
        verify(eventRepository).deleteById(eventId);
    }

    @Test
    public void testUpdateEvent_shouldUpdateFields_andRunValidation_andSave() {
        UUID eventId = UUID.randomUUID();
        Event existingEvent = new Event(
                UUID.randomUUID(), "Old Name", 10000L,
                LocalDateTime.of(2025, 6, 1, 12, 0),
                "Old Location", "Old Description",
                "http://old.image.jpg"
        );
        existingEvent.setStatus("PLANNED");

        Event updatedEvent = new Event(
                existingEvent.getOrganizerId(), "New Name", 20000L,
                LocalDateTime.of(2025, 6, 2, 14, 0),
                "New Location", "New Description",
                "http://new.image.jpg"
        );
        updatedEvent.setStatus("COMPLETED");

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(existingEvent));
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Event result = eventService.updateEvent(eventId, updatedEvent);

        assertThat(result.getName()).isEqualTo("New Name");
        assertThat(result.getTicketPrice()).isEqualTo(20000L);
        assertThat(result.getEventDateTime()).isEqualTo(LocalDateTime.of(2025, 6, 2, 14, 0));
        assertThat(result.getLocation()).isEqualTo("New Location");
        assertThat(result.getDescription()).isEqualTo("New Description");
        assertThat(result.getLink_image()).isEqualTo("http://new.image.jpg");
        assertThat(result.getStatus()).isEqualTo("COMPLETED");

        verify(validationStrategy1).validate(existingEvent);
        verify(validationStrategy2).validate(existingEvent);
        verify(eventRepository).save(existingEvent);
    }
}
