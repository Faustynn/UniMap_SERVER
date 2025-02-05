package org.main.unimapapi.services;

import lombok.AllArgsConstructor;
import org.main.unimapapi.dtos.User_dto;
import org.main.unimapapi.entities.User;
import org.main.unimapapi.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User create(User_dto dto) {
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

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User update(User user) {
        return userRepository.save(user);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public User findByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.orElse(null);
    }

    public User findByLogin(String login) {
        Optional<User> user = userRepository.findByLogin(login);
        return user.orElse(null);
    }

    public User findByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.orElse(null);
    }

}