package backend.eventsphere.event.event_driven.listener;

import backend.eventsphere.event.event_driven.EventCreatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventListener {

    @EventListener
    public void handleEventCreated(EventCreatedEvent event) {
        System.out.println("Notifikasi: Event dibuat - " + event.getEvent().getName());
    }
}