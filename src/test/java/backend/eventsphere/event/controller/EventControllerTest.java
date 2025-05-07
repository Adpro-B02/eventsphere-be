package backend.eventsphere.event.controller;

import backend.eventsphere.event.controller.EventController;
import backend.eventsphere.event.model.Event;
import backend.eventsphere.event.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class EventControllerTest {

    private MockMvc mockMvc;
    private EventService eventService;

    @BeforeEach
    void setUp() {
        eventService = Mockito.mock(EventService.class);
        EventController controller = new EventController(eventService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build(); // no Thymeleaf
    }

    @Test
    void testListEvents_ReturnsModelWithEvents() throws Exception {
        Event event = new Event(
                UUID.randomUUID(), "Concert", 50000L,
                LocalDateTime.of(2025, 8, 10, 20, 0),
                "Stadium", "Big concert", "PLANNED",
                "http://example.com/image.jpg"
        );

        when(eventService.getAllEvents()).thenReturn(List.of(event));

        mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("events"))
                .andExpect(view().name("event/events"));
    }
}
