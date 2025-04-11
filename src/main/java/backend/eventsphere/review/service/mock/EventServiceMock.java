package backend.eventsphere.review.service.mock;

import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;

@Service("mockEventService")
public class EventServiceMock {
    // Hardcoded event IDs that we'll consider as "existing"
    private static final List<Long> VALID_EVENT_IDS = Arrays.asList(1L, 2L, 3L, 4L, 5L);
    
    // Hardcoded user-event pairs that represent users who attended events
    private static final List<String> USER_EVENT_PAIRS = Arrays.asList(
        "1-1", "1-2", "2-1", "2-3", "3-2", "3-4", "4-5", "5-1"
    );

    /**
     * Check if an event exists
     */
    public boolean eventExists(Long eventId) {
        return eventId != null && VALID_EVENT_IDS.contains(eventId);
    }
    
    /**
     * Check if a user attended an event
     */
    public boolean userAttendedEvent(Long userId, Long eventId) {
        if (userId == null || eventId == null) {
            return false;
        }
        
        String pair = userId + "-" + eventId;
        return USER_EVENT_PAIRS.contains(pair);
    }
}