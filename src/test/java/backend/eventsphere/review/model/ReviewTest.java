package backend.eventsphere.review.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ReviewTest {

    @Test
    public void testReviewCreation() {
        // Arrange
        Long id = 1L;
        Long eventId = 101L;
        Long userId = 201L;
        int rating = 4;
        String comment = "Great event!";
        
        // Act
        Review review = new Review(id, eventId, userId, rating, comment);
        
        // Assert
        assertEquals(id, review.getId());
        assertEquals(eventId, review.getEventId());
        assertEquals(userId, review.getUserId());
        assertEquals(rating, review.getRating());
        assertEquals(comment, review.getComment());
    }
    
    @Test
    public void testReviewBuilderPattern() {
        // Arrange & Act
        Review review = Review.builder()
                .id(1L)
                .eventId(101L)
                .userId(201L)
                .rating(5)
                .comment("Excellent organization")
                .build();
        
        // Assert
        assertEquals(1L, review.getId());
        assertEquals(101L, review.getEventId());
        assertEquals(201L, review.getUserId());
        assertEquals(5, review.getRating());
        assertEquals("Excellent organization", review.getComment());
    }
    
    @Test
    public void testReviewEquality() {
        // Arrange
        Review review1 = new Review(1L, 101L, 201L, 4, "Great event!");
        Review review2 = new Review(1L, 101L, 201L, 4, "Great event!");
        Review review3 = new Review(2L, 101L, 201L, 4, "Great event!");
        
        // Assert
        assertEquals(review1, review2);
        assertNotEquals(review1, review3);
    }
    
    @Test
    public void testReviewToString() {
        // Arrange
        Review review = new Review(1L, 101L, 201L, 4, "Great event!");
        
        // Act
        String toString = review.toString();
        
        // Assert
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("eventId=101"));
        assertTrue(toString.contains("userId=201"));
        assertTrue(toString.contains("rating=4"));
        assertTrue(toString.contains("comment=Great event!"));
    }
    
    @Test
    public void testRatingValidation() {
        // Arrange & Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            new Review(1L, 101L, 201L, 6, "Invalid rating");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            new Review(1L, 101L, 201L, 0, "Invalid rating");
        });
        
        assertDoesNotThrow(() -> {
            new Review(1L, 101L, 201L, 1, "Minimum valid rating");
            new Review(1L, 101L, 201L, 5, "Maximum valid rating");
        });
    }
}