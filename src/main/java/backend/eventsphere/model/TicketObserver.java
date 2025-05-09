package backend.eventsphere.model;

import backend.eventsphere.model.Ticket;
import enums.TicketEventType;

public interface TicketObserver {
    void onTicketEvent(Ticket ticket, TicketEventType eventType);
}
