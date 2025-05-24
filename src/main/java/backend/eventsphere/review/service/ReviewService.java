package backend.eventsphere.review.service;

import backend.eventsphere.review.model.Review;
import backend.eventsphere.review.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public Review createReview(UUID eventId, UUID userId, String comment, int rating) {
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
    }

    public Review updateReview(UUID reviewId, UUID userId, String comment, int rating) {
        Review review = reviewRepository.findById(reviewId);
        if (review == null) {
            throw new IllegalArgumentException("Review not found");
        }

        if (!review.getUserId().equals(userId)) {
            throw new IllegalArgumentException("You can only update your own reviews");
        }

        return reviewRepository.update(reviewId, rating, comment);
    }

    public void deleteReview(UUID reviewId, UUID userId) {
        Review review = reviewRepository.findById(reviewId);
        if (review == null) {
            throw new IllegalArgumentException("Review not found");
        }

        if (!review.getUserId().equals(userId)) {
            throw new IllegalArgumentException("You can only delete your own reviews");
        }

        reviewRepository.delete(reviewId);
    }

    public Review findById(UUID reviewId) {
        return reviewRepository.findById(reviewId);
    }

    public Review findByUserIdAndEventId(UUID userId, UUID eventId) {
        return reviewRepository.findByUserIdAndEventId(userId, eventId);
    }

    public List<Review> findByEventId(UUID eventId) {
        return reviewRepository.findByEventId(eventId);
    }

    public List<Review> findByUserId(UUID userId) {
        return reviewRepository.findByUserId(userId);
    }

    public double calculateAverageRatingByEventId(UUID eventId) {
        return reviewRepository.calculateAverageRatingByEventId(eventId);
    }
}