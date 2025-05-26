package backend.eventsphere.ticket.model;

import enums.TicketEventType;

public interface TicketObserver {
    void onTicketEvent(Ticket ticket, TicketEventType eventType);
}
