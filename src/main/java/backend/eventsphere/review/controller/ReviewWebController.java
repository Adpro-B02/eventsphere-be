package backend.eventsphere.review.controller;

import backend.eventsphere.review.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Controller
public class ReviewWebController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewWebController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/reviews/{eventId}")
    public CompletableFuture<String> getEventReviewsPage(@PathVariable UUID eventId, Model model) {
        return reviewService.findByEventIdAsync(eventId)
            .thenApply(reviews -> {
                model.addAttribute("eventId", eventId);
                model.addAttribute("reviews", reviews);
                return "review/review";
            });
    }
}