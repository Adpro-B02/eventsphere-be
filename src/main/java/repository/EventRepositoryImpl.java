package repository;

import model.Event;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EventRepositoryImpl implements EventRepository {
    private final Map<UUID, Event> eventStorage = new ConcurrentHashMap<>();

    @Override
    public Event save(Event event) {
        eventStorage.put(event.getId(), event);
        return event;
    }

    @Override
    public Optional<Event> findById(UUID id) {
        return Optional.ofNullable(eventStorage.get(id));
    }

    @Override
    public List<Event> findAll() {
        return new ArrayList<>(eventStorage.values());
    }

    @Override
    public void deleteById(UUID id) {
        eventStorage.remove(id);
    }

    @Override
    public boolean existsByName(String name) {
        return eventStorage.values().stream()
                .anyMatch(e -> e.getName().equalsIgnoreCase(name));
    }
}
