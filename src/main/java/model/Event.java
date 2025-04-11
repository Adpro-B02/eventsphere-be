package model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

@Getter
public class Event {

    private final UUID id;
    private final UUID organizerId;
    private final String name;
    private final Long ticketPrice;
    private final LocalDateTime eventDateTime;
    private final String location;
    private final String description;

    @Setter
    private String status;

    public Event(UUID organizerId, String name, Long ticketPrice, LocalDateTime eventDateTime, String location, String description) {
        this.id = UUID.randomUUID();
        this.organizerId = organizerId;
        this.name = name;
        this.ticketPrice = ticketPrice;
        this.eventDateTime = eventDateTime;
        this.location = location;
        this.description = description;
        this.status = "PLANNED"; // default status
    }

    public Event(UUID organizerId, String name, Long ticketPrice, LocalDateTime eventDateTime, String location, String description, String status) {
        this(organizerId, name, ticketPrice, eventDateTime, location, description);
        setStatus(status); // validasi dilakukan di setter
    }

    public void setStatus(String status) {
        String[] allowedStatus = {"PLANNED", "CANCELLED", "COMPLETED", "POSTPONED"};
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