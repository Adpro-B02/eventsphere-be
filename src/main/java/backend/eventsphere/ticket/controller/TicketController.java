package backend.eventsphere.ticket.controller;

import backend.eventsphere.ticket.model.Ticket;
import backend.eventsphere.ticket.service.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;

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
    @PreAuthorize("hasAuthority('ORGANIZER')")
    public CompletableFuture<ResponseEntity<?>> createTicket(@RequestBody CreateTicketRequest request) {
        return ticketService.createTicketAsync(
            UUID.fromString(request.getEventId()),
            request.getTicketType(),
            request.getTicketPrice(),
            request.getQuota()
        ).handle((ticket, ex) -> {
            if (ex != null) {
                Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cause.getMessage());
            } else {
                return ResponseEntity.ok(mapToResponse(ticket));
            }
        });
    }

    @GetMapping("/{id_event}")
    @PreAuthorize("hasAnyAuthority('ATTENDEE', 'ORGANIZER', 'ADMIN')")
    public CompletableFuture<ResponseEntity<?>> getTicketsByEvent(@PathVariable("id_event") String eventId) {
        return ticketService.getTicketsByEventAsync(UUID.fromString(eventId))
            .handle((tickets, ex) -> {
                if (ex != null) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cause.getMessage());
                }
                if (tickets.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No tickets found for event");
                }
                return ResponseEntity.ok(tickets.values().stream()
                    .map(this::mapToResponse)
                    .toList());
            });
    }

    @PutMapping("/{id_ticket}")
    @PreAuthorize("hasAuthority('ORGANIZER')")
    public CompletableFuture<ResponseEntity<?>> updateTicket(
            @PathVariable("id_ticket") String ticketId,
            @RequestBody UpdateTicketRequest request) {
        return ticketService.updateTicketAsync(
            UUID.fromString(ticketId),
            request.getTicketPrice(),
            request.getQuota()
        ).handle((ticket, ex) -> {
            if (ex != null) {
                Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                if (cause instanceof IllegalArgumentException) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cause.getMessage());
                }
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating ticket: " + cause.getMessage());
            }
            return ResponseEntity.ok(mapToResponse(ticket));
        });
    }

    @DeleteMapping("/{id_ticket}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public CompletableFuture<ResponseEntity<?>> deleteTicket(@PathVariable("id_ticket") String ticketId) {
        return ticketService.deleteTicketAsync(UUID.fromString(ticketId))
            .handle((deleted, ex) -> {
                if (ex != null) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error deleting ticket: " + cause.getMessage());
                }
                if (Boolean.TRUE.equals(deleted)) {
                    return ResponseEntity.noContent().build();
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ticket not found");
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