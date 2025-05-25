package backend.eventsphere.ticket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tickets")
public class TicketWebController {

    @GetMapping("/{eventId}")
    public String viewTicketsForEvent(@PathVariable String eventId, Model model) {
        model.addAttribute("eventId", eventId);
        return "ticket/tickets";
    }
}