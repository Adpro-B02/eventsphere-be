package backend.eventsphere.event.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

@Getter
public class Event {

    private final UUID id;
    private final UUID organizerId;

    @Setter
    private String name;

    @Setter
    private Long ticketPrice;

    @Setter
    private LocalDateTime eventDateTime;

    @Setter
    private String location;

    @Setter
    private String description;

    @Setter
    private String link_image;

    private String status;

    public Event(UUID organizerId, String name, Long ticketPrice, LocalDateTime eventDateTime, String location, String description, String link_image) {
        this.id = UUID.randomUUID();
        this.organizerId = organizerId;
        this.name = name;
        this.ticketPrice = ticketPrice;
        this.eventDateTime = eventDateTime;
        this.location = location;
        this.description = description;
        this.link_image = link_image;
        this.status = "PLANNED"; // default status
    }

    public Event(UUID organizerId, String name, Long ticketPrice, LocalDateTime eventDateTime, String location, String description, String status, String link_image) {
        this(organizerId, name, ticketPrice, eventDateTime, location, description, link_image);
        setStatus(status); // validasi dilakukan di setter
    }

    public void setStatus(String status) {
        String[] allowedStatus = {"PLANNED", "CANCELLED", "COMPLETED"};
        if (Arrays.stream(allowedStatus).noneMatch(s -> s.equals(status))) {
            throw new IllegalArgumentException("Invalid event status: " + status);
        }
        this.status = status;
    }

    @Override
    public String toString() {
        return this.name;
    }
}