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
        Review review = new Review(null, eventId1, userId1, "Great event!", 5, null, null);
        Review savedReview = reviewRepository.save(review);
        
        assertNotNull(savedReview.getId());
        assertEquals(eventId1, savedReview.getEventId());
        assertEquals(userId1, savedReview.getUserId());
    }

    @Test
    void testSaveExistingReviewThrowsException() {
        Review review = new Review(null, eventId1, userId1, "Great event!", 5, null, null);
        reviewRepository.save(review);
        
        Review duplicateReview = new Review(null, eventId1, userId1, "Great event!", 5, null, null);
        
        assertThrows(IllegalStateException.class, () -> reviewRepository.save(duplicateReview));
    }

    @Test
    void testUpdateReview() {
        Review savedReview = reviewRepository.save(new Review(null, eventId1, userId1, "Great event!", 5, null, null));
        
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
        Review savedReview = reviewRepository.save(new Review(null, eventId1, userId1, "Great event!", 5, null, null));
        
        reviewRepository.delete(savedReview.getId());
        
        assertNull(reviewRepository.findById(savedReview.getId()));
    }
    
    @Test
    void testFindByEventId() {
        reviewRepository.save(new Review(null, eventId1, userId1, "Great event!", 5, null, null));
        reviewRepository.save(new Review(null, eventId1, userId2, "Great event!", 5, null, null));
        reviewRepository.save(new Review(null, eventId2, userId1, "Great event!", 5, null, null));
        
        List<Review> eventReviews = reviewRepository.findByEventId(eventId1);
        
        assertEquals(2, eventReviews.size());
    }
    
    @Test
    void testFindByUserId() {
        reviewRepository.save(new Review(null, eventId1, userId1, "Great event!", 5, null, null));
        reviewRepository.save(new Review(null, eventId1, userId2, "Great event!", 4, null, null));
        reviewRepository.save(new Review(null, eventId2, userId1, "Great event!", 3, null, null));
        
        List<Review> userReviews = reviewRepository.findByUserId(userId1);
        
        assertEquals(2, userReviews.size());
    }
    
    @Test
    void testCalculateAverageRatingByEventId() {
        reviewRepository.save(new Review(null, eventId1, userId1, "Great event!", 5, null, null));
        reviewRepository.save(new Review(null, eventId1, userId2, "Great event!", 4, null, null));
        
        double averageRating = reviewRepository.calculateAverageRatingByEventId(eventId1);
        
        assertEquals(4.5, averageRating);
    }
}