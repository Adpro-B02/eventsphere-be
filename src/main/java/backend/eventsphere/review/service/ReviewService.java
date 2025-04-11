package backend.eventsphere.review.service;

import backend.eventsphere.review.dto.ReviewCreateRequest;
import backend.eventsphere.review.dto.ReviewResponse;
import backend.eventsphere.review.dto.ReviewUpdateRequest;
import backend.eventsphere.review.model.Review;
import backend.eventsphere.review.repository.ReviewRepository;
import backend.eventsphere.review.service.mock.EventService;
import backend.eventsphere.review.service.mock.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service interface for Review operations.
 */
public interface ReviewService {
    Optional<ReviewResponse> createReview(ReviewCreateRequest request);
    Optional<ReviewResponse> updateReview(Long id, ReviewUpdateRequest request);
    boolean deleteReview(Long id);
    Optional<ReviewResponse> getReviewById(Long id);
    List<ReviewResponse> getReviewsByEventId(Long eventId);
    List<ReviewResponse> getReviewsByUserId(Long userId);
    Optional<ReviewResponse> getReviewByUserIdAndEventId(Long userId, Long eventId);
}

/**
 * Implementation of the ReviewService interface.
 */
@Service
public class ReviewServiceImpl implements ReviewService {
    
    private final ReviewRepository reviewRepository;
    private final EventService eventService;
    private final UserService userService;
    
    // Singleton pattern using constructor injection
    @Autowired
    public ReviewServiceImpl(ReviewRepository reviewRepository, EventService eventService, UserService userService) {
        this.reviewRepository = reviewRepository;
        this.eventService = eventService;
        this.userService = userService;
    }
    
    /**
     * Converts Review model to ReviewResponse DTO.
     */
    private ReviewResponse mapToResponse(Review review) {
        return new ReviewResponse(
            review.getId(),
            review.getEventId(),
            review.getUserId(),
            review.getRating(),
            review.getComment()
        );
    }
    
    /**
     * Creates a new review.
     * Returns Optional.empty() if validation fails.
     */
    @Override
    @Transactional
    public Optional<ReviewResponse> createReview(ReviewCreateRequest request) {
        // Validate request
        if (!isCreateRequestValid(request)) {
            return Optional.empty();
        }
        
        // Check if review already exists for this user and event
        if (reviewRepository.findByUserIdAndEventId(request.getUserId(), request.getEventId()).isPresent()) {
            return Optional.empty();
        }
        
        // Create and save review
        try {
            Review review = new Review(
                null,
                request.getEventId(),
                request.getUserId(),
                request.getRating(),
                request.getComment()
            );
            
            Review savedReview = reviewRepository.save(review);
            return Optional.of(mapToResponse(savedReview));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
    
    /**
     * Updates an existing review.
     * Returns Optional.empty() if validation fails or review not found.
     */
    @Override
    @Transactional
    public Optional<ReviewResponse> updateReview(Long id, ReviewUpdateRequest request) {
        // Validate request
        if (request.getRating() < 1 || request.getRating() > 5) {
            return Optional.empty();
        }
        
        // Find review
        Optional<Review> reviewOpt = reviewRepository.findById(id);
        if (!reviewOpt.isPresent()) {
            return Optional.empty();
        }
        
        // Update fields
        Review review = reviewOpt.get();
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        
        // Save and return
        try {
            Review updatedReview = reviewRepository.save(review);
            return Optional.of(mapToResponse(updatedReview));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
    
    /**
     * Deletes a review.
     * Returns false if review not found.
     */
    @Override
    @Transactional
    public boolean deleteReview(Long id) {
        // Verify review exists
        if (!reviewRepository.findById(id).isPresent()) {
            return false;
        }
        
        return reviewRepository.deleteById(id);
    }
    
    /**
     * Gets a review by ID.
     * Returns Optional.empty() if not found.
     */
    @Override
    public Optional<ReviewResponse> getReviewById(Long id) {
        return reviewRepository.findById(id).map(this::mapToResponse);
    }
    
    /**
     * Gets all reviews for an event.
     */
    @Override
    public List<ReviewResponse> getReviewsByEventId(Long eventId) {
        return reviewRepository.findByEventId(eventId).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Gets all reviews by a user.
     */
    @Override
    public List<ReviewResponse> getReviewsByUserId(Long userId) {
        return reviewRepository.findByUserId(userId).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Gets a review by user ID and event ID.
     */
    @Override
    public Optional<ReviewResponse> getReviewByUserIdAndEventId(Long userId, Long eventId) {
        return reviewRepository.findByUserIdAndEventId(userId, eventId)
            .map(this::mapToResponse);
    }
    
    /**
     * Validates a review creation request.
     */
    private boolean isCreateRequestValid(ReviewCreateRequest request) {
        // Validate rating
        if (request.getRating() < 1 || request.getRating() > 5) {
            return false;
        }
        
        // Validate event exists
        if (!eventService.eventExists(request.getEventId())) {
            return false;
        }
        
        // Validate user exists
        if (!userService.userExists(request.getUserId())) {
            return false;
        }
        
        // Validate user attended the event
        if (!eventService.userAttendedEvent(request.getUserId(), request.getEventId())) {
            return false;
        }
        
        return true;
    }
}