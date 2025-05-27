package backend.eventsphere.review.controller;

import backend.eventsphere.review.dto.ReviewCreateDto;
import backend.eventsphere.review.model.Review;
import backend.eventsphere.review.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewControllerTest {
    private ReviewService reviewService;
    private ReviewController reviewController;
    private UUID userId;
    private UUID eventId;
    private UUID reviewId;
    private Review sampleReview;
    private ReviewCreateDto reviewCreateDto;

    @BeforeEach
    void setUp() {
        reviewService = mock(ReviewService.class);
        reviewController = new ReviewController(reviewService);
        userId = UUID.randomUUID();
        eventId = UUID.randomUUID();
        reviewId = UUID.randomUUID();
        
        sampleReview = new Review(reviewId, eventId, userId, "Test Review", 5, 
            LocalDateTime.now(), LocalDateTime.now());
        
        reviewCreateDto = new ReviewCreateDto(eventId, userId, "Test Review", 5);
    }

    @Test
    void whenGetEventReviews_thenSuccess() throws ExecutionException, InterruptedException {
        List<Review> expectedReviews = Arrays.asList(sampleReview);
        when(reviewService.findByEventIdAsync(eventId))
            .thenReturn(CompletableFuture.completedFuture(expectedReviews));

        ResponseEntity<List<Review>> response = 
            reviewController.getEventReviews(eventId).get();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedReviews, response.getBody());
    }

    @Test
    void whenGetEventReviews_withNoReviews_thenNotFound() throws ExecutionException, InterruptedException {
        when(reviewService.findByEventIdAsync(eventId))
            .thenReturn(CompletableFuture.completedFuture(Collections.emptyList()));

        ResponseEntity<List<Review>> response = 
            reviewController.getEventReviews(eventId).get();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void whenGetReview_thenSuccess() throws ExecutionException, InterruptedException {
        when(reviewService.findByIdAsync(reviewId))
            .thenReturn(CompletableFuture.completedFuture(sampleReview));

        ResponseEntity<Review> response = reviewController.getReview(reviewId).get();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sampleReview, response.getBody());
    }

    @Test
    void whenGetReview_withNonexistentReview_thenNotFound() throws ExecutionException, InterruptedException {
        when(reviewService.findByIdAsync(reviewId))
            .thenReturn(CompletableFuture.completedFuture(null));

        ResponseEntity<Review> response = reviewController.getReview(reviewId).get();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void whenCreateReview_thenSuccess() throws ExecutionException, InterruptedException {
        when(reviewService.createReviewAsync(
            reviewCreateDto.getEventId(),
            reviewCreateDto.getUserId(),
            reviewCreateDto.getComment(),
            reviewCreateDto.getRating()))
        .thenReturn(CompletableFuture.completedFuture(sampleReview));

        ResponseEntity<Review> response = 
            reviewController.createReview(reviewCreateDto).get();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(sampleReview, response.getBody());
    }

    @Test
    void whenCreateReview_withDuplicate_thenBadRequest() throws ExecutionException, InterruptedException {
        when(reviewService.createReviewAsync(
            reviewCreateDto.getEventId(),
            reviewCreateDto.getUserId(),
            reviewCreateDto.getComment(),
            reviewCreateDto.getRating()))
        .thenReturn(CompletableFuture.failedFuture(
            new IllegalStateException("Review already exists")));

        ResponseEntity<Review> response = 
            reviewController.createReview(reviewCreateDto).get();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void whenUpdateReview_thenSuccess() throws ExecutionException, InterruptedException {
        Review updatedReview = new Review(reviewId, eventId, userId, "Updated Review", 4, 
            sampleReview.getCreatedAt(), LocalDateTime.now());
        
        when(reviewService.updateReviewAsync(
            reviewId,
            reviewCreateDto.getUserId(),
            reviewCreateDto.getComment(),
            reviewCreateDto.getRating()))
        .thenReturn(CompletableFuture.completedFuture(updatedReview));

        ResponseEntity<Review> response = 
            reviewController.updateReview(reviewId, reviewCreateDto).get();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedReview, response.getBody());
    }

    @Test
    void whenUpdateReview_withNonexistentReview_thenBadRequest() throws ExecutionException, InterruptedException {
        when(reviewService.updateReviewAsync(
            reviewId,
            reviewCreateDto.getUserId(),
            reviewCreateDto.getComment(),
            reviewCreateDto.getRating()))
        .thenReturn(CompletableFuture.failedFuture(
            new IllegalArgumentException("Review not found")));

        ResponseEntity<Review> response = 
            reviewController.updateReview(reviewId, reviewCreateDto).get();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void whenUpdateReview_withWrongUser_thenBadRequest() throws ExecutionException, InterruptedException {
        when(reviewService.updateReviewAsync(
            reviewId,
            reviewCreateDto.getUserId(),
            reviewCreateDto.getComment(),
            reviewCreateDto.getRating()))
        .thenReturn(CompletableFuture.failedFuture(
            new IllegalArgumentException("You can only update your own reviews")));

        ResponseEntity<Review> response = 
            reviewController.updateReview(reviewId, reviewCreateDto).get();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void whenDeleteReview_thenSuccess() throws ExecutionException, InterruptedException {
        when(reviewService.deleteReviewAsync(reviewId, userId))
            .thenReturn(CompletableFuture.completedFuture(null));

        ResponseEntity<Void> response = 
            reviewController.deleteReview(reviewId, userId).get();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void whenDeleteReview_withNonexistentReview_thenBadRequest() throws ExecutionException, InterruptedException {
        when(reviewService.deleteReviewAsync(reviewId, userId))
            .thenReturn(CompletableFuture.failedFuture(
                new IllegalArgumentException("Review not found")));

        ResponseEntity<Void> response = 
            reviewController.deleteReview(reviewId, userId).get();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testGetAverageRating_whenSuccess() throws ExecutionException, InterruptedException {
        double expectedAverageRating = 4.5;
        when(reviewService.calculateAverageRatingByEventIdAsync(eventId))
            .thenReturn(CompletableFuture.completedFuture(expectedAverageRating));

        ResponseEntity<Double> response = reviewController.getAverageRating(eventId).get();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedAverageRating, response.getBody());
    }

    @Test
    void testGetAverageRating_whenInternalServerError() throws ExecutionException, InterruptedException {
        when(reviewService.calculateAverageRatingByEventIdAsync(eventId))
            .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Database error")));

        ResponseEntity<Double> response = reviewController.getAverageRating(eventId).get();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }
}