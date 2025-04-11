package backend.eventsphere.review.dto;

import org.junit.jupiter.api.Test;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ReviewDtoTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void testReviewCreateRequestValidation() {
        // Valid request
        ReviewCreateRequest validRequest = new ReviewCreateRequest(101L, 201L, 4, "Great event!");
        Set<ConstraintViolation<ReviewCreateRequest>> validViolations = validator.validate(validRequest);
        assertTrue(validViolations.isEmpty());
        
        // Invalid rating (too high)
        ReviewCreateRequest tooHighRating = new ReviewCreateRequest(101L, 201L, 6, "Invalid rating");
        Set<ConstraintViolation<ReviewCreateRequest>> highRatingViolations = validator.validate(tooHighRating);
        assertFalse(highRatingViolations.isEmpty());
        
        // Invalid rating (too low)
        ReviewCreateRequest tooLowRating = new ReviewCreateRequest(101L, 201L, 0, "Invalid rating");
        Set<ConstraintViolation<ReviewCreateRequest>> lowRatingViolations = validator.validate(tooLowRating);
        assertFalse(lowRatingViolations.isEmpty());
        
        // Missing eventId
        ReviewCreateRequest nullEventId = new ReviewCreateRequest(null, 201L, 4, "Missing event");
        Set<ConstraintViolation<ReviewCreateRequest>> eventIdViolations = validator.validate(nullEventId);
        assertFalse(eventIdViolations.isEmpty());
        
        // Missing userId
        ReviewCreateRequest nullUserId = new ReviewCreateRequest(101L, null, 4, "Missing user");
        Set<ConstraintViolation<ReviewCreateRequest>> userIdViolations = validator.validate(nullUserId);
        assertFalse(userIdViolations.isEmpty());
    }
    
    @Test
    public void testReviewUpdateRequestValidation() {
        // Valid request
        ReviewUpdateRequest validRequest = new ReviewUpdateRequest(4, "Updated comment");
        Set<ConstraintViolation<ReviewUpdateRequest>> validViolations = validator.validate(validRequest);
        assertTrue(validViolations.isEmpty());
        
        // Invalid rating (too high)
        ReviewUpdateRequest tooHighRating = new ReviewUpdateRequest(6, "Invalid rating");
        Set<ConstraintViolation<ReviewUpdateRequest>> highRatingViolations = validator.validate(tooHighRating);
        assertFalse(highRatingViolations.isEmpty());
        
        // Invalid rating (too low)
        ReviewUpdateRequest tooLowRating = new ReviewUpdateRequest(0, "Invalid rating");
        Set<ConstraintViolation<ReviewUpdateRequest>> lowRatingViolations = validator.validate(tooLowRating);
        assertFalse(lowRatingViolations.isEmpty());
    }
    
    @Test
    public void testReviewResponseEquality() {
        // Creating two identical responses
        ReviewResponse response1 = new ReviewResponse(1L, 101L, 201L, 4, "Great event!");
        ReviewResponse response2 = new ReviewResponse(1L, 101L, 201L, 4, "Great event!");
        
        // Creating different response
        ReviewResponse response3 = new ReviewResponse(2L, 101L, 201L, 4, "Great event!");
        
        // Test equals and hashCode
        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1, response3);
        assertNotEquals(response1.hashCode(), response3.hashCode());
    }
    
    @Test
    public void testReviewCreateRequestConstructor() {
        // Testing constructor and getters
        Long eventId = 101L;
        Long userId = 201L;
        int rating = 4;
        String comment = "Great event!";
        
        ReviewCreateRequest request = new ReviewCreateRequest(eventId, userId, rating, comment);
        
        assertEquals(eventId, request.getEventId());
        assertEquals(userId, request.getUserId());
        assertEquals(rating, request.getRating());
        assertEquals(comment, request.getComment());
    }
    
    @Test
    public void testReviewUpdateRequestConstructor() {
        // Testing constructor and getters
        int rating = 5;
        String comment = "Updated comment";
        
        ReviewUpdateRequest request = new ReviewUpdateRequest(rating, comment);
        
        assertEquals(rating, request.getRating());
        assertEquals(comment, request.getComment());
    }
    
    @Test
    public void testReviewResponseConstructor() {
        // Testing constructor and getters
        Long id = 1L;
        Long eventId = 101L;
        Long userId = 201L;
        int rating = 4;
        String comment = "Great event!";
        
        ReviewResponse response = new ReviewResponse(id, eventId, userId, rating, comment);
        
        assertEquals(id, response.getId());
        assertEquals(eventId, response.getEventId());
        assertEquals(userId, response.getUserId());
        assertEquals(rating, response.getRating());
        assertEquals(comment, response.getComment());
    }
    
    @Test
    public void testReviewResponseToString() {
        ReviewResponse response = new ReviewResponse(1L, 101L, 201L, 4, "Great event!");
        String toString = response.toString();
        
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("eventId=101"));
        assertTrue(toString.contains("userId=201"));
        assertTrue(toString.contains("rating=4"));
        assertTrue(toString.contains("comment=Great event!"));
    }
}