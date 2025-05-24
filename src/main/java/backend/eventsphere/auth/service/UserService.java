package backend.eventsphere.auth.service;

import backend.eventsphere.auth.dto.LoginDto;
import backend.eventsphere.auth.dto.UserRegistrationDto;
import backend.eventsphere.auth.model.User;
import backend.eventsphere.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    public User registerUser(UserRegistrationDto registrationDto) {
        // Check if user already exists
        if (userRepository.existsByEmail(registrationDto.getEmail()) || 
            userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new RuntimeException("User with this email or username already exists");
        }
        
        // Create new user
        User user = new User();
        user.setEmail(registrationDto.getEmail());
        user.setUsername(registrationDto.getUsername());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setFullName(registrationDto.getFullName());
        user.setRole(registrationDto.getRole());
        
        return userRepository.save(user);
    }
    
    public User login(LoginDto loginDto) {
        // Try to find user by email or username
        Optional<User> userByEmail = userRepository.findByEmail(loginDto.getEmailOrUsername());
        Optional<User> userByUsername = userRepository.findByUsername(loginDto.getEmailOrUsername());
        
        User user = userByEmail.orElseGet(() -> userByUsername.orElse(null));
        
        if (user != null && passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            return user;
        }
        
        throw new RuntimeException("Invalid credentials");
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public User updateUser(UUID id, UserRegistrationDto updateDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if username/email are being changed and if they're already taken
        if (!user.getEmail().equals(updateDto.getEmail()) && 
            userRepository.existsByEmail(updateDto.getEmail())) {
            throw new RuntimeException("Email already in use");
        }
        
        if (!user.getUsername().equals(updateDto.getUsername()) && 
            userRepository.existsByUsername(updateDto.getUsername())) {
            throw new RuntimeException("Username already in use");
        }
        
        user.setEmail(updateDto.getEmail());
        user.setUsername(updateDto.getUsername());
        user.setFullName(updateDto.getFullName());
        
        // Only update password if it's provided in the DTO
        if (updateDto.getPassword() != null && !updateDto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updateDto.getPassword()));
        }
        
        // Only update role if it's provided in the DTO
        if (updateDto.getRole() != null) {
            user.setRole(updateDto.getRole());
        }
        
        return userRepository.save(user);
    }
    
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }
    
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
