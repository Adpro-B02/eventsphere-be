package backend.eventsphere.review.service;

import backend.eventsphere.review.model.Review;
import backend.eventsphere.review.repository.ReviewRepository;
import backend.eventsphere.review.strategy.RatingCalculationStrategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReviewServiceTest {
    private ReviewRepository reviewRepository;
    private RatingCalculationStrategy ratingStrategy;
    private ReviewService reviewService;
    private UUID userId;
    private UUID eventId;
    private UUID reviewId;
    private Review sampleReview;

    @BeforeEach
    void setUp() {
        reviewRepository = mock(ReviewRepository.class);
        ratingStrategy = mock(RatingCalculationStrategy.class);
        reviewService = new ReviewService(reviewRepository, ratingStrategy);
        userId = UUID.randomUUID();
        eventId = UUID.randomUUID();
        reviewId = UUID.randomUUID();
        sampleReview = new Review(reviewId, eventId, userId, "Test Review", 5, 
            LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void whenCreateReview_thenSuccess() throws ExecutionException, InterruptedException {
        when(reviewRepository.findByUserIdAndEventId(userId, eventId)).thenReturn(null);
        when(reviewRepository.save(any(Review.class))).thenReturn(sampleReview);

        Review result = reviewService.createReviewAsync(eventId, userId, "Test Review", 5).get();
        
        assertNotNull(result);
        assertEquals(sampleReview, result);
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void whenCreateDuplicateReview_thenThrowException() {
        when(reviewRepository.findByUserIdAndEventId(userId, eventId)).thenReturn(sampleReview);

        Exception exception = assertThrows(ExecutionException.class, () -> 
            reviewService.createReviewAsync(eventId, userId, "Test Review", 5).get());
            
        assertTrue(exception.getCause() instanceof IllegalStateException);
    }

    @Test
    void whenUpdateOwnReview_thenSuccess() throws ExecutionException, InterruptedException {
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(sampleReview));
        when(reviewRepository.save(any(Review.class))).thenReturn(sampleReview);

        Review result = reviewService.updateReviewAsync(reviewId, userId, "Updated", 4).get();
        
        assertNotNull(result);
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void whenUpdateNonexistentReview_thenThrowException() {
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ExecutionException.class, () -> 
            reviewService.updateReviewAsync(reviewId, userId, "Updated", 4).get());
            
        assertTrue(exception.getCause() instanceof IllegalArgumentException);
    }

    @Test
    void whenUpdateReviewNotOwnedByUser_thenThrowException() {
        UUID anotherUserId = UUID.randomUUID();
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(sampleReview));

        ExecutionException exception = assertThrows(ExecutionException.class, () -> 
            reviewService.updateReviewAsync(reviewId, anotherUserId, "Updated Comment", 4).get()
        );
        assertTrue(exception.getCause() instanceof IllegalArgumentException);
        assertEquals("You can only update your own reviews", exception.getCause().getMessage());
    }

    @Test
    void whenDeleteOwnReview_thenSuccess() throws ExecutionException, InterruptedException {
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(sampleReview));
        doNothing().when(reviewRepository).deleteById(reviewId);

        assertDoesNotThrow(() -> reviewService.deleteReviewAsync(reviewId, userId).get());
        verify(reviewRepository).deleteById(reviewId);
    }

    @Test
    void testDeleteReviewAsyncThrowsIfNotFound() {
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty()); // Fix this

        CompletableFuture<Void> futureResult = reviewService.deleteReviewAsync(reviewId, userId);
        
        ExecutionException exception = assertThrows(ExecutionException.class, futureResult::get);
        assertTrue(exception.getCause() instanceof IllegalArgumentException);
    }

    @Test
    void whenDeleteNonexistentReview_thenThrowException() {
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        ExecutionException exception = assertThrows(ExecutionException.class, () -> 
            reviewService.deleteReviewAsync(reviewId, userId).get()
        );
        assertTrue(exception.getCause() instanceof IllegalArgumentException);
        assertEquals("Review not found", exception.getCause().getMessage());
    }

    @Test
    void whenDeleteReviewNotOwnedByUser_thenThrowException() {
        UUID anotherUserId = UUID.randomUUID();
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(sampleReview));

        ExecutionException exception = assertThrows(ExecutionException.class, () -> 
            reviewService.deleteReviewAsync(reviewId, anotherUserId).get()
        );
        assertTrue(exception.getCause() instanceof IllegalArgumentException);
        assertEquals("You can only delete your own reviews", exception.getCause().getMessage());
    }

    @Test
    void testFindByEventIdSuccess() throws ExecutionException, InterruptedException {
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
    void testFindByEventIdReturnsEmptyListWhenNotFound() throws ExecutionException, InterruptedException {
        when(reviewRepository.findByEventId(eventId)).thenReturn(Collections.emptyList());

        CompletableFuture<List<Review>> futureResult = reviewService.findByEventIdAsync(eventId);
        List<Review> result = futureResult.get();

        assertTrue(result.isEmpty());
        verify(reviewRepository).findByEventId(eventId);
    }

    @Test
    void testFindByUserIdSuccess() throws ExecutionException, InterruptedException {
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
    void testFindByUserIdReturnsEmptyListWhenNotFound() 
            throws ExecutionException, InterruptedException {
        when(reviewRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

        CompletableFuture<List<Review>> futureResult = reviewService.findByUserIdAsync(userId);
        List<Review> result = futureResult.get();

        assertTrue(result.isEmpty());
        verify(reviewRepository).findByUserId(userId);
    }

    @Test
    void testFindByIdSuccess() throws ExecutionException, InterruptedException {
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(sampleReview)); // Fix here

        CompletableFuture<Review> futureResult = reviewService.findByIdAsync(reviewId);
        Review result = futureResult.get();

        assertEquals(sampleReview, result); // Fix here
        verify(reviewRepository).findById(reviewId);
    }

    @Test
    void testFindByIdReturnsNullWhenNotFound() throws ExecutionException, InterruptedException {
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty()); // Fix here

        CompletableFuture<Review> futureResult = reviewService.findByIdAsync(reviewId);
        Review result = futureResult.get();

        assertNull(result);
        verify(reviewRepository).findById(reviewId);
    }

    @Test
    void testFindByUserIdAndEventIdSuccess() throws ExecutionException, InterruptedException {
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
    void testFindByUserIdAndEventIdReturnsNullWhenNotFound() throws ExecutionException, InterruptedException {
        when(reviewRepository.findByUserIdAndEventId(userId, eventId)).thenReturn(null);

        CompletableFuture<Review> futureResult = reviewService.findByUserIdAndEventIdAsync(userId, eventId);
        Review result = futureResult.get();

        assertNull(result);
        verify(reviewRepository).findByUserIdAndEventId(userId, eventId);
    }

    @Test
    void whenCalculateAverageRating_thenSuccess() throws ExecutionException, InterruptedException {
        List<Review> reviews = Arrays.asList(
            new Review(reviewId, eventId, userId, "Great!", 5, LocalDateTime.now(), LocalDateTime.now()),
            new Review(UUID.randomUUID(), eventId, UUID.randomUUID(), "Good", 4, LocalDateTime.now(), LocalDateTime.now())
        );
        when(reviewRepository.findByEventId(eventId)).thenReturn(reviews);
        when(ratingStrategy.calculateRating(reviews)).thenReturn(4.5);

        Double averageRating = reviewService.calculateAverageRatingByEventIdAsync(eventId).get();

        assertNotNull(averageRating);
        assertEquals(4.5, averageRating);
        verify(reviewRepository).findByEventId(eventId);
        verify(ratingStrategy).calculateRating(reviews);
    }

    @Test
    void whenCalculateAverageRatingWithNoReviews_thenReturnZero() throws ExecutionException, InterruptedException {
        when(reviewRepository.findByEventId(eventId)).thenReturn(Collections.emptyList());
        when(ratingStrategy.calculateRating(Collections.emptyList())).thenReturn(0.0);

        Double averageRating = reviewService.calculateAverageRatingByEventIdAsync(eventId).get();

        assertNotNull(averageRating);
        assertEquals(0.0, averageRating);
        verify(reviewRepository).findByEventId(eventId);
        verify(ratingStrategy).calculateRating(Collections.emptyList());
    }
}