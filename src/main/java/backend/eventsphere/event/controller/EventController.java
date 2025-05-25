package backend.eventsphere.event.controller;

import backend.eventsphere.auth.model.User;
import backend.eventsphere.auth.repository.UserRepository;
import backend.eventsphere.auth.service.UserService;
import backend.eventsphere.event.model.Event;
import backend.eventsphere.event.service.EventService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/events/")
public class EventController {

    private final EventService eventService;
    private final UserRepository userRepository;
    private final UserService userService;

    public EventController(EventService eventService, UserRepository userRepository, UserService userService) {
        this.eventService = eventService;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping
    public String listEvents(Model model) throws ExecutionException, InterruptedException {
        CompletableFuture<List<Event>> futureEvents = eventService.getAllEventsAsync();
        List<Event> events = futureEvents.get();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User.UserRole role = null;
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userService.findByUsername(username);
            if (user != null) {
                role = user.getRole();
            }
        }

        model.addAttribute("events", events);
        model.addAttribute("userRole", role);
        return "event/events";
    }

    @GetMapping("/create")
    public String createEventPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Event event = new Event();
        event.setOrganizerId(user.getId());
        model.addAttribute("event", event);
        return "event/createEvent";
    }

    @PostMapping("/create")
    public String createEvent(@Valid @ModelAttribute("event") Event event,
                              BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "event/createEvent";
        }

        try {
            eventService.addEvent(event);
        } catch (IllegalArgumentException e) {
            result.rejectValue("name", "duplicate", e.getMessage());
            return "event/createEvent";
        }

        return "redirect:/events/";
    }

    @PostMapping("/delete")
    public String deleteEvent(@RequestParam("id") UUID eventId, RedirectAttributes redirectAttributes) {
        try {
            eventService.deleteEventById(eventId);
            redirectAttributes.addFlashAttribute("successMessage", "Event berhasil dihapus.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("warningMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Terjadi kesalahan saat menghapus event.");
        }

        return "redirect:/events/";
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
    public String updateEvent(@Valid @ModelAttribute("event") Event event,
                              BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "event/updateEvent";
        }

        try {
            eventService.updateEvent(event.getId(), event);
        } catch (IllegalArgumentException e) {
            result.rejectValue("name", "duplicate", e.getMessage());
            return "event/updateEvent";
        }

        return "redirect:/events/";
    }
}