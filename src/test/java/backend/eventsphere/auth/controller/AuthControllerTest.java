package backend.eventsphere.auth.controller;

import backend.eventsphere.auth.model.User;
import backend.eventsphere.auth.service.AuthService;
import backend.eventsphere.auth.service.SessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authController = new AuthController(authService, sessionManager);
    }

    @Test
    void index_WithLoggedInAdmin_ShouldRedirectToAdmin() {
        // Given
        User adminUser = new User();
        adminUser.setRole(User.Role.ADMIN);
        when(sessionManager.getCurrentUser()).thenReturn(adminUser);
        when(sessionManager.isLoggedIn()).thenReturn(true);
        
        // When
        String result = authController.index();
        
        // Then
        assertEquals("redirect:/admin", result);
    }

    @Test
    void index_WithLoggedInOrganizer_ShouldRedirectToOrganizerDashboard() {
        // Given
        User organizerUser = new User();
        organizerUser.setRole(User.Role.ORGANIZER);
        when(sessionManager.getCurrentUser()).thenReturn(organizerUser);
        when(sessionManager.isLoggedIn()).thenReturn(true);
        
        // When
        String result = authController.index();
        
        // Then
        assertEquals("redirect:/organizer/dashboard", result);
    }

    @Test
    void index_WithLoggedInAttendee_ShouldRedirectToEvents() {
        // Given
        User attendeeUser = new User();
        attendeeUser.setRole(User.Role.ATTENDEE);
        when(sessionManager.getCurrentUser()).thenReturn(attendeeUser);
        when(sessionManager.isLoggedIn()).thenReturn(true);
        
        // When
        String result = authController.index();
        
        // Then
        assertEquals("redirect:/events", result);
    }

    @Test
    void index_WithNoLoggedInUser_ShouldRedirectToLogin() {
        // Given
        when(sessionManager.isLoggedIn()).thenReturn(false);
        
        // When
        String result = authController.index();
        
        // Then
        assertEquals("redirect:/login", result);
    }

    @Test
    void loginPage_ShouldReturnLoginView() {
        // When
        String result = authController.loginPage(model);
        
        // Then
        assertEquals("login", result);
        verify(model).addAttribute(eq("user"), any(User.class));
    }

    @Test
    void login_WithValidCredentials_ShouldRedirectBasedOnRole() {
        // Given
        User user = new User();
        user.setRole(User.Role.ATTENDEE);
        when(authService.login("test@example.com", "password")).thenReturn(user);
        
        // When
        String result = authController.login("test@example.com", "password", redirectAttributes);
        
        // Then
        assertEquals("redirect:/events", result);
        verify(sessionManager).setCurrentUser(user);
    }

    @Test
    void login_WithInvalidCredentials_ShouldRedirectToLoginWithError() {
        // Given
        when(authService.login("test@example.com", "wrongpassword")).thenReturn(null);
        
        // When
        String result = authController.login("test@example.com", "wrongpassword", redirectAttributes);
        
        // Then
        assertEquals("redirect:/login", result);
        verify(redirectAttributes).addFlashAttribute("error", "Invalid login credentials");
    }

    @Test
    void registerPage_AsAdmin_ShouldAllowAdminOrOrganizerCreation() {
        // Given
        User adminUser = new User();
        adminUser.setRole(User.Role.ADMIN);
        when(sessionManager.getCurrentUser()).thenReturn(adminUser);
        
        // When
        String result = authController.registerPage(model);
        
        // Then
        assertEquals("register", result);
        verify(model).addAttribute("canCreateAdminOrOrganizer", true);
    }

    @Test
    void registerPage_AsNonAdmin_ShouldNotAllowAdminOrOrganizerCreation() {
        // Given
        User attendeeUser = new User();
        attendeeUser.setRole(User.Role.ATTENDEE);
        when(sessionManager.getCurrentUser()).thenReturn(attendeeUser);
        
        // When
        String result = authController.registerPage(model);
        
        // Then
        assertEquals("register", result);
        verify(model).addAttribute("canCreateAdminOrOrganizer", false);
    }

    @Test
    void register_WithNewUser_ShouldRedirectToLoginWithSuccess() {
        // Given
        User newUser = new User("testuser", "test@example.com", "password", User.Role.ATTENDEE);
        when(authService.registerUser(any(User.class))).thenReturn(true);
        
        // When
        String result = authController.register(newUser, redirectAttributes);
        
        // Then
        assertEquals("redirect:/login", result);
        verify(redirectAttributes).addFlashAttribute("success", "Account successfully created");
    }

    @Test
    void register_WithExistingUser_ShouldRedirectToRegisterWithError() {
        // Given
        User existingUser = new User("testuser", "test@example.com", "password", User.Role.ATTENDEE);
        when(authService.registerUser(any(User.class))).thenReturn(false);
        
        // When
        String result = authController.register(existingUser, redirectAttributes);
        
        // Then
        assertEquals("redirect:/register", result);
        verify(redirectAttributes).addFlashAttribute("error", "Failed to create account. Email or username already exists.");
    }

    @Test
    void register_AsAdmin_ShouldAllowCreatingOrganizerOrAdmin() {
        // Given
        User adminUser = new User();
        adminUser.setRole(User.Role.ADMIN);
        when(sessionManager.getCurrentUser()).thenReturn(adminUser);
        
        User newOrganizerUser = new User("organizer", "organizer@example.com", "password", User.Role.ORGANIZER);
        when(authService.registerUser(any(User.class))).thenReturn(true);
        
        // When
        String result = authController.register(newOrganizerUser, redirectAttributes);
        
        // Then
        assertEquals("redirect:/login", result);
        verify(authService).registerUser(newOrganizerUser);
    }

    @Test
    void logout_ShouldClearSessionAndRedirectToLogin() {
        // When
        String result = authController.logout();
        
        // Then
        assertEquals("redirect:/login", result);
        verify(sessionManager).clearSession();
    }

    @Test
    void loginAsGuest_ShouldCreateGuestUserAndRedirectToEvents() {
        // Given
        User guestUser = new User();
        guestUser.setRole(User.Role.GUEST);
        when(authService.createGuestUser()).thenReturn(guestUser);
        
        // When
        String result = authController.loginAsGuest();
        
        // Then
        assertEquals("redirect:/events", result);
        verify(sessionManager).setCurrentUser(guestUser);
    }
}