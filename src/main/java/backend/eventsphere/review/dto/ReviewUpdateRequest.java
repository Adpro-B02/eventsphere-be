package backend.eventsphere.review.dto;

public class ReviewUpdateRequest {
    private Integer rating;
    private String comment;

    // Default constructor
    public ReviewUpdateRequest() {
    }

    // All-args constructor
    public ReviewUpdateRequest(Integer rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }

    // Getters and setters
    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}