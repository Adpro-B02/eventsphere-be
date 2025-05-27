package backend.eventsphere.ticket.model;

import backend.eventsphere.ticket.enums.TicketEventType;

public interface TicketObserver {
    void onTicketEvent(Ticket ticket, TicketEventType eventType);
}
