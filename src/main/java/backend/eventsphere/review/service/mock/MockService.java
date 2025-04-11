package backend.eventsphere.review.service.mock;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Interface for Event related operations.
 */
public interface EventService {
    boolean eventExists(Long eventId);
    boolean userAttendedEvent(Long userId, Long eventId);
}

/**
 * Interface for User related operations.
 */
public interface UserService {
    boolean userExists(Long userId);
}

/**
 * Mock implementation of EventService for testing and development.
 */
@Service
public class EventServiceMockImpl implements EventService {
    
    // Simulated event IDs that exist in the system
    private final List<Long> existingEvents = Arrays.asList(101L, 102L, 103L);
    
    // Simulated event attendance records: Map<EventId, List<UserId>>
    private final Map<Long, List<Long>> eventAttendees = new HashMap<>() {{
        put(101L, Arrays.asList(201L, 202L));
        put(102L, Arrays.asList(201L, 203L));
        put(103L, Arrays.asList(202L));
    }};

    @Override
    public boolean eventExists(Long eventId) {
        return existingEvents.contains(eventId);
    }

    @Override
    public boolean userAttendedEvent(Long userId, Long eventId) {
        List<Long> attendees = eventAttendees.get(eventId);
        return attendees != null && attendees.contains(userId);
    }
}

/**
 * Mock implementation of UserService for testing and development.
 */
@Service
public class UserServiceMockImpl implements UserService {
    
    // Simulated user IDs that exist in the system
    private final List<Long> existingUsers = Arrays.asList(201L, 202L, 203L);
    
    @Override
    public boolean userExists(Long userId) {
        return existingUsers.contains(userId);
    }
}