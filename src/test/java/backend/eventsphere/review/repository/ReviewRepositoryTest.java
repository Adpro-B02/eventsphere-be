package backend.eventsphere.review.repository;

import backend.eventsphere.review.model.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ReviewRepositoryTest {

    private ReviewRepository reviewRepository;
    private Review review1;
    private Review review2;
    private Review review3;

    @BeforeEach
    void setUp() {
        reviewRepository = new ReviewRepository();
        
        // Create test reviews
        review1 = new Review(null, 1L, 1L, 5, "Excellent event!");
        review2 = new Review(null, 1L, 2L, 4, "Good event!");
        review3 = new Review(null, 2L, 1L, 3, "Average event.");
        
        // Save test reviews
        review1 = reviewRepository.save(review1);
        review2 = reviewRepository.save(review2);
        review3 = reviewRepository.save(review3);
    }

    @Test
    void findById_ExistingId_ReturnsReview() {
        // Act
        Optional<Review> result = reviewRepository.findById(review1.getId());
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals(review1.getId(), result.get().getId());
        assertEquals(review1.getEventId(), result.get().getEventId());
        assertEquals(review1.getUserId(), result.get().getUserId());
        assertEquals(review1.getRating(), result.get().getRating());
        assertEquals(review1.getComment(), result.get().getComment());
    }

    @Test
    void findById_NonExistingId_ReturnsEmpty() {
        // Act
        Optional<Review> result = reviewRepository.findById(999L);
        
        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void save_NewReview_AssignsId() {
        // Arrange
        Review newReview = new Review(null, 3L, 3L, 5, "Amazing!");
        
        // Act
        Review savedReview = reviewRepository.save(newReview);
        
        // Assert
        assertNotNull(savedReview.getId());
        assertEquals(newReview.getEventId(), savedReview.getEventId());
        assertEquals(newReview.getUserId(), savedReview.getUserId());
        assertEquals(newReview.getRating(), savedReview.getRating());
        assertEquals(newReview.getComment(), savedReview.getComment());
    }

    @Test
    void save_ExistingReview_UpdatesReview() {
        // Arrange
        Review updatedReview = new Review(review1.getId(), review1.getEventId(), review1.getUserId(), 4, "Updated comment");
        
        // Act
        Review savedReview = reviewRepository.save(updatedReview);
        Optional<Review> retrievedReview = reviewRepository.findById(review1.getId());
        
        // Assert
        assertEquals(review1.getId(), savedReview.getId());
        assertEquals(updatedReview.getRating(), savedReview.getRating());
        assertEquals(updatedReview.getComment(), savedReview.getComment());
        
        assertTrue(retrievedReview.isPresent());
        assertEquals(updatedReview.getRating(), retrievedReview.get().getRating());
        assertEquals(updatedReview.getComment(), retrievedReview.get().getComment());
    }

    @Test
    void deleteById_ExistingId_RemovesReview() {
        // Act
        reviewRepository.deleteById(review1.getId());
        Optional<Review> result = reviewRepository.findById(review1.getId());
        
        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void deleteById_NonExistingId_DoesNothing() {
        // Arrange
        int initialCount = countReviews();
        
        // Act
        reviewRepository.deleteById(999L);
        int finalCount = countReviews();
        
        // Assert
        assertEquals(initialCount, finalCount);
    }

    @Test
    void findByEventId_ExistingEventId_ReturnsReviews() {
        // Act
        List<Review> results = reviewRepository.findByEventId(1L);
        
        // Assert
        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(r -> r.getId().equals(review1.getId())));
        assertTrue(results.stream().anyMatch(r -> r.getId().equals(review2.getId())));
    }

    @Test
    void findByEventId_NonExistingEventId_ReturnsEmptyList() {
        // Act
        List<Review> results = reviewRepository.findByEventId(999L);
        
        // Assert
        assertTrue(results.isEmpty());
    }

    @Test
    void findByUserId_ExistingUserId_ReturnsReviews() {
        // Act
        List<Review> results = reviewRepository.findByUserId(1L);
        
        // Assert
        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(r -> r.getId().equals(review1.getId())));
        assertTrue(results.stream().anyMatch(r -> r.getId().equals(review3.getId())));
    }

    @Test
    void findByUserId_NonExistingUserId_ReturnsEmptyList() {
        // Act
        List<Review> results = reviewRepository.findByUserId(999L);
        
        // Assert
        assertTrue(results.isEmpty());
    }

    @Test
    void findByUserIdAndEventId_ExistingCombination_ReturnsReview() {
        // Act
        Optional<Review> result = reviewRepository.findByUserIdAndEventId(1L, 1L);
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals(review1.getId(), result.get().getId());
    }

    @Test
    void findByUserIdAndEventId_NonExistingCombination_ReturnsEmpty() {
        // Act
        Optional<Review> result = reviewRepository.findByUserIdAndEventId(999L, 999L);
        
        // Assert
        assertTrue(result.isEmpty());
    }

    /**
     * Helper method to count the number of reviews in the repository
     */
    private int countReviews() {
        int count = 0;
        for (long i = 1; i <= 100; i++) { // Assuming no more than 100 reviews for test
            if (reviewRepository.findById(i).isPresent()) {
                count++;
            }
        }
        return count;
    }
}