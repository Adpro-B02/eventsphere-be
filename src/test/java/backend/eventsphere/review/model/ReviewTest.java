package backend.eventsphere.review.model;

import java.util.UUID;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class ReviewTest {

    private UUID reviewId;
    private UUID eventId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        reviewId = UUID.randomUUID();
        eventId = UUID.randomUUID();
        userId = UUID.randomUUID();
    }

    @Test
    void createValidReview() {
        LocalDateTime now = LocalDateTime.now();
        
        Review review = new Review(reviewId, eventId, userId, "Excellent event!", 5, now, now);

        assertEquals(reviewId, review.getId());
        assertEquals(eventId, review.getEventId());
        assertEquals(userId, review.getUserId());
        assertEquals("Excellent event!", review.getComment());
        assertEquals(5, review.getRating());
        assertEquals(now, review.getCreatedAt());
        assertEquals(now, review.getUpdatedAt());
    }

    @Test
    void createInvalidReview() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Review(null, null, null, null, 0, null, null);
        });
    }

    @Test
    void validateCommentTooLong() {
        StringBuilder longComment = new StringBuilder();
        for (int i = 0; i < 101; i++) {
            longComment.append("0123456789");
        }

        assertThrows(IllegalArgumentException.class, () -> {
            new Review(reviewId, eventId, userId, longComment.toString(), 5, null, null);
        });
    }

    @Test
    void validateRatingOutOfRange() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Review(reviewId, eventId, userId, "Excellent event!", 6, null, null);
        });
    }
    
    @Test
    void setValidReview() {
        LocalDateTime now = LocalDateTime.now();
        Review review = new Review(reviewId, eventId, userId, "Excellent event!", 5, now, now);
        review.setComment("Valid Update");
        review.setRating(4);

        assertEquals("Valid Update", review.getComment());
        assertEquals(4, review.getRating());
    }

    @Test
    void setInvalidReview() {
        LocalDateTime now = LocalDateTime.now();
        Review review = new Review(reviewId, eventId, userId, "Excellent event!", 5, now, now);

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