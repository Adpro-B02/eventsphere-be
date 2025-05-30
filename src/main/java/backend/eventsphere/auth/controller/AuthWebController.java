package backend.eventsphere.auth.controller;

import backend.eventsphere.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthWebController {
    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    @GetMapping("/userlist")
    public String userListPage(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "auth/userlist";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "auth/dashboard";
    }
}