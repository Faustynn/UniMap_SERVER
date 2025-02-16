package org.main.unimapapi.services;

import org.main.unimapapi.entities.User;
import org.main.unimapapi.repository_queries.UserRepositoryImpl;
import org.main.unimapapi.utils.Hashing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ChangePassService {
    private final UserRepositoryImpl userRepository;

    @Autowired
    public ChangePassService(UserRepositoryImpl userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public boolean changePassword(String email, String new_password) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            return false;
        } else {
            new_password = Hashing.hashPassword(new_password);
            User userEntity = user.get();
            userEntity.setPassword(new_password);
            userRepository.update(userEntity);
            return true;
        }
    }
}