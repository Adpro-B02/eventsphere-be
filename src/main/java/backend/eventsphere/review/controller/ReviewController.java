package backend.eventsphere.review.controller;

import backend.eventsphere.review.model.Review;
import backend.eventsphere.review.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/event/{eventId}")
    public CompletableFuture<ResponseEntity<List<Review>>> getEventReviews(@PathVariable UUID eventId) {
        return reviewService.findByEventIdAsync(eventId)
            .thenApply(reviews -> reviews.isEmpty() 
                ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
                : new ResponseEntity<>(reviews, HttpStatus.OK));
    }

    @GetMapping("/{reviewId}")
    public CompletableFuture<ResponseEntity<Review>> getReview(@PathVariable UUID reviewId) {
        return reviewService.findByIdAsync(reviewId)
            .thenApply(review -> review == null
                ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
                : new ResponseEntity<>(review, HttpStatus.OK));
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<Review>> createReview(
            @RequestParam UUID eventId,
            @RequestParam UUID userId,
            @RequestParam String comment,
            @RequestParam int rating) {
        return reviewService.createReviewAsync(eventId, userId, comment, rating)
            .thenApply(review -> new ResponseEntity<>(review, HttpStatus.CREATED))
            .exceptionally(ex -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @PutMapping("/{reviewId}")
    public CompletableFuture<ResponseEntity<Review>> updateReview(
            @PathVariable UUID reviewId,
            @RequestParam UUID userId,
            @RequestParam String comment,
            @RequestParam int rating) {
        return reviewService.updateReviewAsync(reviewId, userId, comment, rating)
            .thenApply(review -> new ResponseEntity<>(review, HttpStatus.OK))
            .exceptionally(ex -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @DeleteMapping("/{reviewId}")
    public CompletableFuture<ResponseEntity<Void>> deleteReview(
            @PathVariable UUID reviewId,
            @RequestParam UUID userId) {
        return reviewService.deleteReviewAsync(reviewId, userId)
            .thenApply(result -> new ResponseEntity<Void>(HttpStatus.NO_CONTENT))
            .exceptionally(ex -> new ResponseEntity<Void>(HttpStatus.BAD_REQUEST));
    }
}