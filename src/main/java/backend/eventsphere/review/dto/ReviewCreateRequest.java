package backend.eventsphere.review.dto;

public class ReviewCreateRequest {
    private Long userId;
    private Long eventId;
    private Integer rating;
    private String comment;

    // Default constructor
    public ReviewCreateRequest() {
    }

    // All-args constructor
    public ReviewCreateRequest(Long userId, Long eventId, Integer rating, String comment) {
        this.userId = userId;
        this.eventId = eventId;
        this.rating = rating;
        this.comment = comment;
    }

    // Getters and setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

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
