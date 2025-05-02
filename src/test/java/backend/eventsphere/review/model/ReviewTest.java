package backend.eventsphere.review.model;

import java.util.UUID;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.*;

public class ReviewTest {

    private final UUID customEventUUID = UUID.fromString("7e4a8c42-0b1f-4c3a-a85f-cb61b0b8e9d2");
    private final UUID customUserUUID = UUID.fromString("6d823b96-7e9e-4217-89a5-029a2f67b4ad");

    @Test
    void createValidReview() {
        LocalDateTime now = LocalDateTime.now();
        
        Review review = new Review(customEventUUID, customUserUUID, "Excellent event!", 5, now, now);

        assertNotNull(review.getId());
        assertEquals(customEventUUID, review.getEventId());
        assertEquals(customUserUUID, review.getUserId());
        assertEquals("Excellent event!", review.getComment());
        assertEquals(5, review.getRating());
        assertEquals(now, review.getCreatedAt());
        assertEquals(now, review.getUpdatedAt());
    }

    @Test
    void createInvalidReview() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Review(null, null, null, 0);
        });
    }

    @Test
    void validateCommentTooLong() {
        StringBuilder longComment = new StringBuilder();
        for (int i = 0; i < 101; i++) {
            longComment.append("0123456789");
        }

        assertThrows(IllegalArgumentException.class, () -> {
            new Review(customEventUUID, customUserUUID, longComment.toString(), 5);
        });
    }

    @Test
    void validateRatingOutOfRange() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Review(customEventUUID, customUserUUID, "Excellent event!", 6);
        });
    }
    
    @Test
    void setValidReview() {
        LocalDateTime previous = LocalDateTime.now();
        Review review = new Review(customEventUUID, customUserUUID, "Excellent event!", 5, previous, previous);
        review.setEventId(customUserUUID);
        review.setUserId(customEventUUID);
        review.setComment("Valid Update");
        review.setRating(4);
                
        LocalDateTime after = LocalDateTime.now();
        review.setUpdatedAt(after);

        assertEquals(customUserUUID, review.getEventId());
        assertEquals(customEventUUID, review.getUserId());
        assertEquals("Valid Update", review.getComment());
        assertEquals(4, review.getRating());
        assertEquals(previous, review.getCreatedAt());
        assertEquals(after, review.getUpdatedAt());
    }

    @Test
    void setInvalidReview() {
        LocalDateTime previous = LocalDateTime.now();
        Review review = new Review(customEventUUID, customUserUUID, "Excellent event!", 5, previous, previous);
        
        assertThrows(IllegalArgumentException.class, () -> {
            review.setEventId(null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            review.setUserId(null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            StringBuilder longComment = new StringBuilder();
            for (int i = 0; i < 101; i++) {
                longComment.append("0123456789");
            }
            
            review.setComment(longComment.toString());
        });

        assertThrows(IllegalArgumentException.class, () -> {
            review.setRating(6);
        });
    }
}