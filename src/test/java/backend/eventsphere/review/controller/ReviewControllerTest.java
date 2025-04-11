package backend.eventsphere.review.controller;

import backend.eventsphere.review.dto.ReviewCreateRequest;
import backend.eventsphere.review.dto.ReviewResponse;
import backend.eventsphere.review.dto.ReviewUpdateRequest;
import backend.eventsphere.review.exception.ReviewNotFoundException;
import backend.eventsphere.review.exception.ValidationException;
import backend.eventsphere.review.service.ReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
public class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @Autowired
    private ObjectMapper objectMapper;

    private ReviewResponse sampleReviewResponse;
    private List<ReviewResponse> sampleReviewResponses;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        
        // Sample data for tests
        sampleReviewResponse = new ReviewResponse(1L, 101L, 201L, 4, "Great event!");
        ReviewResponse anotherReviewResponse = new ReviewResponse(2L, 101L, 202L, 5, "Excellent event!");
        sampleReviewResponses = Arrays.asList(sampleReviewResponse, anotherReviewResponse);
    }

    @Test
    public void testCreateReview() throws Exception {
        // Arrange
        ReviewCreateRequest request = new ReviewCreateRequest(101L, 201L, 4, "Great event!");
        when(reviewService.createReview(any(ReviewCreateRequest.class))).thenReturn(sampleReviewResponse);

        // Act & Assert
        mockMvc.perform(post("/api/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(sampleReviewResponse.getId()))
                .andExpect(jsonPath("$.eventId").value(sampleReviewResponse.getEventId()))
                .andExpect(jsonPath("$.userId").value(sampleReviewResponse.getUserId()))
                .andExpect(jsonPath("$.rating").value(sampleReviewResponse.getRating()))
                .andExpect(jsonPath("$.comment").value(sampleReviewResponse.getComment()));

        verify(reviewService).createReview(any(ReviewCreateRequest.class));
    }

    @Test
    public void testCreateReview_ValidationError() throws Exception {
        // Arrange
        ReviewCreateRequest request = new ReviewCreateRequest(101L, 201L, 6, "Invalid rating");
        when(reviewService.createReview(any(ReviewCreateRequest.class)))
                .thenThrow(new ValidationException("Rating must be between 1 and 5"));

        // Act & Assert
        mockMvc.perform(post("/api/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Rating must be between 1 and 5"));

        verify(reviewService).createReview(any(ReviewCreateRequest.class));
    }

    @Test
    public void testUpdateReview() throws Exception {
        // Arrange
        long reviewId = 1L;
        ReviewUpdateRequest request = new ReviewUpdateRequest(5, "Updated comment!");
        ReviewResponse updatedResponse = new ReviewResponse(reviewId, 101L, 201L, 5, "Updated comment!");
        
        when(reviewService.updateReview(eq(reviewId), any(ReviewUpdateRequest.class))).thenReturn(updatedResponse);

        // Act & Assert
        mockMvc.perform(put("/api/reviews/{id}", reviewId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedResponse.getId()))
                .andExpect(jsonPath("$.rating").value(updatedResponse.getRating()))
                .andExpect(jsonPath("$.comment").value(updatedResponse.getComment()));

        verify(reviewService).updateReview(eq(reviewId), any(ReviewUpdateRequest.class));
    }

    @Test
    public void testUpdateReview_NotFound() throws Exception {
        // Arrange
        long reviewId = 999L;
        ReviewUpdateRequest request = new ReviewUpdateRequest(5, "Updated comment!");
        
        when(reviewService.updateReview(eq(reviewId), any(ReviewUpdateRequest.class)))
                .thenThrow(new ReviewNotFoundException("Review not found with id: " + reviewId));

        // Act & Assert
        mockMvc.perform(put("/api/reviews/{id}", reviewId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Review not found with id: " + reviewId));

        verify(reviewService).updateReview(eq(reviewId), any(ReviewUpdateRequest.class));
    }

    @Test
    public void testDeleteReview() throws Exception {
        // Arrange
        long reviewId = 1L;
        when(reviewService.deleteReview(reviewId)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/api/reviews/{id}", reviewId))
                .andExpect(status().isNoContent());

        verify(reviewService).deleteReview(reviewId);
    }

    @Test
    public void testDeleteReview_NotFound() throws Exception {
        // Arrange
        long reviewId = 999L;
        doThrow(new ReviewNotFoundException("Review not found with id: " + reviewId))
                .when(reviewService).deleteReview(reviewId);

        // Act & Assert
        mockMvc.perform(delete("/api/reviews/{id}", reviewId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Review not found with id: " + reviewId));

        verify(reviewService).deleteReview(reviewId);
    }

    @Test
    public void testGetReviewById() throws Exception {
        // Arrange
        long reviewId = 1L;
        when(reviewService.getReviewById(reviewId)).thenReturn(sampleReviewResponse);

        // Act & Assert
        mockMvc.perform(get("/api/reviews/{id}", reviewId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleReviewResponse.getId()))
                .andExpect(jsonPath("$.eventId").value(sampleReviewResponse.getEventId()))
                .andExpect(jsonPath("$.userId").value(sampleReviewResponse.getUserId()))
                .andExpect(jsonPath("$.rating").value(sampleReviewResponse.getRating()))
                .andExpect(jsonPath("$.comment").value(sampleReviewResponse.getComment()));

        verify(reviewService).getReviewById(reviewId);
    }

    @Test
    public void testGetReviewById_NotFound() throws Exception {
        // Arrange
        long reviewId = 999L;
        when(reviewService.getReviewById(reviewId))
                .thenThrow(new ReviewNotFoundException("Review not found with id: " + reviewId));

        // Act & Assert
        mockMvc.perform(get("/api/reviews/{id}", reviewId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Review not found with id: " + reviewId));

        verify(reviewService).getReviewById(reviewId);
    }

    @Test
    public void testGetReviewsByEventId() throws Exception {
        // Arrange
        long eventId = 101L;
        when(reviewService.getReviewsByEventId(eventId)).thenReturn(sampleReviewResponses);

        // Act & Assert
        mockMvc.perform(get("/api/reviews/event/{eventId}", eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(sampleReviewResponses.get(0).getId()))
                .andExpect(jsonPath("$[1].id").value(sampleReviewResponses.get(1).getId()));

        verify(reviewService).getReviewsByEventId(eventId);
    }

    @Test
    public void testGetReviewsByUserId() throws Exception {
        // Arrange
        long userId = 201L;
        List<ReviewResponse> userReviews = Arrays.asList(sampleReviewResponse);
        when(reviewService.getReviewsByUserId(userId)).thenReturn(userReviews);

        // Act & Assert
        mockMvc.perform(get("/api/reviews/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(sampleReviewResponse.getId()))
                .andExpect(jsonPath("$[0].userId").value(userId));

        verify(reviewService).getReviewsByUserId(userId);
    }

    @Test
    public void testGetReviewByUserIdAndEventId() throws Exception {
        // Arrange
        long userId = 201L;
        long eventId = 101L;
        when(reviewService.getReviewByUserIdAndEventId(userId, eventId))
                .thenReturn(Optional.of(sampleReviewResponse));

        // Act & Assert
        mockMvc.perform(get("/api/reviews/user/{userId}/event/{eventId}", userId, eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleReviewResponse.getId()))
                .andExpect(jsonPath("$.eventId").value(eventId))
                .andExpect(jsonPath("$.userId").value(userId));

        verify(reviewService).getReviewByUserIdAndEventId(userId, eventId);
    }

    @Test
    public void testGetReviewByUserIdAndEventId_NotFound() throws Exception {
        // Arrange
        long userId = 999L;
        long eventId = 888L;
        when(reviewService.getReviewByUserIdAndEventId(userId, eventId))
                .thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/reviews/user/{userId}/event/{eventId}", userId, eventId))
                .andExpect(status().isNotFound());

        verify(reviewService).getReviewByUserIdAndEventId(userId, eventId);
    }

    @Test
    public void testHandleValidationException() throws Exception {
        // Arrange
        ReviewCreateRequest request = new ReviewCreateRequest(null, 201L, 4, "Missing event ID");
        when(reviewService.createReview(any(ReviewCreateRequest.class)))
                .thenThrow(new ValidationException("Event ID cannot be null"));

        // Act & Assert
        mockMvc.perform(post("/api/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Event ID cannot be null"));
    }

    @Test
    public void testHandleReviewNotFoundException() throws Exception {
        // Arrange
        long reviewId = 999L;
        when(reviewService.getReviewById(reviewId))
                .thenThrow(new ReviewNotFoundException("Review not found with id: " + reviewId));

        // Act & Assert
        mockMvc.perform(get("/api/reviews/{id}", reviewId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Review not found with id: " + reviewId));
    }
}