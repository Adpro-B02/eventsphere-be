package backend.eventsphere.review.controller;

import backend.eventsphere.review.dto.ReviewCreateRequest;
import backend.eventsphere.review.dto.ReviewResponse;
import backend.eventsphere.review.dto.ReviewUpdateRequest;
import backend.eventsphere.review.service.ReviewService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ReviewControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewController reviewController;

    private ObjectMapper objectMapper;
    private ReviewCreateRequest validCreateRequest;
    private ReviewUpdateRequest validUpdateRequest;
    private ReviewResponse validReviewResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reviewController).build();
        objectMapper = new ObjectMapper();

        // Setup test data
        validCreateRequest = new ReviewCreateRequest(1L, 1L, 5, "Excellent event!");
        validUpdateRequest = new ReviewUpdateRequest(4, "Good event, but could be better.");
        validReviewResponse = new ReviewResponse(1L, 1L, 1L, 5, "Excellent event!");
    }

    @Test
    void createReview_ValidRequest_ReturnsCreatedReview() throws Exception {
        // Arrange
        when(reviewService.createReview(any(ReviewCreateRequest.class)))
                .thenReturn(Optional.of(validReviewResponse));

        // Act & Assert
        mockMvc.perform(post("/api/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(validReviewResponse.getId()))
                .andExpect(jsonPath("$.eventId").value(validReviewResponse.getEventId()))
                .andExpect(jsonPath("$.userId").value(validReviewResponse.getUserId()))
                .andExpect(jsonPath("$.rating").value(validReviewResponse.getRating()))
                .andExpect(jsonPath("$.comment").value(validReviewResponse.getComment()));
    }

    @Test
    void createReview_InvalidRequest_ReturnsBadRequest() throws Exception {
        // Arrange
        when(reviewService.createReview(any(ReviewCreateRequest.class)))
                .thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(post("/api/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validCreateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Failed to create review"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void updateReview_ValidRequest_ReturnsUpdatedReview() throws Exception {
        // Arrange
        ReviewResponse updatedResponse = new ReviewResponse(
                1L, 1L, 1L, validUpdateRequest.getRating(), validUpdateRequest.getComment());
        
        when(reviewService.updateReview(anyLong(), any(ReviewUpdateRequest.class)))
                .thenReturn(Optional.of(updatedResponse));

        // Act & Assert
        mockMvc.perform(put("/api/reviews/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedResponse.getId()))
                .andExpect(jsonPath("$.rating").value(updatedResponse.getRating()))
                .andExpect(jsonPath("$.comment").value(updatedResponse.getComment()));
    }

    @Test
    void updateReview_ReviewNotFound_ReturnsNotFound() throws Exception {
        // Arrange
        when(reviewService.updateReview(anyLong(), any(ReviewUpdateRequest.class)))
                .thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/api/reviews/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validUpdateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Review not found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deleteReview_ReviewExists_ReturnsNoContent() throws Exception {
        // Arrange
        when(reviewService.deleteReview(anyLong())).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/api/reviews/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteReview_ReviewNotFound_ReturnsNotFound() throws Exception {
        // Arrange
        when(reviewService.deleteReview(anyLong())).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/api/reviews/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Review not found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void getReviewById_ReviewExists_ReturnsReview() throws Exception {
        // Arrange
        when(reviewService.getReviewById(anyLong())).thenReturn(Optional.of(validReviewResponse));

        // Act & Assert
        mockMvc.perform(get("/api/reviews/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(validReviewResponse.getId()))
                .andExpect(jsonPath("$.eventId").value(validReviewResponse.getEventId()))
                .andExpect(jsonPath("$.userId").value(validReviewResponse.getUserId()))
                .andExpect(jsonPath("$.rating").value(validReviewResponse.getRating()))
                .andExpect(jsonPath("$.comment").value(validReviewResponse.getComment()));
    }

    @Test
    void getReviewById_ReviewNotFound_ReturnsNotFound() throws Exception {
        // Arrange
        when(reviewService.getReviewById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/reviews/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getReviewsByEventId_ReturnsReviews() throws Exception {
        // Arrange
        List<ReviewResponse> reviews = Arrays.asList(
                new ReviewResponse(1L, 1L, 1L, 5, "Great event!"),
                new ReviewResponse(2L, 1L, 2L, 4, "Good event!")
        );
        
        when(reviewService.getReviewsByEventId(anyLong())).thenReturn(reviews);

        // Act & Assert
        mockMvc.perform(get("/api/reviews/event/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(reviews.get(0).getId()))
                .andExpect(jsonPath("$[1].id").value(reviews.get(1).getId()))
                .andExpect(jsonPath("$[0].comment").value(reviews.get(0).getComment()))
                .andExpect(jsonPath("$[1].comment").value(reviews.get(1).getComment()));
    }

    @Test
    void getReviewsByUserId_ReturnsReviews() throws Exception {
        // Arrange
        List<ReviewResponse> reviews = Arrays.asList(
                new ReviewResponse(1L, 1L, 1L, 5, "Great event!"),
                new ReviewResponse(2L, 2L, 1L, 4, "Good event!")
        );
        
        when(reviewService.getReviewsByUserId(anyLong())).thenReturn(reviews);

        // Act & Assert
        mockMvc.perform(get("/api/reviews/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(reviews.get(0).getId()))
                .andExpect(jsonPath("$[1].id").value(reviews.get(1).getId()))
                .andExpect(jsonPath("$[0].comment").value(reviews.get(0).getComment()))
                .andExpect(jsonPath("$[1].comment").value(reviews.get(1).getComment()));
    }

    @Test
    void getReviewByUserIdAndEventId_ReviewExists_ReturnsReview() throws Exception {
        // Arrange
        when(reviewService.getReviewByUserIdAndEventId(anyLong(), anyLong()))
                .thenReturn(Optional.of(validReviewResponse));

        // Act & Assert
        mockMvc.perform(get("/api/reviews/user/1/event/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(validReviewResponse.getId()))
                .andExpect(jsonPath("$.eventId").value(validReviewResponse.getEventId()))
                .andExpect(jsonPath("$.userId").value(validReviewResponse.getUserId()))
                .andExpect(jsonPath("$.rating").value(validReviewResponse.getRating()))
                .andExpect(jsonPath("$.comment").value(validReviewResponse.getComment()));
    }
}