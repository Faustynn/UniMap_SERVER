package org.main.unimapapi.services;

import lombok.AllArgsConstructor;
import org.main.unimapapi.entities.User;
import org.main.unimapapi.repository_queries.UserRepository;
import org.main.unimapapi.utils.Hashing;
import org.main.unimapapi.utils.ServerLogger;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    public User authenticate(String login, String password) throws Exception {
        Optional<User> user = userRepository.findByLogin(login);
        Optional<User> user2 = userRepository.findByEmail(login);
        if (user.isPresent()) {
            if (Hashing.checkPassword(password, user.get().getPassword())) {
                return user.get();
            }
            ServerLogger.logServer(ServerLogger.Level.WARNING, "Password mismatch!"+ Hashing.hashPassword(password));

            return null;
        } else if (user2.isPresent()) {
            if (Hashing.checkPassword(password, user2.get().getPassword())) {
                return user2.get();
            }
        }
        ServerLogger.logServer(ServerLogger.Level.WARNING, "User not found!");
        return null;
    }
}