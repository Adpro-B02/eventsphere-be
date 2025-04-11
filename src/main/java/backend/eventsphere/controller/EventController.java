package backend.eventsphere.controller;

import backend.eventsphere.model.Event;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import backend.eventsphere.service.EventService;
import backend.eventsphere.service.strategy.DeletionValidation;
import backend.eventsphere.service.strategy.EventDataValidation;
import backend.eventsphere.service.strategy.EventExistValidation;
import backend.eventsphere.service.strategy.ValidationStrategy;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService service;

    public EventController(EventService service) {
        this.service = service;
    }

    @GetMapping
    public List<Event> getAll() {
        return service.getAllEvents();
    }

    @PostMapping
    public ResponseEntity<Event> create(@RequestBody Event event) {
        List<ValidationStrategy> validations = List.of(
                new EventExistValidation(service.getRepository()),
                new EventDataValidation()
        );
        Event created = service.createEvent(event, validations);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Event> update(@PathVariable UUID id, @RequestBody Event event) {
        List<ValidationStrategy> validations = List.of(new EventDataValidation());
        Event updated = service.updateEvent(id, event, validations);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteEvent(id, new DeletionValidation());
        return ResponseEntity.noContent().build();
    }
}
