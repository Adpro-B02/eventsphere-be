package backend.eventsphere.event.client;

import backend.eventsphere.event.model.Event;

import java.util.List;
import java.util.UUID;

public interface EventClient {
    List<Event> getAllEvents();
    Event getEventById(UUID id);
}