package org.main.unimapapi.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

/**
 * Utility class for hashing and verifying passwords using BCrypt.
 *
 * <p>This class provides static methods to hash plain text passwords and verify them against stored hashes.
 * It uses a default salt round of 12 for strong security.</p>
 */
@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Hashing {
    /**
     * Default number of salt rounds for BCrypt.
     */
    private static final int DEFAULT_SALT_ROUNDS = 12;

    /**
     * Hashes a plain text password using BCrypt with a predefined number of salt rounds.
     *
     * @param password the plain text password to hash
     * @return a hashed version of the password
     */
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(DEFAULT_SALT_ROUNDS));
    }

    /**
     * Compares a plain text password with a previously hashed password.
     *
     * @param plainPassword  the raw password provided by the user
     * @param hashedPassword the hashed password stored in the database
     * @return {@code true} if the passwords match, {@code false} otherwise
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}