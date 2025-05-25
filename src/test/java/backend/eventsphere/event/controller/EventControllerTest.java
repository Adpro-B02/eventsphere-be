package backend.eventsphere.event.controller;

import backend.eventsphere.auth.config.JwtUtil;
import backend.eventsphere.auth.repository.UserRepository;
import backend.eventsphere.auth.service.UserService;
import backend.eventsphere.config.TestSecurityConfig;
import backend.eventsphere.event.model.Event;
import backend.eventsphere.event.service.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
@Import(TestSecurityConfig.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public JwtUtil jwtUtil() {
            return Mockito.mock(JwtUtil.class);
        }
    }

    @Test
    void testListEventsAsync_ReturnsViewWithEvents() throws Exception {
        Event event = new Event(
                UUID.randomUUID(), "Concert", 50000L,
                LocalDateTime.of(2025, 8, 10, 20, 0),
                "Stadium", "Big concert", "PLANNED",
                "http://example.com/image.jpg"
        );

        when(eventService.getAllEventsAsync())
                .thenReturn(CompletableFuture.completedFuture(List.of(event)));

        mockMvc.perform(get("/events/"))
                .andExpect(status().isOk())
                .andExpect(view().name("event/events"))
                .andExpect(model().attributeExists("events"));
    }

    @Test
    void testCreateEventPost_SavesEventAndRedirects() throws Exception {
        mockMvc.perform(post("/events/create")
                        .param("organizerId", "00000000-0000-0000-0000-000000000001")
                        .param("name", "Test Event")
                        .param("ticketPrice", "25000")
                        .param("eventDateTime", "2025-08-15T18:00")
                        .param("location", "Jakarta")
                        .param("description", "Event Desc")
                        .param("link_image", "http://img.com/img.jpg")
                        .param("status", "PLANNED"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/events/"));

        verify(eventService).addEvent(Mockito.any(Event.class));
    }

    @Test
    void testDeleteEvent_DeletesEventAndRedirects() throws Exception {
        UUID eventId = UUID.randomUUID();

        mockMvc.perform(post("/events/delete")
                        .param("id", eventId.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/events/"));

        verify(eventService).deleteEventById(eventId);
    }

    @Test
    void testUpdateEventPage_ReturnsUpdateFormWithModel() throws Exception {
        UUID eventId = UUID.randomUUID();
        Event event = new Event(
                eventId,
                "Sample Event",
                15000L,
                LocalDateTime.of(2025, 8, 20, 14, 30),
                "Surabaya",
                "Event Description",
                "PLANNED",
                "http://example.com/image.jpg"
        );

        when(eventService.getEventById(eventId)).thenReturn(event);

        mockMvc.perform(get("/events/update").param("id", eventId.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("event/updateEvent"))
                .andExpect(model().attributeExists("event"))
                .andExpect(model().attributeExists("formattedEventDateTime"))
                .andExpect(model().attribute("event", event));
    }

    @Test
    void testUpdateEventPost_UpdatesEventAndRedirects() throws Exception {
        UUID eventId = UUID.randomUUID();

        mockMvc.perform(post("/events/update")
                        .param("id", eventId.toString())
                        .param("organizerId", "00000000-0000-0000-0000-000000000001")
                        .param("name", "Updated Event")
                        .param("ticketPrice", "30000")
                        .param("eventDateTime", "2025-08-25T10:00")
                        .param("location", "Bandung")
                        .param("description", "Updated Desc")
                        .param("link_image", "http://img.com/updated.jpg")
                        .param("status", "COMPLETED")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/events/"));

        verify(eventService).updateEvent(Mockito.eq(eventId), Mockito.any(Event.class));
    }

    @Test
    void testCreateEventPost_HasValidationErrors_ReturnsForm() throws Exception {
        mockMvc.perform(post("/events/create")
                        .param("name", "")
                        .param("ticketPrice", "-1")
                        .param("eventDateTime", "2020-01-01T10:00")
                        .param("location", "")
                        .param("description", "")
                        .param("link_image", "")
                        .param("status", "")
                        .param("organizerId", "00000000-0000-0000-0000-000000000001"))
                .andExpect(status().isOk())
                .andExpect(view().name("event/createEvent"))
                .andExpect(model().attributeHasFieldErrors("event", "name", "ticketPrice", "eventDateTime", "location", "description", "link_image", "status"));
    }

    @Test
    void testCreateEventPost_ThrowsIllegalArgumentException_ReturnsFormWithError() throws Exception {
        Mockito.doThrow(new IllegalArgumentException("Event dengan nama dan tanggal tersebut sudah ada."))
                .when(eventService).addEvent(Mockito.any(Event.class));

        mockMvc.perform(post("/events/create")
                        .param("organizerId", "00000000-0000-0000-0000-000000000001")
                        .param("name", "Test Event")
                        .param("ticketPrice", "25000")
                        .param("eventDateTime", "2025-08-15T18:00")
                        .param("location", "Jakarta")
                        .param("description", "Event Desc")
                        .param("link_image", "http://img.com/img.jpg")
                        .param("status", "PLANNED"))
                .andExpect(status().isOk())
                .andExpect(view().name("event/createEvent"))
                .andExpect(model().attributeHasFieldErrors("event", "name"));
    }

    @Test
    void testUpdateEventPost_HasValidationErrors_ReturnsForm() throws Exception {
        mockMvc.perform(post("/events/update")
                        .param("id", UUID.randomUUID().toString())
                        .param("name", "")
                        .param("ticketPrice", "-1")
                        .param("eventDateTime", "2020-08-15T18:00")
                        .param("location", "")
                        .param("description", "")
                        .param("link_image", "")
                        .param("status", "")
                        .param("organizerId", "00000000-0000-0000-0000-000000000001"))
                .andExpect(status().isOk())
                .andExpect(view().name("event/updateEvent"))
                .andExpect(model().attributeHasFieldErrors("event", "name", "ticketPrice", "eventDateTime", "location", "description", "link_image", "status"));
    }

    @Test
    void testUpdateEventPost_ThrowsIllegalArgumentException_ReturnsFormWithError() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.doThrow(new IllegalArgumentException("Event sudah ada."))
                .when(eventService).updateEvent(Mockito.eq(id), Mockito.any(Event.class));

        mockMvc.perform(post("/events/update")
                        .param("id", id.toString())
                        .param("organizerId", "00000000-0000-0000-0000-000000000001")
                        .param("name", "Conflict Event")
                        .param("ticketPrice", "50000")
                        .param("eventDateTime", "2025-12-15T18:00")
                        .param("location", "Bali")
                        .param("description", "Description")
                        .param("link_image", "http://img.com/img.jpg")
                        .param("status", "PLANNED"))
                .andExpect(status().isOk())
                .andExpect(view().name("event/updateEvent"))
                .andExpect(model().attributeHasFieldErrors("event", "name"));
    }

    @Test
    void testDeleteEvent_ThrowsIllegalStateException_RedirectsWithWarning() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.doThrow(new IllegalStateException("Tidak bisa dihapus."))
                .when(eventService).deleteEventById(id);

        mockMvc.perform(post("/events/delete").param("id", id.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/events/"));
    }

    @Test
    void testDeleteEvent_ThrowsGenericException_RedirectsWithError() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.doThrow(new RuntimeException("Unknown error"))
                .when(eventService).deleteEventById(id);

        mockMvc.perform(post("/events/delete").param("id", id.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/events/"));
    }
}