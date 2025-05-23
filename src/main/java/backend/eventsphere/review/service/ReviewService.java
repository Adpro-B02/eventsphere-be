package backend.eventsphere.review.service;

import backend.eventsphere.review.model.Review;
import backend.eventsphere.review.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ExecutorService executorService;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
        this.executorService = Executors.newFixedThreadPool(10);
    }

    public CompletableFuture<Review> createReviewAsync(UUID eventId, UUID userId, String comment, int rating) {
        return CompletableFuture.supplyAsync(() -> {
            Review existingReview = reviewRepository.findByUserIdAndEventId(userId, eventId);
            if (existingReview != null) {
                throw new IllegalStateException("You have already reviewed this event");
            }

            Review newReview = new Review(
                null,
                eventId,
                userId,
                comment,
                rating,
                LocalDateTime.now(),
                LocalDateTime.now()
            );

            return reviewRepository.save(newReview);
        }, executorService);
    }

    public CompletableFuture<Review> updateReviewAsync(UUID reviewId, UUID userId, String comment, int rating) {
        return CompletableFuture.supplyAsync(() -> {
            Review review = reviewRepository.findById(reviewId);
            if (review == null) {
                throw new IllegalArgumentException("Review not found");
            }

            if (!review.getUserId().equals(userId)) {
                throw new IllegalArgumentException("You can only update your own reviews");
            }

            return reviewRepository.update(reviewId, rating, comment);
        }, executorService);
    }

    public CompletableFuture<Void> deleteReviewAsync(UUID reviewId, UUID userId) {
        return CompletableFuture.runAsync(() -> {
            Review review = reviewRepository.findById(reviewId);
            if (review == null) {
                throw new IllegalArgumentException("Review not found");
            }

            if (!review.getUserId().equals(userId)) {
                throw new IllegalArgumentException("You can only delete your own reviews");
            }

            reviewRepository.delete(reviewId);
        }, executorService);
    }

    public CompletableFuture<Review> findByIdAsync(UUID reviewId) {
        return CompletableFuture.supplyAsync(() -> 
            reviewRepository.findById(reviewId), executorService);
    }

    public CompletableFuture<List<Review>> findByEventIdAsync(UUID eventId) {
        return CompletableFuture.supplyAsync(() ->
            reviewRepository.findByEventId(eventId), executorService);
    }

    public CompletableFuture<List<Review>> findByUserIdAsync(UUID userId) {
        return CompletableFuture.supplyAsync(() ->
            reviewRepository.findByUserId(userId), executorService);
    }

    public CompletableFuture<Review> findByUserIdAndEventIdAsync(UUID userId, UUID eventId) {
        return CompletableFuture.supplyAsync(() -> 
            reviewRepository.findByUserIdAndEventId(userId, eventId), executorService);
    }

    public CompletableFuture<Double> calculateAverageRatingByEventIdAsync(UUID eventId) {
        return CompletableFuture.supplyAsync(() ->
            reviewRepository.calculateAverageRatingByEventId(eventId), executorService);
    }
}