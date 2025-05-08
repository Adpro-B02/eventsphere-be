package backend.eventsphere.review.service;

import backend.eventsphere.review.model.Review;
import backend.eventsphere.review.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewServiceTest {

    private ReviewRepository reviewRepository;
    private ReviewService reviewService;

    private UUID userId;
    private UUID eventId;
    private UUID reviewId;

    @BeforeEach
    void setUp() {
        reviewRepository = mock(ReviewRepository.class);
        reviewService = new ReviewService(reviewRepository);

        userId = UUID.randomUUID();
        eventId = UUID.randomUUID();
        reviewId = UUID.randomUUID();
    }

    @Test
    void testCreateReviewSuccessfully() {
        when(reviewRepository.findByUserIdAndEventId(userId, eventId)).thenReturn(null);

        Review saved = new Review(reviewId, eventId, userId, "Nice!", 5, LocalDateTime.now(), LocalDateTime.now());
        when(reviewRepository.save(any())).thenReturn(saved);

        Review result = reviewService.createReview(eventId, userId, "Nice!", 5);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        verify(reviewRepository).save(any());
    }

    @Test
    void testCreateReviewThrowsIfAlreadyExists() {
        when(reviewRepository.findByUserIdAndEventId(userId, eventId)).thenReturn(
            new Review(reviewId, eventId, userId, "Nice!", 5, LocalDateTime.now(), LocalDateTime.now()));

        assertThrows(IllegalStateException.class, () -> {
            reviewService.createReview(eventId, userId, "Duplicate", 5);
        });
    }

    @Test
    void testUpdateReviewSuccessfully() {
        Review existing = new Review(reviewId, eventId, userId, "Old", 3, null, null);
        when(reviewRepository.findById(reviewId)).thenReturn(existing);

        Review updated = new Review(reviewId, eventId, userId, "Updated", 4, null, null);
        when(reviewRepository.update(reviewId, 4, "Updated")).thenReturn(updated);

        Review result = reviewService.updateReview(reviewId, userId, "Updated", 4);

        assertEquals("Updated", result.getComment());
        verify(reviewRepository).update(reviewId, 4, "Updated");
    }

    @Test
    void testUpdateReviewThrowsIfNotFound() {
        when(reviewRepository.findById(reviewId)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> {
            reviewService.updateReview(reviewId, userId, "Nope", 4);
        });
    }

    @Test
    void testUpdateReviewThrowsIfUserMismatch() {
        UUID otherUser = UUID.randomUUID();
        when(reviewRepository.findById(reviewId)).thenReturn(new Review(reviewId, eventId, otherUser, "Comment", 3, null, null));

        assertThrows(IllegalArgumentException.class, () -> {
            reviewService.updateReview(reviewId, userId, "Invalid", 4);
        });
    }

    @Test
    void testDeleteReviewSuccessfully() {
        when(reviewRepository.findById(reviewId)).thenReturn(new Review(reviewId, eventId, userId, "Del", 5, null, null));

        reviewService.deleteReview(reviewId, userId);

        verify(reviewRepository).delete(reviewId);
    }

    @Test
    void testDeleteReviewThrowsIfNotFound() {
        when(reviewRepository.findById(reviewId)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> {
            reviewService.deleteReview(reviewId, userId);
        });
    }

    @Test
    void testDeleteReviewThrowsIfUserMismatch() {
        UUID otherUser = UUID.randomUUID();
        when(reviewRepository.findById(reviewId)).thenReturn(new Review(reviewId, eventId, otherUser, "Comment", 3, null, null));

        assertThrows(IllegalArgumentException.class, () -> {
            reviewService.deleteReview(reviewId, userId);
        });
    }

    @Test
    void testGetReviewByUserAndEvent() {
        Review review = new Review(reviewId, eventId, userId, "Hi", 5, null, null);
        when(reviewRepository.findByUserIdAndEventId(userId, eventId)).thenReturn(review);

        Review result = reviewService.findByUserIdAndEventId(userId, eventId);
        assertEquals(review, result);
    }

    @Test
    void testGetReviewByUserAndEventReturnsNullIfNotFound() {
        when(reviewRepository.findByUserIdAndEventId(userId, eventId)).thenReturn(null);

        Review result = reviewService.findByUserIdAndEventId(userId, eventId);
        assertNull(result); // Assert that the result is null if the review doesn't exist
    }

    @Test
    void testGetReviewsByEventId() {
        List<Review> reviews = Arrays.asList(
            new Review(reviewId, eventId, userId, "Nice!", 5, LocalDateTime.now(), LocalDateTime.now()), 
            new Review(UUID.randomUUID(), eventId, UUID.randomUUID(), "Bad!", 1, LocalDateTime.now(), LocalDateTime.now())
        );
        when(reviewRepository.findByEventId(eventId)).thenReturn(reviews);

        List<Review> result = reviewService.findByEventId(eventId);
        assertEquals(2, result.size());
    }

    @Test
    void testGetReviewsByEventIdReturnsEmptyListIfNoReviews() {
        when(reviewRepository.findByEventId(eventId)).thenReturn(Collections.emptyList());

        List<Review> result = reviewService.findByEventId(eventId);
        assertTrue(result.isEmpty()); // Assert that the result is an empty list
    }

    @Test
    void testGetReviewsByUserId() {
        List<Review> reviews = Arrays.asList(
            new Review(reviewId, eventId, userId, "Nice!", 5, LocalDateTime.now(), LocalDateTime.now()), 
            new Review(UUID.randomUUID(), eventId, UUID.randomUUID(), "Bad!", 1, LocalDateTime.now(), LocalDateTime.now()),
            new Review(UUID.randomUUID(), eventId, userId, "Meh.", 3, LocalDateTime.now(), LocalDateTime.now())
        );
        when(reviewRepository.findByUserId(userId)).thenReturn(reviews);

        List<Review> result = reviewService.findByUserId(userId);
        assertEquals(3, result.size());
    }

    @Test
    void testGetReviewsByUserIdReturnsEmptyListIfNoReviews() {
        when(reviewRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

        List<Review> result = reviewService.findByUserId(userId);
        assertTrue(result.isEmpty()); // Assert that the result is an empty list
    }

    @Test
    void testCalculateAverageRatingForEvent() {
        when(reviewRepository.calculateAverageRatingByEventId(eventId)).thenReturn(4.2);

        double avg = reviewService.calculateAverageRatingByEventId(eventId);
        assertEquals(4.2, avg);
    }

    @Test
    void testCalculateAverageRatingForEventReturnsZeroIfNoReviews() {
        when(reviewRepository.calculateAverageRatingByEventId(eventId)).thenReturn(0.0);

        double avg = reviewService.calculateAverageRatingByEventId(eventId);
        assertEquals(0.0, avg); // Assert that the average is 0 when there are no reviews
    }
}
