package com.app.review.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Request DTO for creating a new review.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCreateRequest {
    @NotNull(message = "Event ID cannot be null")
    private Long eventId;
    
    @NotNull(message = "User ID cannot be null")
    private Long userId;
    
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private int rating;
    
    private String comment;
}

/**
 * Request DTO for updating an existing review.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewUpdateRequest {
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private int rating;
    
    private String comment;
}

/**
 * Response DTO for sending review data to clients.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private Long id;
    private Long eventId;
    private Long userId;
    private int rating;
    private String comment;
}