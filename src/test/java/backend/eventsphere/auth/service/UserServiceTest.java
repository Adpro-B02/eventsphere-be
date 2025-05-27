package backend.eventsphere.auth.service;

import backend.eventsphere.auth.dto.LoginDto;
import backend.eventsphere.auth.dto.UserRegistrationDto;
import backend.eventsphere.auth.model.User;
import backend.eventsphere.auth.model.User.UserRole;
import backend.eventsphere.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private User sampleUser;
    private UUID userId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository, passwordEncoder);

        userId = UUID.randomUUID();
        sampleUser = new User(userId, "test@example.com", "testuser", "encodedPassword", UserRole.ATTENDEE, "Test User");
    }

    // Test for registerUser
    @Test
    void whenRegisterUser_thenReturnSavedUser() {
        // Arrange
        UserRegistrationDto registrationDto = new UserRegistrationDto(
                "test@example.com", "testuser", "Password@123", "Test User", UserRole.ATTENDEE);
        when(userRepository.existsByEmail(registrationDto.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(registrationDto.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(registrationDto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        // Act
        User registeredUser = userService.registerUser(registrationDto);

        // Assert
        assertNotNull(registeredUser);
        assertEquals("test@example.com", registeredUser.getEmail());
        assertEquals("testuser", registeredUser.getUsername());
        assertEquals("encodedPassword", registeredUser.getPassword());
        assertEquals(UserRole.ATTENDEE, registeredUser.getRole());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void whenRegisterUserWithExistingEmail_thenThrowException() {
        // Arrange
        UserRegistrationDto registrationDto = new UserRegistrationDto(
                "test@example.com", "testuser", "Password@123", "Test User", UserRole.ATTENDEE);
        when(userRepository.existsByEmail(registrationDto.getEmail())).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.registerUser(registrationDto));
        assertEquals("User with this email or username already exists", exception.getMessage());
    }

    // Test for login
    @Test
    void whenLoginWithValidCredentials_thenReturnUser() {
        // Arrange
        LoginDto loginDto = new LoginDto("test@example.com", "Password@123");
        when(userRepository.findByEmail(loginDto.getEmailOrUsername())).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches(loginDto.getPassword(), sampleUser.getPassword())).thenReturn(true);

        // Act
        User loggedInUser = userService.login(loginDto);

        // Assert
        assertNotNull(loggedInUser);
        assertEquals("test@example.com", loggedInUser.getEmail());
        verify(userRepository).findByEmail(loginDto.getEmailOrUsername());
    }

    @Test
    void whenLoginWithInvalidCredentials_thenThrowException() {
        // Arrange
        LoginDto loginDto = new LoginDto("test@example.com", "WrongPassword");
        when(userRepository.findByEmail(loginDto.getEmailOrUsername())).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches(loginDto.getPassword(), sampleUser.getPassword())).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.login(loginDto));
        assertEquals("Invalid credentials", exception.getMessage());
    }

    // Test for getAllUsers
    @Test
    void whenGetAllUsers_thenReturnListOfUsers() {
        // Arrange
        when(userRepository.findAll()).thenReturn(List.of(sampleUser));

        // Act
        List<User> users = userService.getAllUsers();

        // Assert
        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals("testuser", users.get(0).getUsername());
    }

    // Test for updateUser
    @Test
    void whenUpdateUser_thenReturnUpdatedUser() {
        // Arrange
        UserRegistrationDto updateDto = new UserRegistrationDto(
                "updated@example.com", "updateduser", "UpdatedPassword@123", "Updated User", UserRole.ORGANIZER);
        when(userRepository.findById(userId)).thenReturn(Optional.of(sampleUser));
        when(userRepository.existsByEmail(updateDto.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(updateDto.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(updateDto.getPassword())).thenReturn("encodedUpdatedPassword");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        // Act
        User updatedUser = userService.updateUser(userId, updateDto);

        // Assert
        assertNotNull(updatedUser);
        assertEquals("updated@example.com", updatedUser.getEmail());
        assertEquals("updateduser", updatedUser.getUsername());
        assertEquals("encodedUpdatedPassword", updatedUser.getPassword());
        assertEquals(UserRole.ORGANIZER, updatedUser.getRole());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void whenUpdateUserWithNonExistingId_thenThrowException() {
        // Arrange
        UserRegistrationDto updateDto = new UserRegistrationDto(
                "updated@example.com", "updateduser", "UpdatedPassword@123", "Updated User", UserRole.ORGANIZER);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.updateUser(userId, updateDto));
        assertEquals("User not found", exception.getMessage());
    }

    // Test for deleteUser
    @Test
    void whenDeleteUser_thenVerifyDeletion() {
        // Arrange
        when(userRepository.existsById(userId)).thenReturn(true);

        // Act
        userService.deleteUser(userId);

        // Assert
        verify(userRepository).deleteById(userId);
    }

    @Test
    void whenDeleteUserWithNonExistingId_thenThrowException() {
        // Arrange
        when(userRepository.existsById(userId)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.deleteUser(userId));
        assertEquals("User not found", exception.getMessage());
    }

    // Test for getUserById
    @Test
    void whenGetUserById_thenReturnUser() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(sampleUser));

        // Act
        User user = userService.getUserById(userId);

        // Assert
        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
    }

    @Test
    void whenGetUserByIdWithNonExistingId_thenThrowException() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.getUserById(userId));
        assertEquals("User not found", exception.getMessage());
    }
}