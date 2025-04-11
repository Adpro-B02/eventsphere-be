package backend.eventsphere.review.repository;

import backend.eventsphere.review.model.Review;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ReviewRepository {
    private final Map<Long, Review> reviewStorage = new HashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public Optional<Review> findById(Long id) {
        return Optional.ofNullable(reviewStorage.get(id));
    }

    public Review save(Review review) {
        Review savedReview = new Review(
            review.getId() == null ? nextId.getAndIncrement() : review.getId(),
            review.getEventId(),
            review.getUserId(),
            review.getRating(),
            review.getComment()
        );
        reviewStorage.put(savedReview.getId(), savedReview);
        return savedReview;
    }

    public void deleteById(Long id) {
        reviewStorage.remove(id);
    }

    public List<Review> findByEventId(Long eventId) {
        List<Review> result = new ArrayList<>();
        reviewStorage.values().stream()
            .filter(review -> eventId.equals(review.getEventId()))
            .forEach(result::add);
        return result;
    }

    public List<Review> findByUserId(Long userId) {
        List<Review> result = new ArrayList<>();
        reviewStorage.values().stream()
            .filter(review -> userId.equals(review.getUserId()))
            .forEach(result::add);
        return result;
    }

    public Optional<Review> findByUserIdAndEventId(Long userId, Long eventId) {
        return reviewStorage.values().stream()
            .filter(review -> userId.equals(review.getUserId()) && eventId.equals(review.getEventId()))
            .findFirst();
    }
}