package backend.eventsphere.ticket.controller;

import backend.eventsphere.ticket.model.Ticket;
import backend.eventsphere.ticket.service.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<?>> createTicket(@RequestBody CreateTicketRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Ticket ticket = ticketService.createTicket(
                    UUID.fromString(request.getEventId()),
                    request.getTicketType(),
                    request.getTicketPrice(),
                    request.getQuota()
                );
                return ResponseEntity.ok(mapToResponse(ticket));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating ticket: " + e.getMessage());
            }
        });
    }

    @GetMapping("/{id_event}")
    public CompletableFuture<ResponseEntity<?>> getTicketsByEvent(@PathVariable("id_event") String eventId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Map<UUID, Ticket> tickets = ticketService.getTicketsByEvent(UUID.fromString(eventId));
                if (tickets.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No tickets found for event");
                }
                return ResponseEntity.ok(tickets.values().stream()
                    .map(this::mapToResponse)
                    .toList());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving tickets: " + e.getMessage());
            }
        });
    }

    @PutMapping("/{id_ticket}")
    public CompletableFuture<ResponseEntity<?>> updateTicket(
            @PathVariable("id_ticket") String ticketId,
            @RequestBody UpdateTicketRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Ticket ticket = ticketService.updateTicket(
                    UUID.fromString(ticketId),
                    request.getTicketPrice(),
                    request.getQuota()
                );
                return ResponseEntity.ok(mapToResponse(ticket));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating ticket: " + e.getMessage());
            }
        });
    }

    @DeleteMapping("/{id_ticket}")
    public CompletableFuture<ResponseEntity<?>> deleteTicket(@PathVariable("id_ticket") String ticketId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                boolean deleted = ticketService.deleteTicket(UUID.fromString(ticketId));
                if (deleted) {
                    return ResponseEntity.noContent().build();
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ticket not found");
                }
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting ticket: " + e.getMessage());
            }
        });
    }

    private TicketResponse mapToResponse(Ticket ticket) {
        TicketResponse response = new TicketResponse();
        response.setId(ticket.getId().toString());
        response.setEventId(ticket.getEventId().toString());
        response.setTicketType(ticket.getTicketType());
        response.setTicketPrice(ticket.getPrice());
        response.setQuota(ticket.getQuota());
        return response;
    }

    @Getter
    @Setter
    public static class CreateTicketRequest {
        private String eventId;
        private String ticketType;
        private Double ticketPrice;
        private Integer quota;
    }

    @Getter
    @Setter
    public static class UpdateTicketRequest {
        private Double ticketPrice;
        private Integer quota;
    }

    @Getter
    @Setter
    public static class TicketResponse {
        private String id;
        private String eventId;
        private String ticketType;
        private Double ticketPrice;
        private Integer quota;
    }
}