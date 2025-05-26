package backend.eventsphere.review.repository;

import backend.eventsphere.review.model.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ReviewRepositoryTest {
    @Autowired
    private ReviewRepository reviewRepository;
    
    private UUID userId1;
    private UUID userId2;
    private UUID eventId1;
    private UUID eventId2;

    @BeforeEach
    void setUp() {
        userId1 = UUID.randomUUID();
        userId2 = UUID.randomUUID();
        eventId1 = UUID.randomUUID();
        eventId2 = UUID.randomUUID();
    }

    @Test
    void testSaveAndFindById() {
        Review review = new Review(null, eventId1, userId1, "Great event!", 5, 
            LocalDateTime.now(), LocalDateTime.now());
        Review savedReview = reviewRepository.save(review);

        Optional<Review> found = reviewRepository.findById(savedReview.getId());
        assertTrue(found.isPresent());
        assertEquals(savedReview, found.get());
    }

    @Test
    void testFindByEventId() {
        Review review1 = reviewRepository.save(new Review(null, eventId1, userId1, "A", 5, 
            LocalDateTime.now(), LocalDateTime.now()));
        Review review2 = reviewRepository.save(new Review(null, eventId1, userId2, "B", 4, 
            LocalDateTime.now(), LocalDateTime.now()));

        List<Review> results = reviewRepository.findByEventId(eventId1);
        assertEquals(2, results.size());
        assertTrue(results.contains(review1));
        assertTrue(results.contains(review2));
    }

    @Test
    void testFindByUserIdAndEventId() {
        Review saved = reviewRepository.save(new Review(null, eventId1, userId1, "Hi", 4, 
            LocalDateTime.now(), LocalDateTime.now()));
        Review result = reviewRepository.findByUserIdAndEventId(userId1, eventId1);
        
        assertNotNull(result);
        assertEquals(saved, result);
    }

    @Test
    void testFindByUserIdAndEventIdWithNoMatch() {
        Review result = reviewRepository.findByUserIdAndEventId(userId1, eventId1);
        assertNull(result);
    }
}