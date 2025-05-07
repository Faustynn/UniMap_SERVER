package org.main.unimapapi.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.main.unimapapi.dtos.User_dto;
import org.main.unimapapi.entities.User;
import org.main.unimapapi.repository_queries.UserRepository;
import org.main.unimapapi.utils.Hashing;
import org.main.unimapapi.utils.ServerLogger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User create(User_dto dto) {
        User user = User.builder()
                .login(dto.getLogin())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .username(dto.getUsername())
                .isAdmin(dto.isAdmin())
                .isPremium(dto.isPremium())
                .avatar(dto.getAvatarBinary() != null ? dto.getAvatarBinary() : null)
                .avatarFileName(dto.getAvatarFileName())
                .build();

        userRepository.save(user);
        return user;
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User update(User user) {
        userRepository.update(user);
        return user;
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void deleteAllUserInfo(Long id) {
        userRepository.deleteAllUserInfo(id);
    }

    public void deleteAllUserComments(Long id) {
        userRepository.deleteAllUserComments(id);
    }

    public boolean updateAvatarData(String login, byte[] avatarBinary, String fileName) {
        return updateUserProperty(login, user -> {
            user.setAvatar(avatarBinary);
            user.setAvatarFileName(fileName);
            ServerLogger.logServer(ServerLogger.Level.INFO, "Avatar updated successfully for user: " + login);
        });
    }

    public boolean changeEmail(String login, String newEmail) {
        return updateUserProperty(login, user -> user.setEmail(newEmail));
    }

    public boolean changePassword(String email, String newPassword) {
        return userRepository.findByEmail(email)
                .map(user -> {
                    user.setPassword(Hashing.hashPassword(newPassword));
                    userRepository.update(user);
                    return true;
                })
                .orElse(false);
    }

    public boolean changeUsername(String email, String newUsername) {
        return userRepository.findByEmail(email)
                .map(user -> {
                    user.setUsername(newUsername);
                    userRepository.update(user);
                    return true;
                })
                .orElse(false);
    }

    private boolean updateUserProperty(String login, java.util.function.Consumer<User> updater) {
        return userRepository.findByLogin(login)
                .map(user -> {
                    updater.accept(user);
                    userRepository.update(user);
                    return true;
                })
                .orElseGet(() -> {
                    ServerLogger.logServer(ServerLogger.Level.WARNING, "User not found!");
                    return false;
                });
    }

    public User updatePremiumStatus(String login) {
        return userRepository.findByLogin(login)
                .map(user -> {
                    user.setPremium(!user.isPremium());
                    userRepository.update(user);
                    return user;
                })
                .orElseGet(() -> {
                    ServerLogger.logServer(ServerLogger.Level.WARNING, "User not found!");
                    return null;
                });
    }
}