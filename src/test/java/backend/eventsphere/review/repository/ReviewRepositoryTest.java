package backend.eventsphere.review.controller;

import backend.eventsphere.review.dto.ReviewCreateRequest;
import backend.eventsphere.review.dto.ReviewResponse;
import backend.eventsphere.review.dto.ReviewUpdateRequest;
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
        when(reviewService.createReview(any(ReviewCreateRequest.class)))
            .thenReturn(Optional.of(sampleReviewResponse));

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
            .thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(post("/api/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());

        verify(reviewService).createReview(any(ReviewCreateRequest.class));
    }

    @Test
    public void testUpdateReview() throws Exception {
        // Arrange
        long reviewId = 1L;
        ReviewUpdateRequest request = new ReviewUpdateRequest(5, "Updated comment!");
        ReviewResponse updatedResponse = new ReviewResponse(reviewId, 101L, 201L, 5, "Updated comment!");
        
        when(reviewService.updateReview(eq(reviewId), any(ReviewUpdateRequest.class)))
            .thenReturn(Optional.of(updatedResponse));

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
            .thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/api/reviews/{id}", reviewId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());

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
        when(reviewService.deleteReview(reviewId)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/api/reviews/{id}", reviewId))
                .andExpect(status().isNotFound());

        verify(reviewService).deleteReview(reviewId);
    }

    @Test
    public void testGetReviewById() throws Exception {
        // Arrange
        long reviewId = 1L;
        when(reviewService.getReviewById(reviewId)).thenReturn(Optional.of(sampleReviewResponse));

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
        when(reviewService.getReviewById(reviewId)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/reviews/{id}", reviewId))
                .andExpect(status().isNotFound());

        verify(reviewService).getReviewById(reviewId);
    }

    @Test
    public void testGetReviewsByEventId() throws Exception {
        // Arrange
        long eventId = 101L