package backend.eventsphere.event.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;

@Entity
@Table(name = "events")
@Getter
@Setter
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private UUID organizerId;

    @Column(nullable = false)
    private String name;

    @NotNull(message = "Harga tiket tidak boleh kosong.")
    @Min(value = 0, message = "Harga tiket harus positif.")
    @Column(nullable = false)
    private Long ticketPrice;

    @Setter
    @Getter
    @NotNull(message = "Tanggal dan waktu event tidak boleh kosong.")
    @Future(message = "Tanggal dan waktu event harus di masa depan.")
    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime eventDateTime;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String link_image;

    @Column(nullable = false)
    private String status;

    public Event() {
        this.status = "PLANNED";
    }

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