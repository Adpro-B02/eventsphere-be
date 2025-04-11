package backend.eventsphere.auth.service;

import backend.eventsphere.auth.model.User;
import backend.eventsphere.auth.repository.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(AuthRepository authRepository, PasswordEncoder passwordEncoder) {
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean registerUser(User user) {
        // Check if user already exists
        if (authRepository.existsByEmail(user.getEmail()) || authRepository.existsByUsername(user.getUsername())) {
            return false;
        }

        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save user
        authRepository.save(user);
        return true;
    }

    public User login(String emailOrUsername, String password) {
        User user = null;

        // Try to find by email
        Optional<User> userByEmail = authRepository.findByEmail(emailOrUsername);
        if (userByEmail.isPresent()) {
            user = userByEmail.get();
        } else {
            // Try to find by username
            Optional<User> userByUsername = authRepository.findByUsername(emailOrUsername);
            if (userByUsername.isPresent()) {
                user = userByUsername.get();
            }
        }

        // Check if user exists and password matches
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }

        return null;
    }

    public User createGuestUser() {
        User guestUser = new User();
        guestUser.setRole(User.Role.GUEST);
        return guestUser;
    }
}
