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
        UUID reviewId = review.getId() != null ? review.getId() : UUID.randomUUID();

        review = new Review(
            reviewId,
            review.getEventId(),
            review.getUserId(),
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

        return review;
    }
    

    public Review update(UUID reviewId, int rating, String comment) {
        Review reviewToUpdate = findById(reviewId);
        if (reviewToUpdate == null) {
            return null;
        }

        Review updatedReview = new Review(
            reviewToUpdate.getId(),
            reviewToUpdate.getEventId(),
            reviewToUpdate.getUserId(),
            comment,
            rating,
            reviewToUpdate.getCreatedAt(),
            LocalDateTime.now()
        );

        for (int i = 0; i < reviewData.size(); i++) {
            if (reviewData.get(i).getId().equals(updatedReview.getId())) {
                reviewData.set(i, updatedReview);

                List<Review> eventReviewsList = eventReviews.get(updatedReview.getEventId());
                if (eventReviewsList != null) {
                    for (int j = 0; j < eventReviewsList.size(); j++) {
                        if (eventReviewsList.get(j).getId().equals(updatedReview.getId())) {
                            eventReviewsList.set(j, updatedReview);
                            break;
                        }
                    }
                }

                List<Review> userReviewsList = userReviews.get(updatedReview.getUserId());
                if (userReviewsList != null) {
                    for (int j = 0; j < userReviewsList.size(); j++) {
                        if (userReviewsList.get(j).getId().equals(updatedReview.getId())) {
                            userReviewsList.set(j, updatedReview);
                            break;
                        }
                    }
                }

                break;
            }
        }
        return updatedReview;
    }

    public void delete(UUID reviewId) {
        Review reviewToDelete = findById(reviewId);

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