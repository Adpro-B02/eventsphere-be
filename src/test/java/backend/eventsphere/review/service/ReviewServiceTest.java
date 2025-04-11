package backend.eventsphere.review.service;

import backend.eventsphere.review.dto.ReviewCreateRequest;
import backend.eventsphere.review.dto.ReviewResponse;
import backend.eventsphere.review.dto.ReviewUpdateRequest;
import backend.eventsphere.review.exception.ReviewNotFoundException;
import backend.eventsphere.review.exception.ValidationException;
import backend.eventsphere.review.model.Review;
import backend.eventsphere.review.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private EventService eventService;  // Mock service for event validation

    @Mock
    private UserService userService;    // Mock service for user validation

    private ReviewServiceImpl reviewService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        reviewService = new ReviewServiceImpl(reviewRepository, eventService, userService);
        
        // Setup common mocks for event and user validation
        when(eventService.eventExists(anyLong())).thenReturn(true);
        when(eventService.userAttendedEvent(anyLong(), anyLong())).thenReturn(true);
        when(userService.userExists(anyLong())).thenReturn(true);
    }

    @Test
    public void testCreateReview() {
        // Arrange
        ReviewCreateRequest request = new ReviewCreateRequest(101L, 201L, 4, "Great event!");
        Review savedReview = new Review(1L, 101L, 201L, 4, "Great event!");
        
        when(reviewRepository.findByUserIdAndEventId(201L, 101L)).thenReturn(Optional.empty());
        when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);

        // Act
        ReviewResponse response = reviewService.createReview(request);

        // Assert
        assertNotNull(response);
        assertEquals(savedReview.getId(), response.getId());
        assertEquals(savedReview.getRating(), response.getRating());
        assertEquals(savedReview.getComment(), response.getComment());
        
        verify(reviewRepository).save(any(Review.class));
        verify(eventService).eventExists(101L);
        verify(eventService).userAttendedEvent(201L, 101L);
        verify(userService).userExists(201L);
    }

    @Test
    public void testCreateReview_AlreadyExists() {
        // Arrange
        ReviewCreateRequest request = new ReviewCreateRequest(101L, 201L, 4, "Great event!");
        Review existingReview = new Review(1L, 101L, 201L, 3, "Existing review");
        
        when(reviewRepository.findByUserIdAndEventId(201L, 101L)).thenReturn(Optional.of(existingReview));

        // Act & Assert
        assertThrows(ValidationException.class, () -> reviewService.createReview(request));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    public void testCreateReview_InvalidRating() {
        // Arrange
        ReviewCreateRequest request = new ReviewCreateRequest(101L, 201L, 6, "Invalid rating");

        // Act & Assert
        assertThrows(ValidationException.class, () -> reviewService.createReview(request));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    public void testCreateReview_EventDoesNotExist() {
        // Arrange
        ReviewCreateRequest request = new ReviewCreateRequest(999L, 201L, 4, "Great event!");
        when(eventService.eventExists(999L)).thenReturn(false);

        // Act & Assert
        assertThrows(ValidationException.class, () -> reviewService.createReview(request));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    public void testCreateReview_UserDidNotAttendEvent() {
        // Arrange
        ReviewCreateRequest request = new ReviewCreateRequest(101L, 201L, 4, "Great event!");
        when(eventService.userAttendedEvent(201L, 101L)).thenReturn(false);

        // Act & Assert
        assertThrows(ValidationException.class, () -> reviewService.createReview(request));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    public void testUpdateReview() {
        // Arrange
        long reviewId = 1L;
        ReviewUpdateRequest request = new ReviewUpdateRequest(5, "Updated comment!");
        Review existingReview = new Review(reviewId, 101L, 201L, 4, "Original comment");
        Review updatedReview = new Review(reviewId, 101L, 201L, 5, "Updated comment!");
        
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(existingReview));
        when(reviewRepository.save(any(Review.class))).thenReturn(updatedReview);

        // Act
        ReviewResponse response = reviewService.updateReview(reviewId, request);

        // Assert
        assertNotNull(response);
        assertEquals(updatedReview.getId(), response.getId());
        assertEquals(updatedReview.getRating(), response.getRating());
        assertEquals(updatedReview.getComment(), response.getComment());
        
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    public void testUpdateReview_NotFound() {
        // Arrange
        long reviewId = 999L;
        ReviewUpdateRequest request = new ReviewUpdateRequest(5, "Updated comment!");
        
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ReviewNotFoundException.class, () -> reviewService.updateReview(reviewId, request));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    public void testUpdateReview_InvalidRating() {
        // Arrange
        long reviewId = 1L;
        ReviewUpdateRequest request = new ReviewUpdateRequest(0, "Invalid rating");
        Review existingReview = new Review(reviewId, 101L, 201L, 4, "Original comment");
        
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(existingReview));

        // Act & Assert
        assertThrows(ValidationException.class, () -> reviewService.updateReview(reviewId, request));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    public void testDeleteReview() {
        // Arrange
        long reviewId = 1L;
        Review existingReview = new Review(reviewId, 101L, 201L, 4, "Great event!");
        
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(existingReview));
        when(reviewRepository.deleteById(reviewId)).thenReturn(true);

        // Act
        boolean result = reviewService.deleteReview(reviewId);

        // Assert
        assertTrue(result);
        verify(reviewRepository).deleteById(reviewId);
    }

    @Test
    public void testDeleteReview_NotFound() {
        // Arrange
        long reviewId = 999L;
        
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ReviewNotFoundException.class, () -> reviewService.deleteReview(reviewId));
        verify(reviewRepository, never()).deleteById(anyLong());
    }

    @Test
    public void testGetReviewById() {
        // Arrange
        long reviewId = 1L;
        Review review = new Review(reviewId, 101L, 201L, 4, "Great event!");
        
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        // Act
        ReviewResponse response = reviewService.getReviewById(reviewId);

        // Assert
        assertNotNull(response);
        assertEquals(review.getId(), response.getId());
        assertEquals(review.getRating(), response.getRating());
        assertEquals(review.getComment(), response.getComment());
    }

    @Test
    public void testGetReviewById_NotFound() {
        // Arrange
        long reviewId = 999L;
        
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ReviewNotFoundException.class, () -> reviewService.getReviewById(reviewId));
    }

    @Test
    public void testGetReviewsByEventId() {
        // Arrange
        long eventId = 101L;
        Review review1 = new Review(1L, eventId, 201L, 4, "Great event!");
        Review review2 = new Review(2L, eventId, 202L, 5, "Excellent!");
        List<Review> reviews = Arrays.asList(review1, review2);
        
        when(reviewRepository.findByEventId(eventId)).thenReturn(reviews);

        // Act
        List<ReviewResponse> responses = reviewService.getReviewsByEventId(eventId);

        // Assert
        assertEquals(2, responses.size());
        assertEquals(review1.getId(), responses.get(0).getId());
        assertEquals(review2.getId(), responses.get(1).getId());
    }

    @Test
    public void testGetReviewsByUserId() {
        // Arrange
        long userId = 201L;
        Review review1 = new Review(1L, 101L, userId, 4, "Great event!");
        Review review2 = new Review(3L, 102L, userId, 3, "Good event");
        List<Review> reviews = Arrays.asList(review1, review2);
        
        when(reviewRepository.findByUserId(userId)).thenReturn(reviews);

        // Act
        List<ReviewResponse> responses = reviewService.getReviewsByUserId(userId);

        // Assert
        assertEquals(2, responses.size());
        assertEquals(review1.getId(), responses.get(0).getId());
        assertEquals(review2.getId(), responses.get(1).getId());
    }

    @Test
    public void testGetReviewByUserIdAndEventId() {
        // Arrange
        long userId = 201L;
        long eventId = 101L;
        Review review = new Review(1L, eventId, userId, 4, "Great event!");
        
        when(reviewRepository.findByUserIdAndEventId(userId, eventId)).thenReturn(Optional.of(review));

        // Act
        Optional<ReviewResponse> response = reviewService.getReviewByUserIdAndEventId(userId, eventId);

        // Assert
        assertTrue(response.isPresent());
        assertEquals(review.getId(), response.get().getId());
    }

    @Test
    public void testGetReviewByUserIdAndEventId_NotFound() {
        // Arrange
        long userId = 999L;
        long eventId = 888L;
        
        when(reviewRepository.findByUserIdAndEventId(userId, eventId)).thenReturn(Optional.empty());

        // Act
        Optional<ReviewResponse> response = reviewService.getReviewByUserIdAndEventId(userId, eventId);

        // Assert
        assertFalse(response.isPresent());
    }
}