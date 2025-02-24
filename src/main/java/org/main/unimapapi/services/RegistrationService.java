package org.main.unimapapi.services;

import org.main.unimapapi.dtos.User_dto;
import org.main.unimapapi.entities.User;
import org.main.unimapapi.repository_queries.UserRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {
    private final UserRepositoryImpl userRepository;

    @Autowired
    public RegistrationService(UserRepositoryImpl userRepository) {
        this.userRepository = userRepository;
    }

    public User register(User_dto dto) {
        User user = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .login(dto.getLogin())
                .password(dto.getPassword())
                .isAdmin(dto.isAdmin())
                .avatar(dto.getAvatar())
                .build();
        userRepository.save(user);
        return user;
    }
}