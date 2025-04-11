package backend.eventsphere.review.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    private Long id;
    private Long eventId;
    private Long userId;
    private int rating;
    private String comment;
    
    /**
     * Validates the review data.
     * @throws IllegalArgumentException if the rating is not between 1 and 5
     */
    public void validate() {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
    }
    
    // Custom constructor with validation
    public Review(Long id, Long eventId, Long userId, int rating, String comment) {
        this.id = id;
        this.eventId = eventId;
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
        validate();
    }
}