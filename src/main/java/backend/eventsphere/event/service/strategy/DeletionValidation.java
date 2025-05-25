package backend.eventsphere.event.service.strategy;

import backend.eventsphere.event.model.Event;
import org.springframework.stereotype.Component;

@Component
public class DeletionValidation implements ValidationStrategy {

    @Override
    public void validate(Event event) {
        if (!(event.getStatus().equals("CANCELLED") || event.getStatus().equals("COMPLETED"))) {
            throw new IllegalStateException("Event hanya bisa dihapus jika statusnya CANCELLED atau COMPLETED.");
        }
    }
}