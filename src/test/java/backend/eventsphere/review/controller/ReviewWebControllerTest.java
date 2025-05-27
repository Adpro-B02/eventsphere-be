package backend.eventsphere.review.controller;

import backend.eventsphere.review.model.Review;
import backend.eventsphere.review.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ReviewWebControllerTest {

    private ReviewWebController reviewWebController;

    @Mock
    private ReviewService reviewService;

    @Mock
    private Model model;

    private UUID eventId;
    private List<Review> sampleReviews;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        reviewWebController = new ReviewWebController(reviewService);
        eventId = UUID.randomUUID();
        sampleReviews = List.of(new Review(), new Review()); // Replace with actual Review objects if needed
    }

    @Test
    void whenGetEventReviewsPage_thenSuccess() throws Exception {
        when(reviewService.findByEventIdAsync(eventId))
            .thenReturn(CompletableFuture.completedFuture(sampleReviews));

        CompletableFuture<String> result = reviewWebController.getEventReviewsPage(eventId, model);

        assertEquals("review/review", result.get());
        verify(model).addAttribute("eventId", eventId);
        verify(model).addAttribute("reviews", sampleReviews);
    }
}