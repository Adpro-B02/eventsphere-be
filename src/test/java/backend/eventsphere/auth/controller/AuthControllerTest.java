package backend.eventsphere.auth.controller;

import backend.eventsphere.auth.config.JwtUtil;
import backend.eventsphere.auth.dto.LoginDto;
import backend.eventsphere.auth.dto.UserRegistrationDto;
import backend.eventsphere.auth.model.User;
import backend.eventsphere.auth.model.User.UserRole;
import backend.eventsphere.auth.repository.UserRepository;
import backend.eventsphere.auth.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    private AuthController authController;

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private Authentication authentication;

    private User sampleUser;
    private UUID userId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authController = new AuthController(userService, jwtUtil, authenticationManager, userRepository);

        userId = UUID.randomUUID();
        sampleUser = new User(userId, "testuser", "test@example.com", "password123", UserRole.ATTENDEE, "Test User");
    }

    @Test
    void whenLoginWithInvalidCredentials_thenReturnUnauthorized() {
        LoginDto loginDto = new LoginDto("testuser", "wrongpassword");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(RuntimeException.class);

        ResponseEntity<?> response = authController.login(loginDto, httpServletResponse);

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Invalid credentials", response.getBody());
    }

    // Test for logout
    @Test
    void whenLogout_thenReturnOk() {
        ResponseEntity<?> response = authController.logout(httpServletResponse);

        assertEquals(200, response.getStatusCodeValue());
        verify(httpServletResponse).addCookie(any(Cookie.class));
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertEquals("Logged out successfully", responseBody.get("message"));
    }

    @Test
    void whenGetUserByIdNotFound_thenReturnNotFound() {
        when(userService.getUserById(userId)).thenThrow(new RuntimeException("User not found"));

        ResponseEntity<?> response = authController.getUserById(userId);

        assertEquals(404, response.getStatusCodeValue());
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertEquals("User not found", responseBody.get("error"));
    }

    // Test for getCurrentUserId
    @Test
    void whenGetCurrentUserId_thenReturnUserId() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(sampleUser));

        ResponseEntity<?> response = authController.getCurrentUserId(authentication);

        assertEquals(200, response.getStatusCodeValue());
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertEquals(userId.toString(), responseBody.get("userId"));
    }

    @Test
    void whenGetCurrentUserIdUnauthorized_thenReturnUnauthorized() {
        when(authentication.isAuthenticated()).thenReturn(false);

        ResponseEntity<?> response = authController.getCurrentUserId(authentication);

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Unauthorized", response.getBody());
    }

    @Test
    void whenGetCurrentUserRoleUnauthorized_thenReturnUnauthorized() {
        when(authentication.isAuthenticated()).thenReturn(false);

        ResponseEntity<?> response = authController.getCurrentUserRole(authentication);

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Unauthorized", response.getBody());
    }
}