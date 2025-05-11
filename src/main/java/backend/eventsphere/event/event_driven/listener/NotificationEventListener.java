package backend.eventsphere.event.event_driven.listener;

import backend.eventsphere.event.event_driven.EventCreatedEvent;
import backend.eventsphere.event.event_driven.EventDeletedEvent;
import backend.eventsphere.event.event_driven.EventUpdatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventListener {

    @EventListener
    public void handleEventCreated(EventCreatedEvent event) {
        System.out.println("Notifikasi: Event dibuat - " + event.getEvent().getName());
    }

    @EventListener
    public void handleEventUpdated(EventUpdatedEvent event) {
        System.out.println("Notifikasi: Event diupdate - " + event.getEvent().getName());
    }

    @EventListener
    public void handleEventDeleted(EventDeletedEvent event) {
        System.out.println("Notifikasi: Event dihapus - " + event.getEventId());
    }
}