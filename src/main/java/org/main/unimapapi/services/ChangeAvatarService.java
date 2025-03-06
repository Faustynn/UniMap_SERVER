package org.main.unimapapi.services;

import org.main.unimapapi.entities.User;
import org.main.unimapapi.repository_queries.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ChangeAvatarService {

    private final UserRepository userRepository;

    @Autowired
    public ChangeAvatarService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public boolean changeAvatar(String email, String avatarPath) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            return false;
        } else {
            User userEntity = user.get();
            userEntity.setAvatar(avatarPath); // Set the avatar path
            userRepository.update(userEntity);
            return true;
        }
    }
}