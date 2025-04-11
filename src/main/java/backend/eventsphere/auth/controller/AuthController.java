package backend.eventsphere.auth.controller;

import backend.eventsphere.auth.model.User;
import backend.eventsphere.auth.service.AuthService;
import backend.eventsphere.auth.service.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {
    
    private final AuthService authService;
    private final SessionManager sessionManager;
    
    @Autowired
    public AuthController(AuthService authService, SessionManager sessionManager) {
        this.authService = authService;
        this.sessionManager = sessionManager;
    }
    
    @GetMapping("/")
    public String index() {
        if (sessionManager.isLoggedIn()) {
            User currentUser = sessionManager.getCurrentUser();
            return redirectBasedOnRole(currentUser.getRole());
        }
        return "redirect:/login";
    }
    
    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }
    
    @PostMapping("/login")
    public String login(@RequestParam String emailOrUsername, @RequestParam String password, RedirectAttributes redirectAttributes) {
        User user = authService.login(emailOrUsername, password);
        
        if (user != null) {
            sessionManager.setCurrentUser(user);
            return redirectBasedOnRole(user.getRole());
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid login credentials");
            return "redirect:/login";
        }
    }
    
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        
        // Determine what type of account can be created based on current user
        User currentUser = sessionManager.getCurrentUser();
        boolean canCreateAdminOrOrganizer = (currentUser != null && currentUser.getRole() == User.Role.ADMIN);
        model.addAttribute("canCreateAdminOrOrganizer", canCreateAdminOrOrganizer);
        
        return "register";
    }
    
    @PostMapping("/register")
    public String register(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        // Set default role based on current user
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser == null || currentUser.getRole() == User.Role.GUEST) {
            user.setRole(User.Role.ATTENDEE);
        } else if (currentUser.getRole() == User.Role.ADMIN && 
                  (user.getRole() == User.Role.ADMIN || user.getRole() == User.Role.ORGANIZER)) {
            // Admin can create ADMIN or ORGANIZER roles - keep the role as is
        } else {
            // Default to ATTENDEE for any other case
            user.setRole(User.Role.ATTENDEE);
        }
        
        boolean success = authService.registerUser(user);
        
        if (success) {
            redirectAttributes.addFlashAttribute("success", "Account successfully created");
            return "redirect:/login";
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to create account. Email or username already exists.");
            return "redirect:/register";
        }
    }
    
    @GetMapping("/logout")
    public String logout() {
        sessionManager.clearSession();
        return "redirect:/login";
    }
    
    @GetMapping("/guest")
    public String loginAsGuest() {
        User guestUser = authService.createGuestUser();
        sessionManager.setCurrentUser(guestUser);
        return "redirect:/events";
    }
    
    private String redirectBasedOnRole(User.Role role) {
        switch (role) {
            case ADMIN:
                return "redirect:/admin";
            case ORGANIZER:
                return "redirect:/organizer/dashboard";
            case ATTENDEE:
            case GUEST:
                return "redirect:/events";
            default:
                return "redirect:/login";
        }
    }
    
    // Placeholder mappings for different dashboards
    @GetMapping("/admin")
    public String adminPanel() {
        return "admin";
    }
    
    @GetMapping("/organizer/dashboard")
    public String organizerDashboard() {
        return "organizer/dashboard";
    }
    
    @GetMapping("/events")
    public String eventsPage() {
        return "events";
    }
}