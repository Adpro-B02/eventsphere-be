package backend.eventsphere.review.service;

import backend.eventsphere.review.dto.ReviewCreateRequest;
import backend.eventsphere.review.dto.ReviewResponse;
import backend.eventsphere.review.dto.ReviewUpdateRequest;
import backend.eventsphere.review.model.Review;
import backend.eventsphere.review.repository.ReviewRepository;
import backend.eventsphere.review.service.mock.EventServiceMock;
import backend.eventsphere.review.service.mock.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private EventServiceMock eventService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ReviewService reviewService;

    private ReviewCreateRequest validCreateRequest;
    private ReviewUpdateRequest validUpdateRequest;
    private Review validReview;

    @BeforeEach
    void setUp() {
        // Setup valid review create request
        validCreateRequest = new ReviewCreateRequest(1L, 1L, 5, "Excellent event!");

        // Setup valid review update request
        validUpdateRequest = new ReviewUpdateRequest(4, "Good event, but could be better.");

        // Setup valid review
        validReview = new Review(1L, 1L, 1L, 5, "Excellent event!");
    }

    @Test
    void createReview_ValidRequest_ReturnsReviewResponse() {
        // Arrange
        when(eventService.eventExists(anyLong())).thenReturn(true);
        when(userService.userExists(anyLong())).thenReturn(true);
        when(eventService.userAttendedEvent(anyLong(), anyLong())).thenReturn(true);
        when(reviewRepository.findByUserIdAndEventId(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(reviewRepository.save(any(Review.class))).thenReturn(validReview);

        // Act
        Optional<ReviewResponse> result = reviewService.createReview(validCreateRequest);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(validReview.getId(), result.get().getId());
        assertEquals(validReview.getEventId(), result.get().getEventId());
        assertEquals(validReview.getUserId(), result.get().getUserId());
        assertEquals(validReview.getRating(), result.get().getRating());
        assertEquals(validReview.getComment(), result.get().getComment());

        verify(eventService).eventExists(validCreateRequest.getEventId());
        verify(userService).userExists(validCreateRequest.getUserId());
        verify(eventService).userAttendedEvent(validCreateRequest.getUserId(), validCreateRequest.getEventId());
        verify(reviewRepository).findByUserIdAndEventId(validCreateRequest.getUserId(), validCreateRequest.getEventId());
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void createReview_EventDoesNotExist_ReturnsEmpty() {
        // Arrange
        when(eventService.eventExists(anyLong())).thenReturn(false);

        // Act
        Optional<ReviewResponse> result = reviewService.createReview(validCreateRequest);

        // Assert
        assertTrue(result.isEmpty());
        verify(eventService).eventExists(validCreateRequest.getEventId());
        verify(userService, never()).userExists(anyLong());
        verify(eventService, never()).userAttendedEvent(anyLong(), anyLong());
        verify(reviewRepository, never()).findByUserIdAndEventId(anyLong(), anyLong());
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void createReview_UserDoesNotExist_ReturnsEmpty() {
        // Arrange
        when(eventService.eventExists(anyLong())).thenReturn(true);
        when(userService.userExists(anyLong())).thenReturn(false);

        // Act
        Optional<ReviewResponse> result = reviewService.createReview(validCreateRequest);

        // Assert
        assertTrue(result.isEmpty());
        verify(eventService).eventExists(validCreateRequest.getEventId());
        verify(userService).userExists(validCreateRequest.getUserId());
        verify(eventService, never()).userAttendedEvent(anyLong(), anyLong());
        verify(reviewRepository, never()).findByUserIdAndEventId(anyLong(), anyLong());
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void createReview_UserDidNotAttendEvent_ReturnsEmpty() {
        // Arrange
        when(eventService.eventExists(anyLong())).thenReturn(true);
        when(userService.userExists(anyLong())).thenReturn(true);
        when(eventService.userAttendedEvent(anyLong(), anyLong())).thenReturn(false);

        // Act
        Optional<ReviewResponse> result = reviewService.createReview(validCreateRequest);

        // Assert
        assertTrue(result.isEmpty());
        verify(eventService).eventExists(validCreateRequest.getEventId());
        verify(userService).userExists(validCreateRequest.getUserId());
        verify(eventService).userAttendedEvent(validCreateRequest.getUserId(), validCreateRequest.getEventId());
        verify(reviewRepository, never()).findByUserIdAndEventId(anyLong(), anyLong());
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void createReview_ReviewAlreadyExists_ReturnsEmpty() {
        // Arrange
        when(eventService.eventExists(anyLong())).thenReturn(true);
        when(userService.userExists(anyLong())).thenReturn(true);
        when(eventService.userAttendedEvent(anyLong(), anyLong())).thenReturn(true);
        when(reviewRepository.findByUserIdAndEventId(anyLong(), anyLong())).thenReturn(Optional.of(validReview));

        // Act
        Optional<ReviewResponse> result = reviewService.createReview(validCreateRequest);

        // Assert
        assertTrue(result.isEmpty());
        verify(eventService).eventExists(validCreateRequest.getEventId());
        verify(userService).userExists(validCreateRequest.getUserId());
        verify(eventService).userAttendedEvent(validCreateRequest.getUserId(), validCreateRequest.getEventId());
        verify(reviewRepository).findByUserIdAndEventId(validCreateRequest.getUserId(), validCreateRequest.getEventId());
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void createReview_InvalidRating_ReturnsEmpty() {
        // Arrange
        ReviewCreateRequest invalidRequest = new ReviewCreateRequest(1L, 1L, 6, "Rating too high");

        // Act
        Optional<ReviewResponse> result = reviewService.createReview(invalidRequest);

        // Assert
        assertTrue(result.isEmpty());
        verify(eventService, never()).eventExists(anyLong());
        verify(userService, never()).userExists(anyLong());
        verify(eventService, never()).userAttendedEvent(anyLong(), anyLong());
        verify(reviewRepository, never()).findByUserIdAndEventId(anyLong(), anyLong());
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void updateReview_ValidRequest_ReturnsUpdatedReview() {
        // Arrange
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.of(validReview));
        when(reviewRepository.save(any(Review.class))).thenReturn(
            new Review(1L, 1L, 1L, 
                       validUpdateRequest.getRating(), 
                       validUpdateRequest.getComment()));

        // Act
        Optional<ReviewResponse> result = reviewService.updateReview(1L, validUpdateRequest);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(validReview.getId(), result.get().getId());
        assertEquals(validReview.getEventId(), result.get().getEventId());
        assertEquals(validReview.getUserId(), result.get().getUserId());
        assertEquals(validUpdateRequest.getRating(), result.get().getRating());
        assertEquals(validUpdateRequest.getComment(), result.get().getComment());

        verify(reviewRepository).findById(1L);
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void updateReview_ReviewNotFound_ReturnsEmpty() {
        // Arrange
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        Optional<ReviewResponse> result = reviewService.updateReview(1L, validUpdateRequest);

        // Assert
        assertTrue(result.isEmpty());
        verify(reviewRepository).findById(1L);
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void deleteReview_ReviewExists_ReturnsTrue() {
        // Arrange
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.of(validReview));
        doNothing().when(reviewRepository).deleteById(anyLong());

        // Act
        boolean result = reviewService.deleteReview(1L);

        // Assert
        assertTrue(result);
        verify(reviewRepository).findById(1L);
        verify(reviewRepository).deleteById(1L);
    }

    @Test
    void deleteReview_ReviewNotFound_ReturnsFalse() {
        // Arrange
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        boolean result = reviewService.deleteReview(1L);

        // Assert
        assertFalse(result);
        verify(reviewRepository).findById(1L);
        verify(reviewRepository, never()).deleteById(anyLong());
    }

    @Test
    void getReviewById_ReviewExists_ReturnsReview() {
        // Arrange
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.of(validReview));

        // Act
        Optional<ReviewResponse> result = reviewService.getReviewById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(validReview.getId(), result.get().getId());
        verify(reviewRepository).findById(1L);
    }

    @Test
    void getReviewById_ReviewNotFound_ReturnsEmpty() {
        // Arrange
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        Optional<ReviewResponse> result = reviewService.getReviewById(1L);

        // Assert
        assertTrue(result.isEmpty());
        verify(reviewRepository).findById(1L);
    }

    @Test
    void getReviewsByEventId_EventExists_ReturnsReviews() {
        // Arrange
        List<Review> reviews = Arrays.asList(
            new Review(1L, 1L, 1L, 5, "Great event!"),
            new Review(2L, 1L, 2L, 4, "Good event!")
        );
        
        when(eventService.eventExists(anyLong())).thenReturn(true);
        when(reviewRepository.findByEventId(anyLong())).thenReturn(reviews);

        // Act
        List<ReviewResponse> result = reviewService.getReviewsByEventId(1L);

        // Assert
        assertEquals(2, result.size());
        verify(eventService).eventExists(1L);
        verify(reviewRepository).findByEventId(1L);
    }

    @Test
    void getReviewsByEventId_EventDoesNotExist_ReturnsEmptyList() {
        // Arrange
        when(eventService.eventExists(anyLong())).thenReturn(false);

        // Act
        List<ReviewResponse> result = reviewService.getReviewsByEventId(1L);

        // Assert
        assertTrue(result.isEmpty());
        verify(eventService).eventExists(1L);
        verify(reviewRepository, never()).findByEventId(anyLong());
    }

    @Test
    void getReviewsByUserId_UserExists_ReturnsReviews() {
        // Arrange
        List<Review> reviews = Arrays.asList(
            new Review(1L, 1L, 1L, 5, "Great event!"),
            new Review(2L, 2L, 1L, 4, "Good event!")
        );
        
        when(userService.userExists(anyLong())).thenReturn(true);
        when(reviewRepository.findByUserId(anyLong())).thenReturn(reviews);

        // Act
        List<ReviewResponse> result = reviewService.getReviewsByUserId(1L);

        // Assert
        assertEquals(2, result.size());
        verify(userService).userExists(1L);
        verify(reviewRepository).findByUserId(1L);
    }

    @Test
    void getReviewsByUserId_UserDoesNotExist_ReturnsEmptyList() {
        // Arrange
        when(userService.userExists(anyLong())).thenReturn(false);

        // Act
        List<ReviewResponse> result = reviewService.getReviewsByUserId(1L);

        // Assert
        assertTrue(result.isEmpty());
        verify(userService).userExists(1L);
        verify(reviewRepository, never()).findByUserId(anyLong());
    }

    @Test
    void getReviewByUserIdAndEventId_ReviewExists_ReturnsReview() {
        // Arrange
        when(reviewRepository.findByUserIdAndEventId(anyLong(), anyLong())).thenReturn(Optional.of(validReview));

        // Act
        Optional<ReviewResponse> result = reviewService.getReviewByUserIdAndEventId(1L, 1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(validReview.getId(), result.get().getId());
        verify(reviewRepository).findByUserIdAndEventId(1L, 1L);
    }

    @Test
    void getReviewByUserIdAndEventId_ReviewNotFound_ReturnsEmpty() {
        // Arrange
        when(reviewRepository.findByUserIdAndEventId(anyLong(), anyLong())).thenReturn(Optional.empty());

        // Act
        Optional<ReviewResponse> result = reviewService.getReviewByUserIdAndEventId(1L, 1L);

        // Assert
        assertTrue(result.isEmpty());
        verify(reviewRepository).findByUserIdAndEventId(1L, 1L);
    }
}