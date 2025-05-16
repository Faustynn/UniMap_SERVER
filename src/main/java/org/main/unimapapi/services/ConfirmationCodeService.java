package org.main.unimapapi.services;

import org.main.unimapapi.entities.ConfirmationCode;
import org.main.unimapapi.repository_queries.ConfirmationCodeRepository;
import org.main.unimapapi.utils.ServerLogger;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.Random;

/**
 * Service responsible for generating, storing, and validating confirmation codes.
 *
 * <p>Used for user verification processes such as email confirmation and password reset.</p>
 */
@Service
@RequiredArgsConstructor
public class ConfirmationCodeService {
    private final ConfirmationCodeRepository confirmationCodeRepository;

    /**
     * Saves a confirmation code to the database.
     *
     * @param confirmationCode the confirmation code entity to store
     */
    public void save(ConfirmationCode confirmationCode) {
        confirmationCodeRepository.save(confirmationCode);
    }

    /**
     * Generates a random 6-digit numeric code as a {@link String}.
     *
     * @return six-digit confirmation code
     */
    public static String generateRandomCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    /**
     * Validates whether the given code matches the stored one for the user.
     * If valid, the code is deleted immediately (one-time use).
     *
     * @param userId the ID of the user
     * @param code   the code to validate
     * @return {@code true} if the code is valid, otherwise {@code false}
     */
    public boolean validateConfirmationCode(Long userId, String code) {
        try {
            ServerLogger.logServer(ServerLogger.Level.INFO, "VALIDATE CONFIRMATION CODE "+userId+" "+code);
            if (confirmationCodeRepository.find(userId, code)) {
                confirmationCodeRepository.deleteByUserId(userId, code);
                return true;
            }
            return false;
        } catch (Exception e) {
            ServerLogger.logServer(ServerLogger.Level.ERROR, "Error during confirmation code validation: " + e.getMessage());
            return false;
        }
    }


}