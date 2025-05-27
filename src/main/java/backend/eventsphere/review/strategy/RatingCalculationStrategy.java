package backend.eventsphere.review.strategy;

import backend.eventsphere.review.model.Review;
import java.util.List;

public interface RatingCalculationStrategy {
    double calculateRating(List<Review> reviews);
}