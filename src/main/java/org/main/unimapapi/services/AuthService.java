package org.main.unimapapi.services;

import lombok.AllArgsConstructor;
import org.main.unimapapi.entities.User;
import org.main.unimapapi.repository_queries.UserRepository;
import org.main.unimapapi.utils.Hashing;
import org.main.unimapapi.utils.ServerLogger;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service responsible for authenticating users based on login/email and password.
 *
 * <p>Used in login workflows, typically by {@code UserController} or OAuth handlers.</p>
 */
@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    /**
     * Attempts to authenticate a user by login or email and password.
     *
     * @param loginOrEmail the login or email provided by the user
     * @param password     the plain-text password to check
     * @return the authenticated {@link User} if successful, or {@code null} if authentication fails
     */
    public User authenticate(String login, String password) {
        try {
            Optional<User> userOptional = userRepository.findByLogin(login);
            if (userOptional.isEmpty()) {
                userOptional = userRepository.findByEmail(login);
            }

            if (userOptional.isPresent() && Hashing.checkPassword(password, userOptional.get().getPassword())) {
                return userOptional.get();
            }

            ServerLogger.logServer(ServerLogger.Level.WARNING, "User not found!");
            return null;
        } catch (Exception e) {
         //   System.err.println("Error during authentication: " + e.getMessage());
            ServerLogger.logServer(ServerLogger.Level.ERROR, "Error during authentication: " + e.getMessage());
            return null;
        }
    }
}