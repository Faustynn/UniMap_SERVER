package org.main.unimapapi.services;

import lombok.AllArgsConstructor;
import org.main.unimapapi.entities.User;
import org.main.unimapapi.repositories.UserRepository;
import org.main.unimapapi.utils.Hashing;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    public User authenticate(String login, String password) {
        Optional<User> user = userRepository.findByLogin(login);
        Optional<User> user2 = userRepository.findByEmail(login);
        if (user.isPresent()) {
           // System.out.println("User found: " + user.get().getLogin());
           // System.out.println("Password: " + password + " Hashed: " + user.get().getPassword());
            if (Hashing.checkPassword(password, user.get().getPassword())) {
              //  System.out.println("User authenticated: " + user.get().getLogin());
                return user.get();
            }
            System.out.println("Password mismatch!");
            return null;
        }else if (user2.isPresent()){
            if (Hashing.checkPassword(password, user2.get().getPassword())) {
              //  System.out.println("User authenticated: " + user2.get().getLogin());
                return user2.get();
            }
        }
        System.out.println("User not found!");
        return null;
    }
}