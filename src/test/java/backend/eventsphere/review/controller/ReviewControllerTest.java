package backend.eventsphere.review.controller;

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

    @BeforeEach
    void setUp() {
        reviewService = mock(ReviewService.class);
        reviewController = new ReviewController(reviewService);
        userId = UUID.randomUUID();
        eventId = UUID.randomUUID();
        reviewId = UUID.randomUUID();
        sampleReview = new Review(reviewId, eventId, userId, "Test Review", 5, 
            LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void testGetEventReviewsSuccess() throws ExecutionException, InterruptedException {
        List<Review> expectedReviews = Arrays.asList(sampleReview);
        when(reviewService.findByEventIdAsync(eventId))
            .thenReturn(CompletableFuture.completedFuture(expectedReviews));

        CompletableFuture<ResponseEntity<List<Review>>> futureResponse = 
            reviewController.getEventReviews(eventId);
        ResponseEntity<List<Review>> response = futureResponse.get();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedReviews, response.getBody());
    }

    @Test
    void testGetEventReviewsNotFound() throws ExecutionException, InterruptedException {
        when(reviewService.findByEventIdAsync(eventId))
            .thenReturn(CompletableFuture.completedFuture(Collections.emptyList()));

        CompletableFuture<ResponseEntity<List<Review>>> futureResponse = 
            reviewController.getEventReviews(eventId);
        ResponseEntity<List<Review>> response = futureResponse.get();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetReviewSuccess() throws ExecutionException, InterruptedException {
        when(reviewService.findByIdAsync(reviewId))
            .thenReturn(CompletableFuture.completedFuture(sampleReview));

        CompletableFuture<ResponseEntity<Review>> futureResponse = 
            reviewController.getReview(reviewId);
        ResponseEntity<Review> response = futureResponse.get();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sampleReview, response.getBody());
    }

    @Test
    void testGetReviewNotFound() throws ExecutionException, InterruptedException {
        when(reviewService.findByIdAsync(reviewId))
            .thenReturn(CompletableFuture.completedFuture(null));

        CompletableFuture<ResponseEntity<Review>> futureResponse = 
            reviewController.getReview(reviewId);
        ResponseEntity<Review> response = futureResponse.get();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testCreateReviewSuccess() throws ExecutionException, InterruptedException {
        when(reviewService.createReviewAsync(eventId, userId, "Test Review", 5))
            .thenReturn(CompletableFuture.completedFuture(sampleReview));

        CompletableFuture<ResponseEntity<Review>> futureResponse = 
            reviewController.createReview(eventId, userId, "Test Review", 5);
        ResponseEntity<Review> response = futureResponse.get();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(sampleReview, response.getBody());
    }

    @Test
    void testCreateReviewFailure() throws ExecutionException, InterruptedException {
        when(reviewService.createReviewAsync(eventId, userId, "Test Review", 5))
            .thenReturn(CompletableFuture.failedFuture(
                new IllegalStateException("Review already exists")));

        CompletableFuture<ResponseEntity<Review>> futureResponse = 
            reviewController.createReview(eventId, userId, "Test Review", 5);
        ResponseEntity<Review> response = futureResponse.get();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testUpdateReviewSuccess() throws ExecutionException, InterruptedException {
        Review updatedReview = new Review(reviewId, eventId, userId, "Updated Review", 4, 
            sampleReview.getCreatedAt(), LocalDateTime.now());
        when(reviewService.updateReviewAsync(reviewId, userId, "Updated Review", 4))
            .thenReturn(CompletableFuture.completedFuture(updatedReview));

        CompletableFuture<ResponseEntity<Review>> futureResponse = 
            reviewController.updateReview(reviewId, userId, "Updated Review", 4);
        ResponseEntity<Review> response = futureResponse.get();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedReview, response.getBody());
    }

    @Test
    void testUpdateReviewFailure() throws ExecutionException, InterruptedException {
        when(reviewService.updateReviewAsync(reviewId, userId, "Updated Review", 4))
            .thenReturn(CompletableFuture.failedFuture(
                new IllegalArgumentException("Review not found or user mismatch")));

        CompletableFuture<ResponseEntity<Review>> futureResponse = 
            reviewController.updateReview(reviewId, userId, "Updated Review", 4);
        ResponseEntity<Review> response = futureResponse.get();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testDeleteReviewSuccess() throws ExecutionException, InterruptedException {
        when(reviewService.deleteReviewAsync(reviewId, userId))
            .thenReturn(CompletableFuture.completedFuture(null));

        CompletableFuture<ResponseEntity<Void>> futureResponse = 
            reviewController.deleteReview(reviewId, userId);
        ResponseEntity<Void> response = futureResponse.get();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testDeleteReviewFailure() throws ExecutionException, InterruptedException {
        when(reviewService.deleteReviewAsync(reviewId, userId))
            .thenReturn(CompletableFuture.failedFuture(
                new IllegalArgumentException("Review not found")));

        CompletableFuture<ResponseEntity<Void>> futureResponse = 
            reviewController.deleteReview(reviewId, userId);
        ResponseEntity<Void> response = futureResponse.get();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}