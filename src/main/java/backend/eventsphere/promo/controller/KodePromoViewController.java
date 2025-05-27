package backend.eventsphere.promo.controller;

import backend.eventsphere.auth.model.User;
import backend.eventsphere.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/promos")
public class KodePromoViewController {

    private final UserService userService;

    @GetMapping("/event/{eventId}")
    public String showPromosByEventPage(@PathVariable UUID eventId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User.UserRole role = null;
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userService.findByUsername(username);
            if (user != null) {
                role = user.getRole();
            }
        }
        model.addAttribute("userRole", role);
        model.addAttribute("eventId", eventId);
        return "promo/promo";
    }

}