package backend.eventsphere.review.controller;

import backend.eventsphere.review.model.Review;
import backend.eventsphere.review.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReviewControllerTest {

    private ReviewService reviewService;
    private ReviewController reviewController;

    private UUID userId;
    private UUID eventId;
    private UUID reviewId;

    @BeforeEach
    void setUp() {
        reviewService = mock(ReviewService.class);
        reviewController = new ReviewController(reviewService);

        userId = UUID.randomUUID();
        eventId = UUID.randomUUID();
        reviewId = UUID.randomUUID();
    }

    @Test
    void testGetEventReviewsReturnsReviewsWhenFound() {
        List<Review> reviews = Arrays.asList(
            new Review(reviewId, eventId, userId, "Great event!", 5, LocalDateTime.now(), LocalDateTime.now()),
            new Review(UUID.randomUUID(), eventId, UUID.randomUUID(), "Good experience", 4, LocalDateTime.now(), LocalDateTime.now())
        );
        when(reviewService.findByEventId(eventId)).thenReturn(reviews);
        
        ResponseEntity<List<Review>> response = reviewController.getEventReviews(eventId);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(reviewService).findByEventId(eventId);
    }

    @Test
    void testGetEventReviewsReturnsNotFoundWhenEmpty() {
        when(reviewService.findByEventId(eventId)).thenReturn(Collections.emptyList());
        
        ResponseEntity<List<Review>> response = reviewController.getEventReviews(eventId);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(reviewService).findByEventId(eventId);
    }

    @Test
    void testGetReviewReturnsReviewWhenFound() {
        Review review = new Review(reviewId, eventId, userId, "Detailed feedback", 4, LocalDateTime.now(), LocalDateTime.now());
        when(reviewService.findById(reviewId)).thenReturn(review);
        
        ResponseEntity<Review> response = reviewController.getReview(reviewId);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(reviewId, response.getBody().getId());
        verify(reviewService).findById(reviewId);
    }

    @Test
    void testGetReviewReturnsNotFoundWhenMissing() {
        when(reviewService.findById(reviewId)).thenReturn(null);
        
        ResponseEntity<Review> response = reviewController.getReview(reviewId);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(reviewService).findById(reviewId);
    }

    @Test
    void testCreateReviewSuccessfully() {
        Review created = new Review(reviewId, eventId, userId, "New review", 5, LocalDateTime.now(), LocalDateTime.now());
        when(reviewService.createReview(eventId, userId, "New review", 5)).thenReturn(created);
        
        ResponseEntity<Review> response = reviewController.createReview(eventId, userId, "New review", 5);
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("New review", response.getBody().getComment());
        assertEquals(5, response.getBody().getRating());
        verify(reviewService).createReview(eventId, userId, "New review", 5);
    }

    @Test
    void testCreateReviewHandlesException() {
        when(reviewService.createReview(eq(eventId), eq(userId), any(), anyInt()))
                .thenThrow(new IllegalStateException("User already reviewed this event"));
        
        ResponseEntity<Review> response = reviewController.createReview(eventId, userId, "Duplicate", 4);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testUpdateReviewSuccessfully() {
        Review updated = new Review(reviewId, eventId, userId, "Updated content", 3, 
                LocalDateTime.now(), LocalDateTime.now());
        when(reviewService.updateReview(reviewId, userId, "Updated content", 3)).thenReturn(updated);
        
        ResponseEntity<Review> response = reviewController.updateReview(reviewId, userId, "Updated content", 3);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Updated content", response.getBody().getComment());
        assertEquals(3, response.getBody().getRating());
        verify(reviewService).updateReview(reviewId, userId, "Updated content", 3);
    }

    @Test
    void testUpdateReviewHandlesNotFoundException() {
        when(reviewService.updateReview(eq(reviewId), eq(userId), any(), anyInt()))
                .thenThrow(new IllegalArgumentException("Review not found"));
        
        ResponseEntity<Review> response = reviewController.updateReview(reviewId, userId, "Won't update", 2);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testUpdateReviewHandlesUserMismatchException() {
        when(reviewService.updateReview(eq(reviewId), eq(userId), any(), anyInt()))
                .thenThrow(new IllegalArgumentException("User doesn't own this review"));
        
        ResponseEntity<Review> response = reviewController.updateReview(reviewId, userId, "Not mine", 2);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testDeleteReviewSuccessfully() {
        doNothing().when(reviewService).deleteReview(reviewId, userId);
        
        ResponseEntity<Void> response = reviewController.deleteReview(reviewId, userId);
        
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(reviewService).deleteReview(reviewId, userId);
    }

    @Test
    void testDeleteReviewHandlesNotFoundException() {
        doThrow(new IllegalArgumentException("Review not found")).when(reviewService).deleteReview(reviewId, userId);
        
        ResponseEntity<Void> response = reviewController.deleteReview(reviewId, userId);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testDeleteReviewHandlesUserMismatchException() {
        doThrow(new IllegalArgumentException("User doesn't own this review")).when(reviewService).deleteReview(reviewId, userId);
        
        ResponseEntity<Void> response = reviewController.deleteReview(reviewId, userId);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}