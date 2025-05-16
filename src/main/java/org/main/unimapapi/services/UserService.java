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

/**
 * Service responsible for user management operations.
 *
 * <p>Handles user creation, updates, password changes, avatar changes,
 * deletion of comments and account, and premium status toggling.</p>
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    /**
     * Creates a new user based on the DTO.
     *
     * @param dto user registration data
     * @return created {@link User}
     */
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

    /**
     * Retrieves all users from the system.
     *
     * @return list of all {@link User}
     */
    public List<User> getAll() {
        return userRepository.findAll();
    }

    /**
     * Updates user details.
     *
     * @param user user to update
     * @return updated {@link User}
     */
    public User update(User user) {
        userRepository.update(user);
        return user;
    }

    /**
     * Deletes user by ID.
     *
     * @param id user ID
     */
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * Finds a user by email.
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Finds a user by login.
     */
    public Optional<User> findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    /**
     * Finds a user by username.
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Deletes all user data, including account, comments, and confirmation codes.
     */
    public void deleteAllUserInfo(Long id) {
        userRepository.deleteAllUserInfo(id);
    }

    /**
     * Deletes only the comments of a user.
     */
    public void deleteAllUserComments(Long id) {
        userRepository.deleteAllUserComments(id);
    }

    /**
     * Updates the avatar image and file name of a user by login.
     */
    public boolean updateAvatarData(String login, byte[] avatarBinary, String fileName) {
        return updateUserProperty(login, user -> {
            user.setAvatar(avatarBinary);
            user.setAvatarFileName(fileName);
            ServerLogger.logServer(ServerLogger.Level.INFO, "Avatar updated successfully for user: " + login);
        });
    }

    /**
     * Changes the user's email.
     */
    public boolean changeEmail(String login, String newEmail) {
        return updateUserProperty(login, user -> user.setEmail(newEmail));
    }

    /**
     * Changes the user's password based on email.
     */
    public boolean changePassword(String email, String newPassword) {
        return userRepository.findByEmail(email)
                .map(user -> {
                    user.setPassword(Hashing.hashPassword(newPassword));
                    userRepository.update(user);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Changes the user's username based on email.
     */
    public boolean changeUsername(String email, String newUsername) {
        return userRepository.findByEmail(email)
                .map(user -> {
                    user.setUsername(newUsername);
                    userRepository.update(user);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Toggles premium status of the user by login.
     *
     * @param login user login
     * @return updated {@link User} or {@code null} if user not found
     */
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

    /**
     * Internal utility for modifying a user property if they exist.
     *
     * @param login   user login
     * @param updater lambda to modify the user
     * @return {@code true} if update succeeded, otherwise {@code false}
     */
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