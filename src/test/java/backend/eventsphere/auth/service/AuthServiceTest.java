package backend.eventsphere.auth.service;

import backend.eventsphere.auth.model.User;
import backend.eventsphere.auth.repository.AuthRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private AuthRepository authRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthService(authRepository, passwordEncoder);
    }

    @Test
    void registerUser_WithNewUser_ShouldReturnTrue() {
        // Given
        User user = new User("testuser", "test@example.com", "password", User.Role.ATTENDEE);
        when(authRepository.existsByEmail(anyString())).thenReturn(false);
        when(authRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // When
        boolean result = authService.registerUser(user);

        // Then
        assertTrue(result);
        verify(authRepository).save(user);
        verify(passwordEncoder).encode("password");
    }

    @Test
    void registerUser_WithExistingEmailOrUsername_ShouldReturnFalse() {
        // Given
        User user = new User("testuser", "test@example.com", "password", User.Role.ATTENDEE);
        when(authRepository.existsByEmail("test@example.com")).thenReturn(true);

        // When
        boolean result = authService.registerUser(user);

        // Then
        assertFalse(result);
        verify(authRepository, never()).save(any(User.class));
    }

    @Test
    void login_WithValidEmailAndPassword_ShouldReturnUser() {
        // Given
        User user = new User("testuser", "test@example.com", "encodedPassword", User.Role.ATTENDEE);
        when(authRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);

        // When
        User loggedInUser = authService.login("test@example.com", "password");

        // Then
        assertNotNull(loggedInUser);
        assertEquals(user, loggedInUser);
    }

    @Test
    void login_WithValidUsernameAndPassword_ShouldReturnUser() {
        // Given
        User user = new User("testuser", "test@example.com", "encodedPassword", User.Role.ATTENDEE);
        when(authRepository.findByEmail("testuser")).thenReturn(Optional.empty());
        when(authRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);

        // When
        User loggedInUser = authService.login("testuser", "password");

        // Then
        assertNotNull(loggedInUser);
        assertEquals(user, loggedInUser);
    }

    @Test
    void login_WithInvalidCredentials_ShouldReturnNull() {
        // Given
        User user = new User("testuser", "test@example.com", "encodedPassword", User.Role.ATTENDEE);
        when(authRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        // When
        User loggedInUser = authService.login("test@example.com", "wrongpassword");

        // Then
        assertNull(loggedInUser);
    }

    @Test
    void createGuestUser_ShouldReturnGuestUser() {
        // When
        User guestUser = authService.createGuestUser();

        // Then
        assertNotNull(guestUser);
        assertEquals(User.Role.GUEST, guestUser.getRole());
    }
}
