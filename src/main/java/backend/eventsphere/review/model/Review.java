package backend.eventsphere.review.model;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Review {
    private final UUID id;
    private final UUID eventId;
    private final UUID userId;
    private String comment;
    private int rating;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Review(UUID id, UUID eventId, UUID userId, String comment, int rating,
                LocalDateTime createdAt, LocalDateTime updatedAt) {
        // this.id = id != null ? id : UUID.randomUUID();
        this.id = id;
        validateEventId(eventId);
        validateUserId(userId);
        validateComment(comment);
        validateRating(rating);

        this.eventId = eventId;
        this.userId = userId;
        this.comment = comment;
        this.rating = rating;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
    }

    private void validateEventId(UUID eventId) {
        if (eventId == null) {
            throw new IllegalArgumentException("Event ID cannot be null");
        }
    }

    private void validateUserId(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
    }

    private void validateComment(String comment) {
        if (comment != null && comment.length() > 1000) {
            throw new IllegalArgumentException("Comment cannot exceed 1000 characters");
        }
    }

    private void validateRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
    }

    public void setComment(String comment) {
        validateComment(comment);
        this.comment = comment;
        this.updatedAt = LocalDateTime.now();
    }

    public void setRating(int rating) {
        validateRating(rating);
        this.rating = rating;
        this.updatedAt = LocalDateTime.now();
    }
}
