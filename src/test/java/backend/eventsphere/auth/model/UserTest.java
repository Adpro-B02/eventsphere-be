package backend.eventsphere.auth.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserEquality() {
        User user1 = new User("testuser", "test@example.com", "password", User.Role.ATTENDEE);
        User user2 = new User("testuser", "different@example.com", "password", User.Role.ATTENDEE);
        User user3 = new User("different", "test@example.com", "password", User.Role.ATTENDEE);
        User user4 = new User("different", "different@example.com", "password", User.Role.ATTENDEE);
        
        // Same username or email should be considered equal
        assertEquals(user1, user2);
        assertEquals(user1, user3);
        assertNotEquals(user1, user4);
    }
}