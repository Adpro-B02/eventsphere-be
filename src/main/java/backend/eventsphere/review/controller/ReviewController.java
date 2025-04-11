package backend.eventsphere.review.controller;

import backend.eventsphere.review.dto.ReviewCreateRequest;
import backend.eventsphere.review.dto.ReviewResponse;
import backend.eventsphere.review.dto.ReviewUpdateRequest;
import backend.eventsphere.review.service.ReviewService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody ReviewCreateRequest request) {
        Optional<ReviewResponse> createdReview = reviewService.createReview(request);
        
        if (createdReview.isPresent()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(createdReview.get());
        } else {
            Map<String, Object> errorResponse = createErrorMap("Failed to create review", 400);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReview(
            @PathVariable Long id,
            @RequestBody ReviewUpdateRequest request) {
        Optional<ReviewResponse> updatedReview = reviewService.updateReview(id, request);
        
        if (updatedReview.isPresent()) {
            return ResponseEntity.ok(updatedReview.get());
        } else {
            Map<String, Object> errorResponse = createErrorMap("Review not found", 404);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable Long id) {
        boolean deleted = reviewService.deleteReview(id);
        
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            Map<String, Object> errorResponse = createErrorMap("Review not found", 404);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReviewById(@PathVariable Long id) {
        Optional<ReviewResponse> review = reviewService.getReviewById(id);
        
        if (review.isPresent()) {
            return ResponseEntity.ok(review.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByEventId(@PathVariable Long eventId) {
        List<ReviewResponse> reviews = reviewService.getReviewsByEventId(eventId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByUserId(@PathVariable Long userId) {
        List<ReviewResponse> reviews = reviewService.getReviewsByUserId(userId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/user/{userId}/event/{eventId}")
    public ResponseEntity<?> getReviewByUserIdAndEventId(
            @PathVariable Long userId,
            @PathVariable Long eventId) {
        Optional<ReviewResponse> review = reviewService.getReviewByUserIdAndEventId(userId, eventId);
        
        if (review.isPresent()) {
            return ResponseEntity.ok(review.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Helper method to create error response map
    private Map<String, Object> createErrorMap(String message, int status) {
        Map<String, Object> errorMap = new HashMap<>();
        errorMap.put("message", message);
        errorMap.put("status", status);
        return errorMap;
    }
}