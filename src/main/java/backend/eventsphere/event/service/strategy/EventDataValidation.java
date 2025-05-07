package backend.eventsphere.event.service.strategy;

import backend.eventsphere.event.model.Event;
import org.springframework.stereotype.Component;

@Component
public class EventDataValidation implements ValidationStrategy {

    @Override
    public void validate(Event event) {
        if (event.getName() == null || event.getName().isBlank()) {
            throw new IllegalArgumentException("Nama event tidak boleh kosong.");
        }
        if (event.getTicketPrice() == null || event.getTicketPrice() < 0) {
            throw new IllegalArgumentException("Harga tiket harus positif.");
        }
        if (event.getEventDateTime() == null) {
            throw new IllegalArgumentException("Tanggal dan waktu tidak boleh kosong.");
        }
        if (event.getLocation() == null || event.getLocation().isBlank()) {
            throw new IllegalArgumentException("Lokasi tidak boleh kosong.");
        }
    }
}