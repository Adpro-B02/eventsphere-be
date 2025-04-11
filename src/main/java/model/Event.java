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
    }

    public Event(UUID organizerId, String name, Long ticketPrice, LocalDateTime eventDateTime, String location, String description, String status) {
    }

    public void setStatus(String status) {
    }
}