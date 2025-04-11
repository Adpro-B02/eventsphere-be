package backend.eventsphere.auth.repository;

import backend.eventsphere.auth.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AuthRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AuthRepository authRepository;

    @Test
    void findByEmail_ShouldReturnUser() {
        User user = new User("testuser", "test@example.com", "password", User.Role.ATTENDEE);
        entityManager.persistAndFlush(user);

        Optional<User> found = authRepository.findByEmail("test@example.com");

        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
    }

    @Test
    void findByUsername_ShouldReturnUser() {
        User user = new User("testuser", "test@example.com", "password", User.Role.ATTENDEE);
        entityManager.persistAndFlush(user);

        Optional<User> found = authRepository.findByUsername("testuser");

        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());
    }

    @Test
    void existsByEmailOrUsername_ShouldReturnTrue() {
        User user = new User("testuser", "test@example.com", "password", User.Role.ATTENDEE);
        entityManager.persistAndFlush(user);

        assertTrue(authRepository.existsByEmail("test@example.com"));
        assertTrue(authRepository.existsByUsername("testuser"));
        assertFalse(authRepository.existsByEmail("nonexistent@example.com"));
        assertFalse(authRepository.existsByUsername("nonexistent"));
    }
}
