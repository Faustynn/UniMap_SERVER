package org.main.unimapapi.services;

import org.main.unimapapi.entities.User;
import org.main.unimapapi.repository_queries.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ChangeUsernameService {

    private final UserRepository userRepository;

    @Autowired
    public ChangeUsernameService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public boolean changeUsername(String email, String new_username) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            return false;
        } else {
            User userEntity = user.get();
            userEntity.setUsername(new_username); // Set the avatar path
            userRepository.update(userEntity);
            return true;
        }
    }
}