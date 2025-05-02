package backend.eventsphere.review.service.mock;

import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;

@Service("mockUserService") // Qualify bean name to avoid conflicts
public class UserService {
    // Hardcoded user IDs that we'll consider as "existing"
    private static final List<Long> VALID_USER_IDS = Arrays.asList(1L, 2L, 3L, 4L, 5L);
    
    /**
     * Check if a user exists
     */
    public boolean userExists(Long userId) {
        return userId != null && VALID_USER_IDS.contains(userId);
    }
}
