package backend.eventsphere.review.controller;

import backend.eventsphere.review.service.ReviewService;
import backend.eventsphere.auth.model.User;
import backend.eventsphere.auth.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;
import java.util.UUID;

@Controller
public class ReviewWebController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewWebController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/reviews/{eventId}")
    public String getEventReviewsPage(@PathVariable UUID eventId, Model model) {
        model.addAttribute("eventId", eventId);
        model.addAttribute("reviews", reviewService.findByEventIdAsync(eventId).join());
        return "review/review";
    }
}