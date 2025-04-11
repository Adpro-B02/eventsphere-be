package backend.eventsphere.review.repository;

import backend.eventsphere.review.model.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ReviewRepositoryImplTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    private ReviewRepositoryImpl reviewRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        reviewRepository = new ReviewRepositoryImpl(jdbcTemplate);
    }

    @Test
    public void testFindById() {
        // Arrange
        Review expectedReview = new Review(1L, 101L, 201L, 4, "Great event!");
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), eq(1L)))
                .thenReturn(expectedReview);

        // Act
        Optional<Review> result = reviewRepository.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(expectedReview, result.get());
        verify(jdbcTemplate).queryForObject(anyString(), any(RowMapper.class), eq(1L));
    }

    @Test
    public void testFindById_NotFound() {
        // Arrange
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), eq(999L)))
                .thenReturn(null);

        // Act
        Optional<Review> result = reviewRepository.findById(999L);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    public void testFindByEventId() {
        // Arrange
        Review review1 = new Review(1L, 101L, 201L, 4, "Great event!");
        Review review2 = new Review(2L, 101L, 202L, 5, "Excellent!");
        List<Review> expectedReviews = Arrays.asList(review1, review2);

        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(101L)))
                .thenReturn(expectedReviews);

        // Act
        List<Review> results = reviewRepository.findByEventId(101L);

        // Assert
        assertEquals(2, results.size());
        assertEquals(expectedReviews, results);
        verify(jdbcTemplate).query(anyString(), any(RowMapper.class), eq(101L));
    }

    @Test
    public void testFindByUserId() {
        // Arrange
        Review review1 = new Review(1L, 101L, 201L, 4, "Great event!");
        Review review2 = new Review(3L, 102L, 201L, 3, "Good event");
        List<Review> expectedReviews = Arrays.asList(review1, review2);

        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(201L)))
                .thenReturn(expectedReviews);

        // Act
        List<Review> results = reviewRepository.findByUserId(201L);

        // Assert
        assertEquals(2, results.size());
        assertEquals(expectedReviews, results);
        verify(jdbcTemplate).query(anyString(), any(RowMapper.class), eq(201L));
    }

    @Test
    public void testFindByUserIdAndEventId() {
        // Arrange
        Review expectedReview = new Review(1L, 101L, 201L, 4, "Great event!");
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), eq(201L), eq(101L)))
                .thenReturn(expectedReview);

        // Act
        Optional<Review> result = reviewRepository.findByUserIdAndEventId(201L, 101L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(expectedReview, result.get());
        verify(jdbcTemplate).queryForObject(anyString(), any(RowMapper.class), eq(201L), eq(101L));
    }

    @Test
    public void testSave_Insert() {
        // Arrange
        Review review = new Review(null, 101L, 201L, 4, "Great event!");
        when(jdbcTemplate.update(
                anyString(),
                eq(review.getEventId()),
                eq(review.getUserId()),
                eq(review.getRating()),
                eq(review.getComment())
        )).thenReturn(1);
        when(jdbcTemplate.queryForObject(eq("SELECT LAST_INSERT_ID()"), eq(Long.class)))
                .thenReturn(1L);

        // Act
        Review savedReview = reviewRepository.save(review);

        // Assert
        assertNotNull(savedReview.getId());
        assertEquals(1L, savedReview.getId());
        verify(jdbcTemplate).update(
                anyString(),
                eq(review.getEventId()),
                eq(review.getUserId()),
                eq(review.getRating()),
                eq(review.getComment())
        );
    }

    @Test
    public void testSave_Update() {
        // Arrange
        Review review = new Review(1L, 101L, 201L, 4, "Updated review");
        when(jdbcTemplate.update(
                anyString(),
                eq(review.getRating()),
                eq(review.getComment()),
                eq(review.getId())
        )).thenReturn(1);

        // Act
        Review updatedReview = reviewRepository.save(review);

        // Assert
        assertEquals(review, updatedReview);
        verify(jdbcTemplate).update(
                anyString(),
                eq(review.getRating()),
                eq(review.getComment()),
                eq(review.getId())
        );
    }

    @Test
    public void testDeleteById() {
        // Arrange
        when(jdbcTemplate.update(anyString(), eq(1L))).thenReturn(1);

        // Act
        boolean result = reviewRepository.deleteById(1L);

        // Assert
        assertTrue(result);
        verify(jdbcTemplate).update(anyString(), eq(1L));
    }

    @Test
    public void testDeleteById_NotFound() {
        // Arrange
        when(jdbcTemplate.update(anyString(), eq(999L))).thenReturn(0);

        // Act
        boolean result = reviewRepository.deleteById(999L);

        // Assert
        assertFalse(result);
        verify(jdbcTemplate).update(anyString(), eq(999L));
    }
}
