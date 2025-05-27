package backend.eventsphere.review.model;

import java.util.UUID;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class ReviewTest {
    @Test
    void createValidReview() {
        Review review = new Review(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
            "Valid comment", 4, LocalDateTime.now(), LocalDateTime.now());
        
        assertNotNull(review);
        assertEquals("Valid comment", review.getComment());
        assertEquals(4, review.getRating());
    }
}