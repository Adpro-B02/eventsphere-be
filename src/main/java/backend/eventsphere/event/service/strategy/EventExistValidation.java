package backend.eventsphere.event.service.strategy;

import backend.eventsphere.event.client.EventClient;
import backend.eventsphere.event.model.Event;
import org.springframework.stereotype.Component;

@Component
public class EventExistValidation implements ValidationStrategy {

    private final EventClient eventClient;

    public EventExistValidation(EventClient eventClient) {
        this.eventClient = eventClient;
    }

    @Override
    public void validate(Event event) {
        boolean exists = eventClient.getAllEvents().stream().anyMatch(
                e -> e.getName().equalsIgnoreCase(event.getName()) &&
                        e.getEventDateTime().equals(event.getEventDateTime()) && e.getId() != event.getId()
        );
        if (exists) {
            throw new IllegalArgumentException("Event dengan nama dan tanggal tersebut sudah ada.");
        }
    }
}
