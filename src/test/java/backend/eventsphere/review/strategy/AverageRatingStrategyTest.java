package backend.eventsphere.review.strategy;

import backend.eventsphere.review.model.Review;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class AverageRatingStrategyTest {
    private final AverageRatingStrategy strategy = new AverageRatingStrategy();

    @Test
    void calculateRating_WithValidReviews() {
        Review review1 = new Review(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 
            "Good", 4, LocalDateTime.now(), LocalDateTime.now());
        Review review2 = new Review(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 
            "Great", 5, LocalDateTime.now(), LocalDateTime.now());
        
        double result = strategy.calculateRating(Arrays.asList(review1, review2));
        
        assertEquals(4.5, result);
    }

    @Test
    void calculateRating_WithEmptyList() {
        double result = strategy.calculateRating(Collections.emptyList());
        assertEquals(0.0, result);
    }
}