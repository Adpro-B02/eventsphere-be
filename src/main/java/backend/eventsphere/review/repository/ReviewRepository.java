package backend.eventsphere.review.repository;

import backend.eventsphere.review.model.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Review entity.
 */
public interface ReviewRepository {
    Optional<Review> findById(Long id);
    List<Review> findByEventId(Long eventId);
    List<Review> findByUserId(Long userId);
    Optional<Review> findByUserIdAndEventId(Long userId, Long eventId);
    Review save(Review review);
    boolean deleteById(Long id);
    List<Review> findAll();
}

/**
 * JDBC implementation of the ReviewRepository interface.
 */
@Repository
public class ReviewRepositoryImpl implements ReviewRepository {
    
    private final JdbcTemplate jdbcTemplate;
    
    private final RowMapper<Review> reviewRowMapper = (ResultSet rs, int rowNum) -> {
        Review review = new Review();
        review.setId(rs.getLong("id"));
        review.setEventId(rs.getLong("event_id"));
        review.setUserId(rs.getLong("user_id"));
        review.setRating(rs.getInt("rating"));
        review.setComment(rs.getString("comment"));
        return review;
    };
    
    @Autowired
    public ReviewRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    @Override
    public Optional<Review> findById(Long id) {
        try {
            Review review = jdbcTemplate.queryForObject(
                "SELECT id, event_id, user_id, rating, comment FROM reviews WHERE id = ?",
                reviewRowMapper,
                id
            );
            return Optional.ofNullable(review);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
    
    @Override
    public List<Review> findByEventId(Long eventId) {
        return jdbcTemplate.query(
            "SELECT id, event_id, user_id, rating, comment FROM reviews WHERE event_id = ?",
            reviewRowMapper,
            eventId
        );
    }
    
    @Override
    public List<Review> findByUserId(Long userId) {
        return jdbcTemplate.query(
            "SELECT id, event_id, user_id, rating, comment FROM reviews WHERE user_id = ?",
            reviewRowMapper,
            userId
        );
    }
    
    @Override
    public Optional<Review> findByUserIdAndEventId(Long userId, Long eventId) {
        try {
            Review review = jdbcTemplate.queryForObject(
                "SELECT id, event_id, user_id, rating, comment FROM reviews WHERE user_id = ? AND event_id = ?",
                reviewRowMapper,
                userId,
                eventId
            );
            return Optional.ofNullable(review);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
    
    @Override
    public Review save(Review review) {
        if (review.getId() == null) {
            // Insert new review
            jdbcTemplate.update(
                "INSERT INTO reviews (event_id, user_id, rating, comment) VALUES (?, ?, ?, ?)",
                review.getEventId(),
                review.getUserId(),
                review.getRating(),
                review.getComment()
            );
            
            // Get the generated ID
            Long id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
            review.setId(id);
        } else {
            // Update existing review
            jdbcTemplate.update(
                "UPDATE reviews SET rating = ?, comment = ? WHERE id = ?",
                review.getRating(),
                review.getComment(),
                review.getId()
            );
        }
        
        return review;
    }
    
    @Override
    public boolean deleteById(Long id) {
        int rowsAffected = jdbcTemplate.update("DELETE FROM reviews WHERE id = ?", id);
        return rowsAffected > 0;
    }
    
    @Override
    public List<Review> findAll() {
        return jdbcTemplate.query(
            "SELECT id, event_id, user_id, rating, comment FROM reviews",
            reviewRowMapper
        );
    }
}