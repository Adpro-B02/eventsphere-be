package backend.eventsphere.review.strategy;

import backend.eventsphere.review.model.Review;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class AverageRatingStrategy implements RatingCalculationStrategy {
    @Override
    public double calculateRating(List<Review> reviews) {
        if (reviews.isEmpty()) {
            return 0.0;
        }
        return reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
    }
}