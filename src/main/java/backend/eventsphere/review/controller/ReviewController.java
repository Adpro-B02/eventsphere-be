package backend.eventsphere.review.controller;

import backend.eventsphere.review.dto.ReviewCreateRequest;
import backend.eventsphere.review.dto.ReviewResponse;
import backend.eventsphere.review.dto.ReviewUpdateRequest;
import backend.eventsphere.review.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * REST API Controller for handling review operations.
 */
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    
    private final ReviewService reviewService;
    
    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }
    
    /**
     * Create a new review.
     */
    @PostMapping
    public ResponseEntity<?> createReview(@Valid @RequestBody ReviewCreateRequest request) {
        Optional<ReviewResponse> createdReview = reviewService.createReview(request);
        
        return createdReview
                .map(review -> new ResponseEntity<>(review, HttpStatus.CREATED))
                .orElse(new ResponseEntity<>(
                        new ErrorResponse("Invalid review data or user already reviewed this event"), 
                        HttpStatus.BAD_REQUEST));
    }
    
    /**
     * Update an existing review.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateReview(
            @PathVariable Long id, 
            @Valid @RequestBody ReviewUpdateRequest request) {
        Optional<ReviewResponse> updatedReview = reviewService.updateReview(id, request);
        
        return updatedReview
                .map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>(
                        new ErrorResponse("Review not found or invalid data"), 
                        HttpStatus.NOT_FOUND));
    }
    
    /**
     * Delete a review.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        boolean deleted = reviewService.deleteReview(id);
        
        return deleted
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
    
    /**
     * Get a review by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getReviewById(@PathVariable Long id) {
        Optional<ReviewResponse> review = reviewService.getReviewById(id);
        
        return review
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get all reviews for an event.
     */
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByEventId(@PathVariable Long eventId) {
        List<ReviewResponse> reviews = reviewService.getReviewsByEventId(eventId);
        return ResponseEntity.ok(reviews);
    }
    
    /**
     * Get all reviews by a user.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByUserId(@PathVariable Long userId) {
        List<ReviewResponse> reviews = reviewService.getReviewsByUserId(userId);
        return ResponseEntity.ok(reviews);
    }
    
    /**
     * Get a review by user ID and event ID.
     */
    @GetMapping("/user/{userId}/event/{eventId}")
    public ResponseEntity<ReviewResponse> getReviewByUserIdAndEventId(
            @PathVariable Long userId, 
            @PathVariable Long eventId) {
        Optional<ReviewResponse> review = reviewService.getReviewByUserIdAndEventId(userId, eventId);
        
        return review
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Simple error response class for REST API.
     */
    private static class ErrorResponse {
        private final String message;
        
        public ErrorResponse(String message) {
            this.message = message;
        }
        
        public String getMessage() {
            return message;
        }
    }
}