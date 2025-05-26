package backend.eventsphere.ticket.controller;

import backend.eventsphere.auth.config.JwtUtil;
import backend.eventsphere.config.TestSecurityConfig;
import backend.eventsphere.ticket.model.Ticket;
import backend.eventsphere.ticket.service.TicketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TicketController.class)
@Import(TestSecurityConfig.class)
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TicketService ticketService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID eventId;
    private UUID ticketId;
    private Ticket ticket;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public JwtUtil jwtUtil() {
            return Mockito.mock(JwtUtil.class);
        }
    }

    @BeforeEach
    void setup() {
        eventId = UUID.randomUUID();
        ticketId = UUID.randomUUID();

        ticket = new Ticket(eventId, "VIP", 100.0, 50);
        try {
            var idField = Ticket.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(ticket, ticketId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testCreateTicket_Success() throws Exception {
        TicketController.CreateTicketRequest request = new TicketController.CreateTicketRequest();
        request.setEventId(eventId.toString());
        request.setTicketType("VIP");
        request.setTicketPrice(100.0);
        request.setQuota(50);

        Mockito.when(ticketService.createTicketAsync(eq(eventId), anyString(), anyDouble(), anyInt()))
                .thenReturn(CompletableFuture.completedFuture(ticket));

        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(
                        mockMvc.perform(post("/api/tickets")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                .andReturn()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(ticket.getId().toString())))
                .andExpect(jsonPath("$.eventId", is(ticket.getEventId().toString())))
                .andExpect(jsonPath("$.ticketType", is(ticket.getTicketType())))
                .andExpect(jsonPath("$.ticketPrice", is(ticket.getPrice())))
                .andExpect(jsonPath("$.quota", is(ticket.getQuota())));
    }

    @Test
    void testCreateTicket_ValidationError() throws Exception {
        TicketController.CreateTicketRequest request = new TicketController.CreateTicketRequest();
        request.setEventId(eventId.toString());
        request.setTicketType("VIP");
        request.setTicketPrice(-100.0); // Invalid price
        request.setQuota(50);

        Mockito.when(ticketService.createTicketAsync(eq(eventId), anyString(), anyDouble(), anyInt()))
                .thenReturn(CompletableFuture.failedFuture(
                        new IllegalArgumentException("Price cannot be negative")));

        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(
                        mockMvc.perform(post("/api/tickets")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                .andReturn()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Price cannot be negative")));
    }

    @Test
    void testGetTicketsByEvent_Success() throws Exception {
        Map<UUID, Ticket> ticketsMap = Map.of(ticketId, ticket);

        Mockito.when(ticketService.getTicketsByEventAsync(eventId))
                .thenReturn(CompletableFuture.completedFuture(ticketsMap));

        mockMvc.perform(get("/api/tickets/{id_event}", eventId))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(
                        mockMvc.perform(get("/api/tickets/{id_event}", eventId))
                                .andReturn()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(ticket.getId().toString())))
                .andExpect(jsonPath("$[0].eventId", is(ticket.getEventId().toString())))
                .andExpect(jsonPath("$[0].ticketType", is(ticket.getTicketType())));
    }

    @Test
    void testGetTicketsByEvent_NotFound() throws Exception {
        Mockito.when(ticketService.getTicketsByEventAsync(eventId))
                .thenReturn(CompletableFuture.completedFuture(Collections.emptyMap()));

        mockMvc.perform(get("/api/tickets/{id_event}", eventId))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(
                        mockMvc.perform(get("/api/tickets/{id_event}", eventId))
                                .andReturn()))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("No tickets found for event")));
    }

    @Test
    void testUpdateTicket_Success() throws Exception {
        TicketController.UpdateTicketRequest updateRequest = new TicketController.UpdateTicketRequest();
        updateRequest.setTicketPrice(120.0);
        updateRequest.setQuota(60);

        Ticket updatedTicket = new Ticket(eventId, "VIP", 120.0, 60);
        try {
            var idField = Ticket.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(updatedTicket, ticketId);
        } catch (Exception e) {}

        Mockito.when(ticketService.updateTicketAsync(eq(ticketId), eq(120.0), eq(60)))
                .thenReturn(CompletableFuture.completedFuture(updatedTicket));

        mockMvc.perform(put("/api/tickets/{id_ticket}", ticketId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(
                        mockMvc.perform(put("/api/tickets/{id_ticket}", ticketId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(updateRequest)))
                                .andReturn()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticketPrice", is(120.0)))
                .andExpect(jsonPath("$.quota", is(60)));
    }

    @Test
    void testUpdateTicket_ValidationError() throws Exception {
        TicketController.UpdateTicketRequest updateRequest = new TicketController.UpdateTicketRequest();
        updateRequest.setTicketPrice(-120.0);
        updateRequest.setQuota(60);

        Mockito.when(ticketService.updateTicketAsync(eq(ticketId), eq(-120.0), eq(60)))
                .thenReturn(CompletableFuture.failedFuture(new IllegalArgumentException("Price cannot be negative")));

        mockMvc.perform(put("/api/tickets/{id_ticket}", ticketId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(
                        mockMvc.perform(put("/api/tickets/{id_ticket}", ticketId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(updateRequest)))
                                .andReturn()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Price cannot be negative")));
    }

    @Test
    void testDeleteTicket_Success() throws Exception {
        Mockito.when(ticketService.deleteTicketAsync(ticketId))
                .thenReturn(CompletableFuture.completedFuture(true));

        mockMvc.perform(delete("/api/tickets/{id_ticket}", ticketId))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(
                        mockMvc.perform(delete("/api/tickets/{id_ticket}", ticketId))
                                .andReturn()))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteTicket_NotFound() throws Exception {
        Mockito.when(ticketService.deleteTicketAsync(ticketId))
                .thenReturn(CompletableFuture.completedFuture(false));

        mockMvc.perform(delete("/api/tickets/{id_ticket}", ticketId))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(
                        mockMvc.perform(delete("/api/tickets/{id_ticket}", ticketId))
                                .andReturn()))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Ticket not found")));
    }
}