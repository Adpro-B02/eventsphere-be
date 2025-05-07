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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
}
