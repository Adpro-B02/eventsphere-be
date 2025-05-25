package backend.eventsphere.ticket.service;

import backend.eventsphere.ticket.model.Ticket;
import backend.eventsphere.ticket.model.TicketFactory;
import backend.eventsphere.ticket.repository.TicketRepository;
import backend.eventsphere.ticket.model.TicketObserver;
import enums.TicketEventType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;
    private final TicketFactory ticketFactory;
    private final List<TicketObserver> observers = new ArrayList<>();

    public TicketService(TicketRepository ticketRepository, TicketFactory ticketFactory) {
        this.ticketRepository = ticketRepository;
        this.ticketFactory = ticketFactory;
    }

    public void registerObserver(TicketObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(TicketObserver observer) {
        observers.remove(observer);
    }

    private void notifyTicketEvent(Ticket ticket, TicketEventType eventType) {
        for (TicketObserver observer : observers) {
            observer.onTicketEvent(ticket, eventType);
        }
    }

    @Transactional
    public Ticket createTicket(UUID eventId, String ticketType, Double price, Integer quota) {
        Ticket ticket = ticketFactory.createTicket(eventId, ticketType, price, quota);
        ticketRepository.save(ticket);
        notifyTicketEvent(ticket, TicketEventType.CREATED);
        return ticket;
    }

    public Map<UUID, Ticket> getTicketsByEvent(UUID eventId) {
        return ticketRepository.findByEventId(eventId).stream()
            .collect(Collectors.toMap(Ticket::getId, ticket -> ticket));
    }

    @Transactional
    public Ticket updateTicket(UUID ticketId, Double newPrice, Integer newQuota) {
        Optional<Ticket> optionalTicket = ticketRepository.findById(ticketId);
        if (optionalTicket.isEmpty()) {
            throw new IllegalArgumentException("Ticket not found with ID: " + ticketId);
        }

        Ticket ticket = optionalTicket.get();
        if (newPrice != null) {
            if (newPrice < 0) {
                throw new IllegalArgumentException("Price cannot be negative");
            }
            ticket.setPrice(newPrice);
        }
        if (newQuota != null) {
            if (newQuota < 0) {
                throw new IllegalArgumentException("Quota cannot be negative");
            }
            ticket.setQuota(newQuota);
        }

        ticket = ticketRepository.save(ticket);
        notifyTicketEvent(ticket, TicketEventType.UPDATED);
        return ticket;
    }

    @Transactional
    public boolean deleteTicket(UUID ticketId) {
        Optional<Ticket> optionalTicket = ticketRepository.findById(ticketId);
        if (optionalTicket.isEmpty()) {
            return false;
        }

        Ticket ticket = optionalTicket.get();
        ticketRepository.delete(ticket);
        notifyTicketEvent(ticket, TicketEventType.DELETED);
        return true;
    }

    @Async
    @Transactional
    public CompletableFuture<Ticket> createTicketAsync(UUID eventId, String ticketType, Double price, Integer quota) {
        try {
            return CompletableFuture.completedFuture(createTicket(eventId, ticketType, price, quota));
        } catch (Exception e) {
            CompletableFuture<Ticket> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    @Async
    public CompletableFuture<Map<UUID, Ticket>> getTicketsByEventAsync(UUID eventId) {
        try {
            return CompletableFuture.completedFuture(getTicketsByEvent(eventId));
        } catch (Exception e) {
            CompletableFuture<Map<UUID, Ticket>> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    @Async
    @Transactional
    public CompletableFuture<Ticket> updateTicketAsync(UUID ticketId, Double newPrice, Integer newQuota) {
        try {
            return CompletableFuture.completedFuture(updateTicket(ticketId, newPrice, newQuota));
        } catch (Exception e) {
            CompletableFuture<Ticket> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    @Async
    @Transactional
    public CompletableFuture<Boolean> deleteTicketAsync(UUID ticketId) {
        try {
            return CompletableFuture.completedFuture(deleteTicket(ticketId));
        } catch (Exception e) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }
}