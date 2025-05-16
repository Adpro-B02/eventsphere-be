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
        assertNotNull(reviewRepository.findById(savedReview.getId()));
    }

    @Test
    void testUpdateReview() {
        Review savedReview = reviewRepository.save(new Review(null, eventId1, userId1, "Nice", 3, null, null));

        Review updated = reviewRepository.update(savedReview.getId(), 4, "Improved");
        assertNotNull(updated);
        assertEquals(4, updated.getRating());
        assertEquals("Improved", updated.getComment());
        assertEquals(savedReview.getId(), updated.getId());
    
        Review retrieved = reviewRepository.findById(savedReview.getId());
        assertEquals(4, retrieved.getRating());
        assertEquals("Improved", retrieved.getComment());
    }

    @Test
    void testDeleteReview() {
        Review saved = reviewRepository.save(new Review(null, eventId1, userId1, "Good", 4, null, null));

        reviewRepository.delete(saved.getId());

        assertNull(reviewRepository.findById(saved.getId()));
        assertTrue(reviewRepository.findByEventId(eventId1).isEmpty());
        assertTrue(reviewRepository.findByUserId(userId1).isEmpty());
    }

    @Test
    void testFindByEventId() {
        reviewRepository.save(new Review(null, eventId1, userId1, "A", 5, null, null));
        reviewRepository.save(new Review(null, eventId1, userId2, "B", 4, null, null));
        reviewRepository.save(new Review(null, eventId2, userId1, "C", 3, null, null));

        List<Review> results = reviewRepository.findByEventId(eventId1);
        assertEquals(2, results.size());
    }

    @Test
    void testFindByUserId() {
        reviewRepository.save(new Review(null, eventId1, userId1, "A", 5, null, null));
        reviewRepository.save(new Review(null, eventId2, userId1, "B", 4, null, null));
        reviewRepository.save(new Review(null, eventId1, userId2, "C", 3, null, null));

        List<Review> results = reviewRepository.findByUserId(userId1);
        assertEquals(2, results.size());
    }

    @Test
    void testFindByUserIdAndEventId() {
        reviewRepository.save(new Review(null, eventId1, userId1, "Hi", 4, null, null));
        Review result = reviewRepository.findByUserIdAndEventId(userId1, eventId1);
        assertNotNull(result);
        assertEquals(4, result.getRating());
    }

    @Test
    void testFindByUserIdAndEventIdWithNoMatch() {
        Review result = reviewRepository.findByUserIdAndEventId(userId1, eventId1);
        assertNull(result);
    }

    @Test
    void testCalculateAverageRatingByEventId() {
        reviewRepository.save(new Review(null, eventId1, userId1, "Nice", 4, null, null));
        reviewRepository.save(new Review(null, eventId1, userId2, "Better", 2, null, null));

        double avg = reviewRepository.calculateAverageRatingByEventId(eventId1);
        assertEquals(3.0, avg);
    }
    
    @Test
    void testCalculateAverageRatingByEventIdWithNoReviews() {
        double avg = reviewRepository.calculateAverageRatingByEventId(eventId1);
        assertEquals(0.0, avg);
    }
    
    @Test
    void testFindAll() {
        reviewRepository.save(new Review(null, eventId1, userId1, "A", 5, null, null));
        reviewRepository.save(new Review(null, eventId2, userId2, "B", 4, null, null));
        
        List<Review> results = reviewRepository.findAll();
        assertEquals(2, results.size());
    }
}