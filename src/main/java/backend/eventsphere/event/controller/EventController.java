package backend.eventsphere.event.controller;

import backend.eventsphere.event.model.Event;
import backend.eventsphere.event.service.EventService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public String listEvents(Model model) {
        List<Event> events = eventService.getAllEvents();
        model.addAttribute("events", events);
        return "event/events";
    }

    @GetMapping("/create")
    public String createEventPage(Model model) {
        Event event = new Event();
        event.setOrganizerId(UUID.fromString("00000000-0000-0000-0000-000000000001")); // login user ID (organizer), sementara hardcoded
        model.addAttribute("event", event);
        return "event/createEvent";
    }

    @PostMapping("/create")
    public String createEventPost(@ModelAttribute Event event) {
        eventService.addEvent(event);
        return "redirect:/events";
    }
}