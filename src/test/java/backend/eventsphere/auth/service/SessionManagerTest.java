package backend.eventsphere.auth.service;

import backend.eventsphere.auth.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SessionManagerTest {

    @Autowired
    private SessionManager sessionManager;

    @Test
    void setCurrentUser_ShouldStoreUserInSession() {
        User user = new User("testuser", "test@example.com", "password", User.Role.ATTENDEE);
        sessionManager.setCurrentUser(user);
        assertEquals(user, sessionManager.getCurrentUser());
    }

    @Test
    void clearSession_ShouldRemoveCurrentUser() {
        User user = new User("testuser", "test@example.com", "password", User.Role.ATTENDEE);
        sessionManager.setCurrentUser(user);
        sessionManager.clearSession();
        assertNull(sessionManager.getCurrentUser());
    }

    @Test
    void isLoggedIn_WithLoggedInUser_ShouldReturnTrue() {
        User user = new User("testuser", "test@example.com", "password", User.Role.ATTENDEE);
        sessionManager.setCurrentUser(user);
        assertTrue(sessionManager.isLoggedIn());
    }

    @Test
    void isLoggedIn_WithNoUser_ShouldReturnFalse() {
        sessionManager.clearSession();
        assertFalse(sessionManager.isLoggedIn());
    }
}
