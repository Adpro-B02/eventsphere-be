package backend.eventsphere.service.strategy;

import backend.eventsphere.model.Event;

public class DeletionValidation implements ValidationStrategy {

    @Override
    public void validate(Event event) {
        if (!event.getStatus().equals("CANCELLED")) {
            throw new IllegalStateException("Only CANCELLED events can be deleted");
        }
    }
}
