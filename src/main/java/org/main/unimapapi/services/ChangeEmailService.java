package org.main.unimapapi.services;

import org.main.unimapapi.entities.User;
import org.main.unimapapi.repository_queries.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ChangeEmailService {

    private final UserRepository userRepository;

    @Autowired
    public ChangeEmailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public boolean changeEmail(String login, String new_email) {
        Optional<User> user = userRepository.findByLogin(login);
        if (user.isEmpty()) {
            return false;
        } else {
            User userEntity = user.get();
            userEntity.setEmail(new_email); // Set the avatar path
            userRepository.update(userEntity);
            return true;
        }
    }
}