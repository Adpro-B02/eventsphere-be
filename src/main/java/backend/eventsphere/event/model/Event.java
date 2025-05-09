package backend.eventsphere.event.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private UUID organizerId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long ticketPrice;

    @Column(nullable = false)
    private LocalDateTime eventDateTime;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String link_image;

    @Column(nullable = false)
    private String status;

    public Event(UUID organizerId, String name, Long ticketPrice, LocalDateTime eventDateTime,
                 String location, String description, String link_image) {
        this.organizerId = organizerId;
        this.name = name;
        this.ticketPrice = ticketPrice;
        this.eventDateTime = eventDateTime;
        this.location = location;
        this.description = description;
        this.link_image = link_image;
        this.status = "PLANNED";
    }

    public Event(UUID organizerId, String name, Long ticketPrice, LocalDateTime eventDateTime,
                 String location, String description, String status, String link_image) {
        this(organizerId, name, ticketPrice, eventDateTime, location, description, link_image);
        setStatus(status);
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
