package backend.eventsphere.ticket.controller;

import backend.eventsphere.ticket.model.Ticket;
import backend.eventsphere.ticket.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.Mockito;

import java.util.*;
import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TicketController.class)
@Import(TicketControllerTest.TestConfig.class)
public class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TicketService ticketService;

    private UUID testTicketId;
    private UUID testEventId;
    private Ticket testTicket;

    @BeforeEach
    void setUp() throws Exception {
        testTicketId = UUID.randomUUID();
        testEventId = UUID.randomUUID();

        testTicket = new Ticket(testEventId, "VIP", 100.0, 50);

        Field idField = Ticket.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(testTicket, testTicketId);
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public TicketService ticketService() {
            return Mockito.mock(TicketService.class);
        }
    }

    @Test
    void testCreateTicket() throws Exception {
        TicketController.CreateTicketRequest request = new TicketController.CreateTicketRequest();
        request.setEventId(testEventId.toString());
        request.setTicketType("VIP");
        request.setTicketPrice(100.0);
        request.setQuota(50);

        when(ticketService.createTicket(any(UUID.class), anyString(), anyDouble(), anyInt()))
                .thenReturn(testTicket);

        mockMvc.perform(post("/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testTicketId.toString()))
                .andExpect(jsonPath("$.eventId").value(testEventId.toString()))
                .andExpect(jsonPath("$.ticketType").value("VIP"))
                .andExpect(jsonPath("$.ticketPrice").value(100.0))
                .andExpect(jsonPath("$.quota").value(50));

        verify(ticketService).createTicket(eq(testEventId), eq("VIP"), eq(100.0), eq(50));
    }

    @Test
    void testCreateTicketInvalid() throws Exception {
        TicketController.CreateTicketRequest request = new TicketController.CreateTicketRequest();
        request.setEventId(testEventId.toString());
        request.setTicketType("VIP");
        request.setTicketPrice(100.0);
        request.setQuota(50);

        when(ticketService.createTicket(any(UUID.class), anyString(), anyDouble(), anyInt()))
                .thenThrow(new IllegalArgumentException("Invalid ticket data"));

        mockMvc.perform(post("/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid ticket data"));
    }

    @Test
    void testGetTicketsByEvent() throws Exception {
        Map<UUID, Ticket> ticketsMap = new HashMap<>();
        ticketsMap.put(testTicketId, testTicket);

        when(ticketService.getTicketsByEvent(testEventId)).thenReturn(ticketsMap);

        mockMvc.perform(get("/tickets/{id_event}", testEventId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testTicketId.toString()))
                .andExpect(jsonPath("$[0].eventId").value(testEventId.toString()))
                .andExpect(jsonPath("$[0].ticketType").value("VIP"))
                .andExpect(jsonPath("$[0].ticketPrice").value(100.0))
                .andExpect(jsonPath("$[0].quota").value(50));

        verify(ticketService).getTicketsByEvent(testEventId);
    }

    @Test
    void testGetTicketsByNonexistentEvent() throws Exception {
        when(ticketService.getTicketsByEvent(any(UUID.class))).thenReturn(Collections.emptyMap());

        mockMvc.perform(get("/tickets/{id_event}", testEventId.toString()))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No tickets found for event"));
    }

    @Test
    void testUpdateTicket() throws Exception {
        TicketController.UpdateTicketRequest request = new TicketController.UpdateTicketRequest();
        request.setTicketPrice(150.0);
        request.setQuota(75);

        Ticket updatedTicket = new Ticket(testEventId, "VIP", 150.0, 75);

        Field idField = Ticket.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(updatedTicket, testTicketId);

        when(ticketService.updateTicket(eq(testTicketId), anyDouble(), anyInt()))
                .thenReturn(updatedTicket);

        mockMvc.perform(put("/tickets/{id_ticket}", testTicketId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testTicketId.toString()))
                .andExpect(jsonPath("$.ticketPrice").value(150.0))
                .andExpect(jsonPath("$.quota").value(75));

        verify(ticketService).updateTicket(testTicketId, 150.0, 75);
    }

    @Test
    void testUpdateNonexistentTicket() throws Exception {
        TicketController.UpdateTicketRequest request = new TicketController.UpdateTicketRequest();
        request.setTicketPrice(150.0);
        request.setQuota(75);

        when(ticketService.updateTicket(any(UUID.class), anyDouble(), anyInt()))
                .thenThrow(new IllegalArgumentException("Ticket not found with ID: " + testTicketId));

        mockMvc.perform(put("/tickets/{id_ticket}", testTicketId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Ticket not found with ID: " + testTicketId));
    }

    @Test
    void testDeleteTicket() throws Exception {
        when(ticketService.deleteTicket(testTicketId)).thenReturn(true);

        mockMvc.perform(delete("/tickets/{id_ticket}", testTicketId.toString()))
                .andExpect(status().isNoContent());

        verify(ticketService).deleteTicket(testTicketId);
    }

    @Test
    void testDeleteNonexistentTicket() throws Exception {
        when(ticketService.deleteTicket(any(UUID.class))).thenReturn(false);

        mockMvc.perform(delete("/tickets/{id_ticket}", testTicketId.toString()))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Ticket not found"));
    }
}