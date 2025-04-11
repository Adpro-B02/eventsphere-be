package backend.eventsphere.review.service;

import backend.eventsphere.review.dto.ReviewCreateRequest;
import backend.eventsphere.review.dto.ReviewResponse;
import backend.eventsphere.review.dto.ReviewUpdateRequest;
import backend.eventsphere.review.model.Review;
import backend.eventsphere.review.repository.ReviewRepository;
import backend.eventsphere.review.service.mock.EventService;
import backend.eventsphere.review.service.mock.UserService;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final EventService eventService;
    private final UserService userService;

    public ReviewService(
        ReviewRepository reviewRepository, 
        @Qualifier("mockEventService") EventService eventService, 
        @Qualifier("mockUserService") UserService userService) {
        this.reviewRepository = reviewRepository;
        this.eventService = eventService;
        this.userService = userService;
    }

    private ReviewResponse mapToResponse(Review review) {
        return new ReviewResponse(
            review.getId(),
            review.getEventId(),
            review.getUserId(),
            review.getRating(),
            review.getComment()
        );
    }

    @Transactional
    public Optional<ReviewResponse> createReview(ReviewCreateRequest request) {
        // Check if request is valid
        if (!isCreateRequestValid(request)) {
            return Optional.empty();
        }

        // Check if event exists
        if (!eventService.eventExists(request.getEventId())) {
            return Optional.empty();
        }

        // Check if user exists
        if (!userService.userExists(request.getUserId())) {
            return Optional.empty();
        }

        // Check if user attended the event
        if (!eventService.userAttendedEvent(request.getUserId(), request.getEventId())) {
            return Optional.empty();
        }

        // Check if review already exists for this user and event
        if (reviewRepository.findByUserIdAndEventId(request.getUserId(), request.getEventId()).isPresent()) {
            return Optional.empty();
        }

        // Create new review
        Review review = new Review(
            null,
            request.getEventId(),
            request.getUserId(),
            request.getRating(),
            request.getComment()
        );

        // Save review
        Review savedReview = reviewRepository.save(review);
        return Optional.of(mapToResponse(savedReview));
    }

    @Transactional
    public Optional<ReviewResponse> updateReview(Long id, ReviewUpdateRequest request) {
        // Get review
        Optional<Review> optionalReview = reviewRepository.findById(id);
        if (optionalReview.isEmpty()) {
            return Optional.empty();
        }

        Review review = optionalReview.get();

        // Update review
        if (request.getRating() != null) {
            review.setRating(request.getRating());
        }

        if (request.getComment() != null) {
            review.setComment(request.getComment());
        }

        // Save review
        Review updatedReview = reviewRepository.save(review);
        return Optional.of(mapToResponse(updatedReview));
    }

    @Transactional
    public boolean deleteReview(Long id) {
        // Check if review exists
        if (reviewRepository.findById(id).isEmpty()) {
            return false;
        }

        // Delete review
        reviewRepository.deleteById(id);
        return true;
    }

    public Optional<ReviewResponse> getReviewById(Long id) {
        return reviewRepository.findById(id)
            .map(this::mapToResponse);
    }

    public List<ReviewResponse> getReviewsByEventId(Long eventId) {
        // Check if event exists
        if (!eventService.eventExists(eventId)) {
            return List.of();
        }

        return reviewRepository.findByEventId(eventId).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    public List<ReviewResponse> getReviewsByUserId(Long userId) {
        // Check if user exists
        if (!userService.userExists(userId)) {
            return List.of();
        }

        return reviewRepository.findByUserId(userId).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    public Optional<ReviewResponse> getReviewByUserIdAndEventId(Long userId, Long eventId) {
        return reviewRepository.findByUserIdAndEventId(userId, eventId)
            .map(this::mapToResponse);
    }

    private boolean isCreateRequestValid(ReviewCreateRequest request) {
        return request.getUserId() != null && request.getEventId() != null &&
               request.getRating() != null && request.getRating() >= 1 && request.getRating() <= 5;
    }
}