package backend.eventsphere.review.repository;

import backend.eventsphere.review.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    Review findByUserIdAndEventId(UUID userId, UUID eventId);
    List<Review> findByEventId(UUID eventId);
    List<Review> findByUserId(UUID userId);
}