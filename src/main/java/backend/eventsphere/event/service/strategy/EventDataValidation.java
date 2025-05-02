package backend.eventsphere.event.service.strategy;

import backend.eventsphere.event.model.Event;

public class EventDataValidation implements ValidationStrategy {

    @Override
    public void validate(Event event) {
        if (event.getName() == null || event.getName().isBlank()) {
            throw new IllegalArgumentException("Event name must not be empty");
        }
        if (event.getTicketPrice() == null || event.getTicketPrice() <= 0) {
            throw new IllegalArgumentException("Ticket price must be greater than 0");
        }
        if (event.getEventDateTime() == null || event.getEventDateTime().isBefore(java.time.LocalDateTime.now())) {
            throw new IllegalArgumentException("Event date must be in the future");
        }
    }
}
