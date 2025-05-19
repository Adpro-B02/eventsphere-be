package backend.eventsphere.event.controller;

import backend.eventsphere.event.model.Event;
import backend.eventsphere.event.service.EventService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public String listEvents(Model model) throws ExecutionException, InterruptedException {
        CompletableFuture<List<Event>> futureEvents = eventService.getAllEventsAsync();
        List<Event> events = futureEvents.get();
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
    public String createEvent(@Valid @ModelAttribute("event") Event event, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "event/createEvent";
        }

        eventService.addEvent(event);
        return "redirect:/events";
    }

    @PostMapping("/delete")
    public String deleteEvent(@RequestParam("id") UUID eventId, Model model) {
        try {
            eventService.deleteEventById(eventId);
            model.addAttribute("successMessage", "Event berhasil dihapus.");
        } catch (IllegalStateException e) {
            model.addAttribute("warningMessage", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Terjadi kesalahan saat menghapus event.");
        }

        try {
            List<Event> events = eventService.getAllEventsAsync().get();
            model.addAttribute("events", events);
        } catch (InterruptedException | ExecutionException e) {
            model.addAttribute("errorMessage", "Gagal memuat daftar event.");
            Thread.currentThread().interrupt(); // reset interrupt status
        }

        return "event/events";
    }

    @GetMapping("/update")
    public String updateEventPage(@RequestParam("id") UUID id, Model model) {
        Event event = eventService.getEventById(id);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        String formattedDateTime = event.getEventDateTime().format(formatter);

        model.addAttribute("formattedEventDateTime", formattedDateTime);
        model.addAttribute("event", event);

        return "event/updateEvent";
    }

    @PostMapping("/update")
    public String updateEvent(@Valid @ModelAttribute("event") Event event, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "event/updateEvent";
        }

        eventService.updateEvent(event.getId(), event);
        return "redirect:/events";
    }
}