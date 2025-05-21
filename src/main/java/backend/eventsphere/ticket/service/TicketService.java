package backend.eventsphere.ticket.service;

import backend.eventsphere.ticket.model.Ticket;
import backend.eventsphere.ticket.model.TicketFactory;
import backend.eventsphere.ticket.repository.TicketRepository;
import backend.eventsphere.ticket.model.TicketObserver;
import enums.TicketEventType;
import org.springframework.stereotype.Service;

import java.util.*;

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

    public Ticket createTicket(UUID eventId, String ticketType, Double price, Integer quota) {
        Ticket ticket = ticketFactory.createTicket(eventId, ticketType, price, quota);
        ticketRepository.save(ticket);
        notifyTicketEvent(ticket, TicketEventType.CREATED);
        return ticket;
    }

    public Map<UUID, Ticket> getTicketsByEvent(UUID eventId) {
        return ticketRepository.listByEvent(eventId);
    }

    public Ticket updateTicket(UUID ticketId, Double newPrice, Integer newQuota) {
        Ticket ticket = ticketRepository.findById(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("Ticket not found with ID: " + ticketId);
        }
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
        ticketRepository.update(ticket);
        notifyTicketEvent(ticket, TicketEventType.UPDATED);
        return ticket;
    }

    public boolean deleteTicket(UUID ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId);
        if (ticket == null) {
            return false;
        }
        boolean deleted = ticketRepository.delete(ticketId);
        if (deleted) {
            notifyTicketEvent(ticket, TicketEventType.DELETED);
        }
        return deleted;
    }
}