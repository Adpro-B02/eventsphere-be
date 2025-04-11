package backend.eventsphere.review.dto;

public class ReviewResponse {
    private Long id;
    private Long eventId;
    private Long userId;
    private Integer rating;
    private String comment;

    // Default constructor
    public ReviewResponse() {
    }

    // All-args constructor
    public ReviewResponse(Long id, Long eventId, Long userId, Integer rating, String comment) {
        this.id = id;
        this.eventId = eventId;
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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