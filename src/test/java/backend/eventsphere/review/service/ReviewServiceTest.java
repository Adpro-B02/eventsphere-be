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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    void testCreateReviewAsyncSuccessfully() throws ExecutionException, InterruptedException {
        when(reviewRepository.findByUserIdAndEventId(userId, eventId)).thenReturn(null);
        Review saved = new Review(reviewId, eventId, userId, "Nice!", 5, LocalDateTime.now(), LocalDateTime.now());
        when(reviewRepository.save(any())).thenReturn(saved);

        CompletableFuture<Review> futureResult = reviewService.createReviewAsync(eventId, userId, "Nice!", 5);
        Review result = futureResult.get();

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        verify(reviewRepository).save(any());
    }

    @Test
    void testCreateReviewAsyncThrowsIfAlreadyExists() {
        when(reviewRepository.findByUserIdAndEventId(userId, eventId))
            .thenReturn(new Review(reviewId, eventId, userId, "Nice!", 5, LocalDateTime.now(), LocalDateTime.now()));

        CompletableFuture<Review> futureResult = reviewService.createReviewAsync(eventId, userId, "Duplicate", 5);
        
        ExecutionException exception = assertThrows(ExecutionException.class, futureResult::get);
        assertTrue(exception.getCause() instanceof IllegalStateException);
    }

    @Test
    void testUpdateReviewAsyncSuccessfully() throws ExecutionException, InterruptedException {
        Review existing = new Review(reviewId, eventId, userId, "Old", 3, LocalDateTime.now(), LocalDateTime.now());
        when(reviewRepository.findById(reviewId)).thenReturn(existing);

        Review updated = new Review(reviewId, eventId, userId, "Updated", 4, existing.getCreatedAt(), LocalDateTime.now());
        when(reviewRepository.update(reviewId, 4, "Updated")).thenReturn(updated);

        CompletableFuture<Review> futureResult = reviewService.updateReviewAsync(reviewId, userId, "Updated", 4);
        Review result = futureResult.get();

        assertEquals("Updated", result.getComment());
        verify(reviewRepository).update(reviewId, 4, "Updated");
    }

    @Test
    void testUpdateReviewAsyncThrowsIfNotFound() {
        when(reviewRepository.findById(reviewId)).thenReturn(null);

        CompletableFuture<Review> futureResult = reviewService.updateReviewAsync(reviewId, userId, "Nope", 4);
        
        ExecutionException exception = assertThrows(ExecutionException.class, futureResult::get);
        assertTrue(exception.getCause() instanceof IllegalArgumentException);
    }

    @Test
    void testDeleteReviewAsyncSuccessfully() throws ExecutionException, InterruptedException {
        when(reviewRepository.findById(reviewId))
            .thenReturn(new Review(reviewId, eventId, userId, "Del", 5, LocalDateTime.now(), LocalDateTime.now()));

        CompletableFuture<Void> futureResult = reviewService.deleteReviewAsync(reviewId, userId);
        futureResult.get(); // Wait for completion

        verify(reviewRepository).delete(reviewId);
    }

    @Test
    void testDeleteReviewAsyncThrowsIfNotFound() {
        when(reviewRepository.findById(reviewId)).thenReturn(null);

        CompletableFuture<Void> futureResult = reviewService.deleteReviewAsync(reviewId, userId);
        
        ExecutionException exception = assertThrows(ExecutionException.class, futureResult::get);
        assertTrue(exception.getCause() instanceof IllegalArgumentException);
    }

    @Test
    void testFindByEventIdAsyncSuccess() throws ExecutionException, InterruptedException {
        List<Review> reviews = Arrays.asList(
            new Review(reviewId, eventId, userId, "Nice!", 5, LocalDateTime.now(), LocalDateTime.now()),
            new Review(UUID.randomUUID(), eventId, UUID.randomUUID(), "Bad!", 1, LocalDateTime.now(), LocalDateTime.now())
        );
        when(reviewRepository.findByEventId(eventId)).thenReturn(reviews);

        CompletableFuture<List<Review>> futureResult = reviewService.findByEventIdAsync(eventId);
        List<Review> result = futureResult.get();

        assertEquals(2, result.size());
        verify(reviewRepository).findByEventId(eventId);
    }

    @Test
    void testFindByEventIdAsyncReturnsEmptyListWhenNotFound() throws ExecutionException, InterruptedException {
        when(reviewRepository.findByEventId(eventId)).thenReturn(Collections.emptyList());

        CompletableFuture<List<Review>> futureResult = reviewService.findByEventIdAsync(eventId);
        List<Review> result = futureResult.get();

        assertTrue(result.isEmpty());
        verify(reviewRepository).findByEventId(eventId);
    }

    @Test
    void testFindByUserIdAsyncSuccess() throws ExecutionException, InterruptedException {
        List<Review> reviews = Arrays.asList(
            new Review(reviewId, eventId, userId, "Nice!", 5, LocalDateTime.now(), LocalDateTime.now()),
            new Review(UUID.randomUUID(), UUID.randomUUID(), userId, "Good!", 4, 
                LocalDateTime.now(), LocalDateTime.now())
        );
        when(reviewRepository.findByUserId(userId)).thenReturn(reviews);

        CompletableFuture<List<Review>> futureResult = reviewService.findByUserIdAsync(userId);
        List<Review> result = futureResult.get();

        assertEquals(2, result.size());
        verify(reviewRepository).findByUserId(userId);
    }

    @Test
    void testFindByUserIdAsyncReturnsEmptyListWhenNotFound() 
            throws ExecutionException, InterruptedException {
        when(reviewRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

        CompletableFuture<List<Review>> futureResult = reviewService.findByUserIdAsync(userId);
        List<Review> result = futureResult.get();

        assertTrue(result.isEmpty());
        verify(reviewRepository).findByUserId(userId);
    }

    @Test
    void testFindByIdAsyncSuccess() throws ExecutionException, InterruptedException {
        Review review = new Review(reviewId, eventId, userId, "Test", 5, LocalDateTime.now(), LocalDateTime.now());
        when(reviewRepository.findById(reviewId)).thenReturn(review);

        CompletableFuture<Review> futureResult = reviewService.findByIdAsync(reviewId);
        Review result = futureResult.get();

        assertEquals(review, result);
        verify(reviewRepository).findById(reviewId);
    }

    @Test
    void testFindByIdAsyncReturnsNullWhenNotFound() throws ExecutionException, InterruptedException {
        when(reviewRepository.findById(reviewId)).thenReturn(null);

        CompletableFuture<Review> futureResult = reviewService.findByIdAsync(reviewId);
        Review result = futureResult.get();

        assertNull(result);
        verify(reviewRepository).findById(reviewId);
    }

    @Test
    void testFindByUserIdAndEventIdAsyncSuccess() throws ExecutionException, InterruptedException {
        Review expectedReview = new Review(reviewId, eventId, userId, "Test Review", 5, 
            LocalDateTime.now(), LocalDateTime.now());
        when(reviewRepository.findByUserIdAndEventId(userId, eventId)).thenReturn(expectedReview);
    
        CompletableFuture<Review> futureResult = reviewService.findByUserIdAndEventIdAsync(userId, eventId);
        Review result = futureResult.get();
    
        assertNotNull(result);
        assertEquals(expectedReview, result);
        assertEquals("Test Review", result.getComment());
        assertEquals(5, result.getRating());
        verify(reviewRepository).findByUserIdAndEventId(userId, eventId);
    }

    @Test
    void testFindByUserIdAndEventIdAsyncReturnsNullWhenNotFound() throws ExecutionException, InterruptedException {
        when(reviewRepository.findByUserIdAndEventId(userId, eventId)).thenReturn(null);

        CompletableFuture<Review> futureResult = reviewService.findByUserIdAndEventIdAsync(userId, eventId);
        Review result = futureResult.get();

        assertNull(result);
        verify(reviewRepository).findByUserIdAndEventId(userId, eventId);
    }

    @Test
    void testCalculateAverageRatingByEventIdAsync() throws ExecutionException, InterruptedException {
        when(reviewRepository.calculateAverageRatingByEventId(eventId)).thenReturn(4.2);

        CompletableFuture<Double> futureResult = reviewService.calculateAverageRatingByEventIdAsync(eventId);
        double result = futureResult.get();

        assertEquals(4.2, result);
        verify(reviewRepository).calculateAverageRatingByEventId(eventId);
    }

    @Test
    void testCalculateAverageRatingByEventIdAsyncReturnsZeroWhenNoReviews() 
            throws ExecutionException, InterruptedException {
        when(reviewRepository.calculateAverageRatingByEventId(eventId)).thenReturn(0.0);

        CompletableFuture<Double> futureResult = 
            reviewService.calculateAverageRatingByEventIdAsync(eventId);
        double result = futureResult.get();

        assertEquals(0.0, result);
        verify(reviewRepository).calculateAverageRatingByEventId(eventId);
    }
}