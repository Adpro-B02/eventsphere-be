package backend.eventsphere.review.repository;

import backend.eventsphere.review.model.Review;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

@Repository
public class ReviewRepository {

    private final List<Review> reviewData = new ArrayList<>();
    private final Map<UUID, List<Review>> eventReviews = new HashMap<>();
    private final Map<UUID, List<Review>> userReviews = new HashMap<>();

    public Review save(Review review) {
        boolean hasExistingReview = false;
        for (Review r : reviewData) {
            if (r.getUserId().equals(review.getUserId()) &&
                r.getEventId().equals(review.getEventId())) {
                hasExistingReview = true;
                break;
            }
        }

        if (hasExistingReview && review.getId() == null) {
            throw new IllegalStateException("User already has a review for this event");
        }

        if (review.getId() == null) {
            review = new Review(
                null,
                review.getUserId(),
                review.getEventId(),
                review.getComment(),
                review.getRating(),
                LocalDateTime.now(),
                LocalDateTime.now()
            );

            reviewData.add(review);

            eventReviews
                .computeIfAbsent(review.getEventId(), k -> new ArrayList<>())
                .add(review);

            userReviews
                .computeIfAbsent(review.getUserId(), k -> new ArrayList<>())
                .add(review);
        } else {
            for (int i = 0; i < reviewData.size(); i++) {
                if (reviewData.get(i).getId().equals(review.getId())) {
                    reviewData.set(i, review);

                    List<Review> eventReviewsList = eventReviews.get(review.getEventId());
                    if (eventReviewsList != null) {
                        for (int j = 0; j < eventReviewsList.size(); j++) {
                            if (eventReviewsList.get(j).getId().equals(review.getId())) {
                                eventReviewsList.set(j, review);
                                break;
                            }
                        }
                    }

                    List<Review> userReviewsList = userReviews.get(review.getUserId());
                    if (userReviewsList != null) {
                        for (int j = 0; j < userReviewsList.size(); j++) {
                            if (userReviewsList.get(j).getId().equals(review.getId())) {
                                userReviewsList.set(j, review);
                                break;
                            }
                        }
                    }

                    break;
                }
            }
        }
        return review;
    }

    public Review update(UUID reviewId, int rating, String comment) {
        Review reviewToUpdate = findById(reviewId);
        if (reviewToUpdate == null) {
            throw new IllegalArgumentException("Review not found");
        }

        Review updatedReview = new Review(
            reviewToUpdate.getId(),
            reviewToUpdate.getUserId(),
            reviewToUpdate.getEventId(),
            comment,
            rating,
            reviewToUpdate.getCreatedAt(),
            LocalDateTime.now()
        );

        return save(updatedReview);
    }

    public void delete(UUID reviewId) {
        Review reviewToDelete = findById(reviewId);
        if (reviewToDelete == null) {
            throw new IllegalArgumentException("Review not found");
        }

        for (int i = 0; i < reviewData.size(); i++) {
            if (reviewData.get(i).getId().equals(reviewId)) {
                reviewData.remove(i);
                break;
            }
        }

        List<Review> eventReviewsList = eventReviews.get(reviewToDelete.getEventId());
        if (eventReviewsList != null) {
            for (int i = 0; i < eventReviewsList.size(); i++) {
                if (eventReviewsList.get(i).getId().equals(reviewId)) {
                    eventReviewsList.remove(i);
                    break;
                }
            }
        }

        List<Review> userReviewsList = userReviews.get(reviewToDelete.getUserId());
        if (userReviewsList != null) {
            for (int i = 0; i < userReviewsList.size(); i++) {
                if (userReviewsList.get(i).getId().equals(reviewId)) {
                    userReviewsList.remove(i);
                    break;
                }
            }
        }
    }

    public Review findById(UUID reviewId) {
        for (Review review : reviewData) {
            if (review.getId().equals(reviewId)) {
                return review;
            }
        }
        return null;
    }

    public List<Review> findByEventId(UUID eventId) {
        List<Review> result = eventReviews.get(eventId);
        return result != null ? new ArrayList<>(result) : new ArrayList<>();
    }

    public List<Review> findByUserId(UUID userId) {
        List<Review> result = userReviews.get(userId);
        return result != null ? new ArrayList<>(result) : new ArrayList<>();
    }

    public Review findByUserIdAndEventId(UUID userId, UUID eventId) {
        for (Review review : reviewData) {
            if (review.getUserId().equals(userId) && review.getEventId().equals(eventId)) {
                return review;
            }
        }
        return null;
    }

    public List<Review> findAll() {
        return new ArrayList<>(reviewData);
    }

    public double calculateAverageRatingByEventId(UUID eventId) {
        List<Review> reviews = eventReviews.get(eventId);
        if (reviews == null || reviews.isEmpty()) {
            return 0.0;
        }

        int totalRating = 0;
        for (Review review : reviews) {
            totalRating += review.getRating();
        }

        return (double) totalRating / reviews.size();
    }
}
