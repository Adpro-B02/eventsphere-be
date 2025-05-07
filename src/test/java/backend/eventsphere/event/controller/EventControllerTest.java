package backend.eventsphere.event.controller;

import backend.eventsphere.event.model.Event;
import backend.eventsphere.event.service.EventService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
@Import(EventControllerTest.TestConfig.class)
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventService eventService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public EventService eventService() {
            return Mockito.mock(EventService.class);
        }
    }

    @Test
    void testListEvents_ReturnsViewWithEvents() throws Exception {
        Event event = new Event(
                UUID.randomUUID(), "Concert", 50000L,
                LocalDateTime.of(2025, 8, 10, 20, 0),
                "Stadium", "Big concert", "PLANNED",
                "http://example.com/image.jpg"
        );

        when(eventService.getAllEvents()).thenReturn(List.of(event));

        mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(view().name("event/events"))
                .andExpect(model().attributeExists("events"));
    }

    @Test
    void testCreateEventPage_ReturnsCreateFormWithModel() throws Exception {
        mockMvc.perform(get("/events/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("event/createEvent"))
                .andExpect(model().attributeExists("event"));
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
                        .param("status", "PLANNED")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/events"));

        verify(eventService).addEvent(Mockito.any(Event.class));
    }
}
