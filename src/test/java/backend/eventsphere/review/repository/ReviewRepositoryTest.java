package backend.eventsphere.review.repository;

import backend.eventsphere.review.model.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ReviewRepositoryTest {

    private ReviewRepository reviewRepository;
    private UUID userId1;
    private UUID userId2;
    private UUID eventId1;
    private UUID eventId2;

    @BeforeEach
    void setUp() {
        reviewRepository = new ReviewRepository();
        userId1 = UUID.randomUUID();
        userId2 = UUID.randomUUID();
        eventId1 = UUID.randomUUID();
        eventId2 = UUID.randomUUID();
    }

    @Test
    void testSaveNewReview() {
        Review review = new Review(eventId1, userId1, "Great event!", 5);
        Review savedReview = reviewRepository.save(review);
        
        assertNotNull(savedReview.getId());
        assertEquals(eventId1, savedReview.getEventId());
        assertEquals(userId1, savedReview.getUserId());
    }

    @Test
    void testSaveExistingReviewThrowsException() {
        Review review = new Review(eventId1, userId1, "First review", 4);
        reviewRepository.save(review);
        
        Review duplicateReview = new Review(eventId1, userId1, "Duplicate review", 5);
        
        assertThrows(IllegalStateException.class, () -> reviewRepository.save(duplicateReview));
    }

    @Test
    void testUpdateReview() {
        Review savedReview = reviewRepository.save(new Review(eventId1, userId1, "Initial review", 3));
        
        Review updatedReview = reviewRepository.update(savedReview.getId(), 5, "Updated review");
        
        assertEquals("Updated review", updatedReview.getComment());
        assertEquals(5, updatedReview.getRating());
        assertEquals(savedReview.getId(), updatedReview.getId());
    }
    
    @Test
    void testUpdateNonExistentReview() {
        UUID nonExistentId = UUID.randomUUID();
        
        assertThrows(IllegalArgumentException.class, () -> 
            reviewRepository.update(nonExistentId, 4, "Updated review"));
    }
    
    @Test
    void testDeleteReview() {
        Review savedReview = reviewRepository.save(new Review(eventId1, userId1, "Review to delete", 4));
        
        reviewRepository.delete(savedReview.getId());
        
        assertNull(reviewRepository.findById(savedReview.getId()));
    }
    
    @Test
    void testFindByEventId() {
        reviewRepository.save(new Review(eventId1, userId1, "Event 1 Review 1", 5));
        reviewRepository.save(new Review(eventId1, userId2, "Event 1 Review 2", 4));
        reviewRepository.save(new Review(eventId2, userId1, "Event 2 Review", 3));
        
        List<Review> eventReviews = reviewRepository.findByEventId(eventId1);
        
        assertEquals(2, eventReviews.size());
    }
    
    @Test
    void testFindByUserId() {
        reviewRepository.save(new Review(eventId1, userId1, "User 1 Review 1", 5));
        reviewRepository.save(new Review(eventId2, userId1, "User 1 Review 2", 4));
        reviewRepository.save(new Review(eventId1, userId2, "User 2 Review", 3));
        
        List<Review> userReviews = reviewRepository.findByUserId(userId1);
        
        assertEquals(2, userReviews.size());
    }
    
    @Test
    void testCalculateAverageRatingByEventId() {
        reviewRepository.save(new Review(eventId1, userId1, "Rating 5", 5));
        reviewRepository.save(new Review(eventId1, userId2, "Rating 3", 3));
        
        double averageRating = reviewRepository.calculateAverageRatingByEventId(eventId1);
        
        assertEquals(4.0, averageRating);
    }
    
    @Test
    void testCountByEventId() {
        reviewRepository.save(new Review(eventId1, userId1, "Event 1 Review 1", 5));
        reviewRepository.save(new Review(eventId1, userId2, "Event 1 Review 2", 4));
        
        long count = reviewRepository.countByEventId(eventId1);
        
        assertEquals(2, count);
    }
}