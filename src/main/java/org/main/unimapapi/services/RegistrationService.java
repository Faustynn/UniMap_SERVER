package org.main.unimapapi.services;

import lombok.AllArgsConstructor;
import org.main.unimapapi.dtos.User_dto;
import org.main.unimapapi.entities.User;
import org.main.unimapapi.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RegistrationService {
    private final UserRepository userRepository;

    public User register(User_dto dto) {
        User user = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .login(dto.getLogin())
                .password(dto.getPassword())
                .isAdmin(dto.isAdmin())
                .subscribe(dto.isSubscribe())
                .verification(dto.isVerification())
                .avatar(dto.getAvatar())
                .build();
        return userRepository.save(user);
    }
}
